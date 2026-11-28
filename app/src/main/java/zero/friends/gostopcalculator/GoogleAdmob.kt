package zero.friends.gostopcalculator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

internal fun shouldShowOfflineGate(
    isDebugBuild: Boolean,
    isDebugAdEnabled: Boolean,
    isInCooldown: Boolean,
    hasReadyAd: Boolean,
    isNetworkConnected: Boolean,
): Boolean {
    if (isDebugBuild && !isDebugAdEnabled) return false
    if (isInCooldown) return false
    if (hasReadyAd) return false
    return !isNetworkConnected
}

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

internal fun isCooldownWindowActive(
    lastMarkedElapsedMs: Long,
    nowElapsedMs: Long,
    cooldownMillis: Long,
): Boolean = lastMarkedElapsedMs > 0L && (nowElapsedMs - lastMarkedElapsedMs) in 0L until cooldownMillis

@Inject
class GoogleAdmob(
    private val context: Context,
    private val netWorkChecker: NetWorkChecker
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
    private var activeShowingAd: InterstitialAd? = null
    private var isConnect = false
    private var isLoading = false
    private var isShowingAd = false
    private var activeShowSessionId: Long = 0L
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
        private var lastAdCooldownMarkedAtElapsedMs: Long = 0L
        private const val AD_COOLDOWN_MILLIS: Long = 10_000L
        private const val AD_SHOW_WATCHDOG_MILLIS: Long = 3_000L
        private const val AD_RESUME_RECOVERY_MILLIS: Long = 500L
    }

    init {
        isConnect = netWorkChecker.isCurrentlyConnected()

        netWorkChecker { connected ->
            isConnect = connected
            if (connected) {
                retryCount = 0
                if (interstitialAd == null) {
                    reload()
                }
            } else {
                cancelRetry()
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
                val sessionId = activeShowSessionId
                if (isShowingAd && activity === showingActivityRef?.get() && adActivityPausedHost && didShowFullScreen) {
                    resumeRecoveryJob?.cancel()
                    resumeRecoveryJob = scope.launch {
                        delay(AD_RESUME_RECOVERY_MILLIS)
                        val host = showingActivityRef?.get()
                        if (isShowingAd && sessionId == activeShowSessionId && host != null && isActivityForegroundEligible(host)) {
                            log.e("Ad closed without callback after host resumed; recovering ad state.")
                            markCooldownNow()
                            completeShowingSession(
                                sessionId = sessionId,
                                result = AdCallback.OnError,
                                reloadAfter = true,
                                invokeCallback = true,
                            )
                        }
                    }
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (isShowingAd && activity === showingActivityRef?.get()) {
                    completeShowingSession(
                        sessionId = activeShowSessionId,
                        result = AdCallback.OnError,
                        reloadAfter = false,
                        invokeCallback = false,
                    )
                }
            }
        })
    }

    private fun isCooldownActive(): Boolean = isCooldownWindowActive(
        lastMarkedElapsedMs = lastAdCooldownMarkedAtElapsedMs,
        nowElapsedMs = SystemClock.elapsedRealtime(),
        cooldownMillis = AD_COOLDOWN_MILLIS,
    )

    private fun markCooldownNow() {
        lastAdCooldownMarkedAtElapsedMs = SystemClock.elapsedRealtime()
    }

    fun markOfflineGateContinued() {
        markCooldownNow()
    }

    fun shouldGateOfflineAd(): Boolean {
        return shouldShowOfflineGate(
            isDebugBuild = BuildConfig.DEBUG,
            isDebugAdEnabled = isDebugAdEnabled,
            isInCooldown = isCooldownActive(),
            hasReadyAd = interstitialAd != null,
            isNetworkConnected = netWorkChecker.isCurrentlyConnected(),
        )
    }

    fun isNetworkConnected(): Boolean = netWorkChecker.isCurrentlyConnected()

    private fun cancelRetry() {
        retryJob?.cancel()
        retryJob = null
    }

    private fun recordAdExceptionSafely(message: String, cause: Throwable? = null) {
        runCatching {
            FirebaseCrashlytics.getInstance().recordException(AdException(message, cause))
        }
    }

    fun reload() {
        val connected = netWorkChecker.isCurrentlyConnected()
        isConnect = connected
        if (isLoading || interstitialAd != null || !connected) return
        isLoading = true

        val adUnitId = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/1033173712"
        } else {
            BuildConfig.AD_UNIT_ID
        }

        try {
            InterstitialAd.load(
                context,
                adUnitId,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        isLoading = false
                        interstitialAd = null
                        log.e("onAdFailedToLoad: code=${adError.code}, message=${adError.message}")
                        if (adError.code != AdRequest.ERROR_CODE_NETWORK_ERROR &&
                            adError.code != AdRequest.ERROR_CODE_NO_FILL
                        ) {
                            recordAdExceptionSafely(
                                "AD_UNIT_ID = $adUnitId / code : ${adError.code} / ${adError.message}"
                            )
                        }

                        cancelRetry()
                        if (retryCount < maxRetryCount && netWorkChecker.isCurrentlyConnected()) {
                            retryCount++
                            retryJob = scope.launch {
                                delay(5000L)
                                if (netWorkChecker.isCurrentlyConnected() && interstitialAd == null) {
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
        } catch (e: CancellationException) {
            isLoading = false
            throw e
        } catch (e: Exception) {
            isLoading = false
            log.e("Exception while requesting interstitial ad load: ${e.message}")
            recordAdExceptionSafely("Exception while requesting interstitial ad load: ${e.message}", e)
        }
    }

    private fun completeShowingSession(
        sessionId: Long,
        result: AdCallback,
        reloadAfter: Boolean,
        invokeCallback: Boolean = true,
    ) {
        if (!isShowingAd || sessionId != activeShowSessionId) return

        showWatchdogJob?.cancel()
        showWatchdogJob = null
        resumeRecoveryJob?.cancel()
        resumeRecoveryJob = null
        activeShowingAd?.fullScreenContentCallback = null
        activeShowingAd = null
        isShowingAd = false
        didShowFullScreen = false
        adActivityPausedHost = false
        showingActivityRef = null
        val callback = pendingAdCallback
        pendingAdCallback = null

        if (invokeCallback) {
            callback?.invoke(result)
        }
        if (reloadAfter && netWorkChecker.isCurrentlyConnected()) {
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
            completeShowingSession(
                sessionId = activeShowSessionId,
                result = AdCallback.OnError,
                reloadAfter = false,
                invokeCallback = false,
            )
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

        if (isCooldownActive()) {
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

            val sessionId = ++activeShowSessionId
            interstitialAd = null
            activeShowingAd = currentAd
            isShowingAd = true
            didShowFullScreen = false
            adActivityPausedHost = false
            showingActivityRef = WeakReference(activity)
            pendingAdCallback = adCallback

            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    if (!isShowingAd || sessionId != activeShowSessionId) return
                    didShowFullScreen = true
                    val host = showingActivityRef?.get()
                    if (host != null && !isActivityForegroundEligible(host)) {
                        adActivityPausedHost = true
                    }
                    showWatchdogJob?.cancel()
                    showWatchdogJob = null
                    log.d("onAdShowedFullScreenContent: Ad presented.")
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    if (!isShowingAd || sessionId != activeShowSessionId) return
                    log.d("onAdDismissedFullScreenContent: User closed ad.")
                    markCooldownNow()
                    completeShowingSession(
                        sessionId = sessionId,
                        result = AdCallback.OnSuccess,
                        reloadAfter = true,
                        invokeCallback = true,
                    )
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    if (!isShowingAd || sessionId != activeShowSessionId) return
                    log.e("onAdFailedToShow: code=${adError.code}, message=${adError.message}")
                    recordAdExceptionSafely("Failed to show ad: ${adError.message}")
                    completeShowingSession(
                        sessionId = sessionId,
                        result = AdCallback.OnError,
                        reloadAfter = true,
                        invokeCallback = true,
                    )
                }
            }
            showWatchdogJob?.cancel()
            showWatchdogJob = scope.launch {
                delay(AD_SHOW_WATCHDOG_MILLIS)
                if (isShowingAd && sessionId == activeShowSessionId && !didShowFullScreen) {
                    val host = showingActivityRef?.get()
                    val hostForeground = host != null && isActivityForegroundEligible(host)
                    log.e("Ad show watchdog expired without presentation; recovering state (hostForeground=$hostForeground).")
                    completeShowingSession(
                        sessionId = sessionId,
                        result = AdCallback.OnError,
                        reloadAfter = true,
                        invokeCallback = hostForeground,
                    )
                }
            }
            log.d("Showing ad on ${activity.localClassName}")
            try {
                currentAd.show(activity)
            } catch (e: CancellationException) {
                completeShowingSession(
                    sessionId = sessionId,
                    result = AdCallback.OnError,
                    reloadAfter = false,
                    invokeCallback = false,
                )
                throw e
            } catch (e: Exception) {
                log.e("Exception while showing ad: ${e.message}")
                recordAdExceptionSafely("Exception while showing ad: ${e.message}", e)
                completeShowingSession(
                    sessionId = sessionId,
                    result = AdCallback.OnError,
                    reloadAfter = true,
                    invokeCallback = true,
                )
            }
        } else {
            val connected = netWorkChecker.isCurrentlyConnected()
            isConnect = connected
            log.e("No ad ready to show! isConnect=$connected")
            if (connected) {
                adCallback(AdCallback.OnError)
                reload()
            } else {
                adCallback(AdCallback.LostNetwork)
            }
        }
    }
}
