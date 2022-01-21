package zero.friends.gostopcalculator.initailizer

import android.content.Context
import androidx.startup.Initializer
import com.google.android.gms.ads.MobileAds

class MobileAdsInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        MobileAds.initialize(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}