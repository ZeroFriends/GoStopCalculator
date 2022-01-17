package zero.friends.gostopcalculator.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class GoogleAdmob @Inject constructor(@ActivityContext private val context: Context) {
    private var mInterstitialAd: InterstitialAd? = null

    fun loadAds(endAdsCallback: () -> Unit) {
        InterstitialAd.load(
            context,
            AdUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
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
                    endAdsCallback()
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