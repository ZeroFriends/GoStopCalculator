package zero.friends.gostopcalculator

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ActivityContext
import timber.log.Timber
import javax.inject.Inject

sealed interface AdCallback {
    object OnError : AdCallback
    object OnSuccess : AdCallback
    object LostNetwork : AdCallback
}

class GoogleAdmob @Inject constructor(
    @ActivityContext private val context: Context,
    netWorkChecker: NetWorkChecker
) {
    private var interstitialAd: InterstitialAd? = null

    private lateinit var adCallback: (AdCallback) -> Unit

    private var isConnect = false

    init {
        netWorkChecker(
            onAvailable = {
                isConnect = true
                reload()
            },
            onLost = {
                interstitialAd = null
                isConnect = false
            }
        )
    }

    private fun reload() {
        InterstitialAd.load(
            context,
            AdUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.tag("🔥zero:onAdFailedToLoad").e("$adError")
                    adCallback(AdCallback.OnError)
                    interstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    this@GoogleAdmob.interstitialAd = interstitialAd
                }
            }
        )
    }

    fun showAd(adCallback: (AdCallback) -> Unit) {
        val interstitialAd = interstitialAd

        if (interstitialAd != null) {
            interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    adCallback(AdCallback.OnSuccess)
                    this@GoogleAdmob.interstitialAd = null
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    Timber.tag("🔥GoogleAdmob").e(p0.message)
                    adCallback(AdCallback.OnError)
                }
            }
            interstitialAd.show(context as Activity)
        } else {
            if (isConnect) {
                adCallback(AdCallback.OnError)
            } else {
                adCallback(AdCallback.LostNetwork)
            }
        }

        reload()
    }

    companion object {
        private const val AdUnitId = "ca-app-pub-1663298612263181/4159961076"
        private const val TestAdUnitId = "ca-app-pub-3940256099942544/1033173712"
    }
}