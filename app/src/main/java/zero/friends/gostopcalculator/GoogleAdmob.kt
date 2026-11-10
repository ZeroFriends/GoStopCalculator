package zero.friends.gostopcalculator

import android.app.Activity
import android.content.Context
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
import zero.friends.gostopcalculator.util.GoogleAdmob as SharedGoogleAdmob

sealed interface AdCallback {
    object OnError : AdCallback
    object OnSuccess : AdCallback
    object LostNetwork : AdCallback
}

class AdException(override val message: String?) : Throwable()

@Inject
class GoogleAdmob(
    private val context: Context,
    netWorkChecker: NetWorkChecker
) {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private var retryJob: Job? = null
    private var retryCount = 0
    private val maxRetryCount = 3

    private var interstitialAd: InterstitialAd? = null
    private var isConnect = false
    private var isLoading = false

    companion object {
        var isDebugAdEnabled: Boolean
            get() = SharedGoogleAdmob.isDebugAdEnabled
            set(value) {
                SharedGoogleAdmob.isDebugAdEnabled = value
            }
        private var lastAdDismissedTime: Long = 0L
        private const val AD_COOLDOWN_MILLIS: Long = 10_000L
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

    fun showAd(activity: Activity, adCallback: (AdCallback) -> Unit) {
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
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    log.d("onAdDismissedFullScreenContent: User closed ad.")
                    lastAdDismissedTime = System.currentTimeMillis()
                    interstitialAd = null
                    adCallback(AdCallback.OnSuccess)
                    reload()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    interstitialAd = null
                    log.e("onAdFailedToShow: code=${adError.code}, message=${adError.message}")
                    FirebaseCrashlytics.getInstance().recordException(
                        AdException("Failed to show ad: ${adError.message}")
                    )
                    adCallback(AdCallback.OnError)
                    reload()
                }
            }
            if (!activity.isFinishing && !activity.isDestroyed) {
                log.d("Showing ad on ${activity.localClassName}")
                currentAd.show(activity)
            } else {
                log.e("Activity is finishing or destroyed: isFinishing=${activity.isFinishing}, isDestroyed=${activity.isDestroyed}")
                adCallback(AdCallback.OnError)
                reload()
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
