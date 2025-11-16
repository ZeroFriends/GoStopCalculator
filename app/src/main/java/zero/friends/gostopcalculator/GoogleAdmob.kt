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
            BuildConfig.AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.tag("🔥zero:onAdFailedToLoad").e("$adError")
                    FirebaseCrashlytics.getInstance().recordException(
                        AdException("AD_UNIT_ID = ${BuildConfig.AD_UNIT_ID} / code : ${adError.code} / ${adError.message}")
                    )
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
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    Timber.tag("🔥GoogleAdmob").e(adError.message)
                    FirebaseCrashlytics.getInstance().recordException(
                        AdException("Failed to show ad: ${adError.message}")
                    )
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

}
