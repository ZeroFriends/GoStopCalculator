package zero.friends.gostopcalculator.util

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
}

class GoogleAdmob @Inject constructor(@ActivityContext private val context: Context) {
    private var mInterstitialAd: InterstitialAd? = null

    fun loadAds(adCallback: (AdCallback) -> Unit) {
        InterstitialAd.load(
            context,
            AdUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.tag("🔥zero:onAdFailedToLoad").e("$adError")
                    adCallback(AdCallback.OnError)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            }
        )

        mInterstitialAd?.let {
            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    adCallback(AdCallback.OnSuccess)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    Timber.tag("🔥GoogleAdmob").e(p0.message)
                    adCallback(AdCallback.OnError)
                }
            }
            it.show(context as Activity)
        }
    }

    companion object {
        private const val AdUnitId = "ca-app-pub-1663298612263181/4159961076"
        private const val TestAdUnitId = "ca-app-pub-3940256099942544/1033173712"
    }
}