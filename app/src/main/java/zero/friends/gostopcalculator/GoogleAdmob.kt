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
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

sealed interface AdCallback {
    object OnError : AdCallback
    object OnSuccess : AdCallback
    object LostNetwork : AdCallback
}

class AdException(override val message: String?) : Throwable()

class GoogleAdmob @Inject constructor(
    @ActivityContext private val context: Context,
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
        var isDebugAdEnabled: Boolean = false
        private var lastAdDismissedTime: Long = 0L
        private const val AD_COOLDOWN_MILLIS: Long = 10_000L
    }

    init {
        netWorkChecker(
            onAvailable = {
                isConnect = true
                reload()
            },
            onLost = {
                cancelRetry()
                interstitialAd = null
                isConnect = false
            }
        )
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
                    Timber.tag("🔥GoogleAdmob").e("onAdFailedToLoad: $adError")
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
                    Timber.tag("🔥GoogleAdmob").d("onAdLoaded Success")
                }
            }
        )
    }

    fun showAd(adCallback: (AdCallback) -> Unit) {
        if (BuildConfig.DEBUG && !isDebugAdEnabled) {
            adCallback(AdCallback.OnSuccess)
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAdDismissedTime < AD_COOLDOWN_MILLIS) {
            adCallback(AdCallback.OnSuccess)
            return
        }

        val currentAd = interstitialAd

        if (currentAd != null) {
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    lastAdDismissedTime = System.currentTimeMillis()
                    interstitialAd = null
                    adCallback(AdCallback.OnSuccess)
                    reload()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    interstitialAd = null
                    Timber.tag("🔥GoogleAdmob").e("onAdFailedToShow: ${adError.message}")
                    FirebaseCrashlytics.getInstance().recordException(
                        AdException("Failed to show ad: ${adError.message}")
                    )
                    adCallback(AdCallback.OnError)
                    reload()
                }
            }
            val activity = context as? Activity
            if (activity != null && !activity.isFinishing && !activity.isDestroyed) {
                currentAd.show(activity)
            } else {
                adCallback(AdCallback.OnError)
                reload()
            }
        } else {
            if (isConnect) {
                adCallback(AdCallback.OnError)
            } else {
                adCallback(AdCallback.LostNetwork)
            }
            reload()
        }
    }
}
