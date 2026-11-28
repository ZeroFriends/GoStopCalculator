package zero.friends.gostopcalculator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.*
import me.tatarka.inject.annotations.Inject
import zero.friends.gostopcalculator.util.log
import java.lang.ref.WeakReference
import kotlin.coroutines.cancellation.CancellationException
import zero.friends.gostopcalculator.util.GoogleAdmob as SharedGoogleAdmob

sealed interface AdCallback {
    object OnError : AdCallback
    object OnSuccess : AdCallback
    object LostNetwork : AdCallback
}

class AdException(
    override val message: String?,
    override val cause: Throwable? = null,
) : Throwable(message, cause)

internal fun shouldRecoverStaleAdSession(
    isShowingAd: Boolean,
    trackedHostAlive: Boolean,
    sameHostAsCaller: Boolean,
): Boolean {
    if (!isShowingAd) return false
    return !trackedHostAlive || !sameHostAsCaller
}

internal fun isActivityEligibleForAd(
    isFinishing: Boolean,
    isDestroyed: Boolean,
    isAtLeastResumed: Boolean,
): Boolean = !isFinishing && !isDestroyed && isAtLeastResumed

@Inject
class GoogleAdmob(
    private val context: Context,
    netWorkChecker: NetWorkChecker
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        log.e("GoogleAdmob coroutine failed: ${throwable.message}")
    }
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob() + exceptionHandler)
    private var retryJob: Job? = null
    private var showWatchdogJob: Job? = null
    private var resumeRecoveryJob: Job? = null
    private var retryCount = 0
    private val maxRetryCount = 3

    private var interstitialAd: InterstitialAd? = null
    private var isConnect = false
    private var isLoading = false
    private var isShowingAd = false
    private var didShowFullScreen = false
    private var adActivityPausedHost = false
    private var showingActivityRef: WeakReference<Activity>? = null
    private var pendingAdCallback: ((AdCallback) -> Unit)? = null

    companion object {
        var isDebugAdEnabled: Boolean
            get() = SharedGoogleAdmob.isDebugAdEnabled
            set(value) {
                SharedGoogleAdmob.isDebugAdEnabled = value
            }
        private var lastAdDismissedTime: Long = 0L
        private const val AD_COOLDOWN_MILLIS: Long = 10_000L
        private const val AD_SHOW_WATCHDOG_MILLIS: Long = 3_000L
        private const val AD_RESUME_RECOVERY_MILLIS: Long = 500L
    }

    init {
        isConnect = netWorkChecker.isCurrentlyConnected()

        netWorkChecker { connected ->
            isConnect = connected
            if (connected) {
                if (interstitialAd == null) {
                    reload()
                }
            } else {
                cancelRetry()
                interstitialAd = null
            }
        }

        val application = context.applicationContext as? Application
        application?.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityStopped(activity: Activity) = Unit

            override fun onActivityPaused(activity: Activity) {
                if (isShowingAd && activity === showingActivityRef?.get()) {
                    adActivityPausedHost = true
                    resumeRecoveryJob?.cancel()
                    resumeRecoveryJob = null
                }
            }

            override fun onActivityResumed(activity: Activity) {
                if (isShowingAd && activity === showingActivityRef?.get() && adActivityPausedHost) {
                    resumeRecoveryJob?.cancel()
                    resumeRecoveryJob = scope.launch {
                        delay(AD_RESUME_RECOVERY_MILLIS)
                        val host = showingActivityRef?.get()
                        if (isShowingAd && host != null && isActivityForegroundEligible(host)) {
                            log.e("Ad closed without callback after host resumed; recovering ad state.")
                            completeShowingSession(AdCallback.OnError, reloadAfter = true)
                        }
                    }
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (isShowingAd && activity === showingActivityRef?.get() && activity.isFinishing) {
                    completeShowingSession(AdCallback.OnError, reloadAfter = false)
                }
            }
        })
    }

    private fun cancelRetry() {
        retryJob?.cancel()
        retryJob = null
    }

    fun reload() {
        if (isLoading || interstitialAd != null) return
        isLoading = true

        val adUnitId = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/1033173712"
        } else {
            BuildConfig.AD_UNIT_ID
        }

        InterstitialAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoading = false
                    interstitialAd = null
                    log.e("onAdFailedToLoad: code=${adError.code}, message=${adError.message}")
                    FirebaseCrashlytics.getInstance().recordException(
                        AdException("AD_UNIT_ID = $adUnitId / code : ${adError.code} / ${adError.message}")
                    )

                    cancelRetry()
                    if (retryCount < maxRetryCount) {
                        retryCount++
                        retryJob = scope.launch {
                            delay(5000L)
                            if (isConnect && interstitialAd == null) {
                                reload()
                            }
                        }
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    isLoading = false
                    retryCount = 0
                    cancelRetry()
                    interstitialAd = ad
                    log.d("onAdLoaded Success")
                }
            }
        )
    }

    private fun completeShowingSession(result: AdCallback, reloadAfter: Boolean) {
        showWatchdogJob?.cancel()
        showWatchdogJob = null
        resumeRecoveryJob?.cancel()
        resumeRecoveryJob = null
        isShowingAd = false
        didShowFullScreen = false
        adActivityPausedHost = false
        showingActivityRef = null
        val callback = pendingAdCallback
        pendingAdCallback = null
        callback?.invoke(result)
        if (reloadAfter) {
            reload()
        }
    }

    private fun isActivityForegroundEligible(activity: Activity): Boolean {
        val isAtLeastResumed = (activity as? LifecycleOwner)
            ?.lifecycle
            ?.currentState
            ?.isAtLeast(Lifecycle.State.RESUMED)
            ?: true
        return isActivityEligibleForAd(
            isFinishing = activity.isFinishing,
            isDestroyed = activity.isDestroyed,
            isAtLeastResumed = isAtLeastResumed,
        )
    }

    fun showAd(activity: Activity, adCallback: (AdCallback) -> Unit) {
        val trackedHost = showingActivityRef?.get()
        val trackedHostAlive = trackedHost != null && !trackedHost.isFinishing && !trackedHost.isDestroyed
        if (shouldRecoverStaleAdSession(
                isShowingAd = isShowingAd,
                trackedHostAlive = trackedHostAlive,
                sameHostAsCaller = trackedHost === activity,
            )
        ) {
            log.e("Recovering stale isShowingAd session from inactive/destroyed host activity.")
            completeShowingSession(AdCallback.OnError, reloadAfter = false)
        } else if (isShowingAd) {
            log.d("Skipping duplicate showAd call: ad is already showing.")
            return
        }

        val debugEnabled = isDebugAdEnabled

        if (BuildConfig.DEBUG && !debugEnabled) {
            log.d("Skipping ad: DEBUG mode and isDebugAdEnabled is FALSE.")
            adCallback(AdCallback.OnSuccess)
            return
        }

        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastAdDismissedTime

        if (elapsedTime < AD_COOLDOWN_MILLIS) {
            log.d("Skipping ad: Cooldown active.")
            adCallback(AdCallback.OnSuccess)
            return
        }

        val currentAd = interstitialAd

        if (currentAd != null) {
            if (!isActivityForegroundEligible(activity)) {
                log.e(
                    "Activity not eligible for ad: isFinishing=${activity.isFinishing}, isDestroyed=${activity.isDestroyed}"
                )
                adCallback(AdCallback.OnError)
                return
            }

            interstitialAd = null
            isShowingAd = true
            didShowFullScreen = false
            adActivityPausedHost = false
            showingActivityRef = WeakReference(activity)
            pendingAdCallback = adCallback

            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    didShowFullScreen = true
                    showWatchdogJob?.cancel()
                    showWatchdogJob = null
                    log.d("onAdShowedFullScreenContent: Ad presented.")
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    log.d("onAdDismissedFullScreenContent: User closed ad.")
                    lastAdDismissedTime = System.currentTimeMillis()
                    completeShowingSession(AdCallback.OnSuccess, reloadAfter = true)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    log.e("onAdFailedToShow: code=${adError.code}, message=${adError.message}")
                    FirebaseCrashlytics.getInstance().recordException(
                        AdException("Failed to show ad: ${adError.message}")
                    )
                    completeShowingSession(AdCallback.OnError, reloadAfter = true)
                }
            }
            showWatchdogJob?.cancel()
            showWatchdogJob = scope.launch {
                delay(AD_SHOW_WATCHDOG_MILLIS)
                if (isShowingAd && !didShowFullScreen && !adActivityPausedHost) {
                    log.e("Ad show watchdog expired without presentation; recovering state.")
                    completeShowingSession(AdCallback.OnError, reloadAfter = true)
                }
            }
            log.d("Showing ad on ${activity.localClassName}")
            try {
                currentAd.show(activity)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.e("Exception while showing ad: ${e.message}")
                FirebaseCrashlytics.getInstance().recordException(
                    AdException("Exception while showing ad: ${e.message}", e)
                )
                completeShowingSession(AdCallback.OnError, reloadAfter = true)
            }
        } else {
            log.e("No ad ready to show! isConnect=$isConnect")
            if (isConnect) {
                adCallback(AdCallback.OnError)
            } else {
                adCallback(AdCallback.LostNetwork)
            }
            reload()
        }
    }
}
