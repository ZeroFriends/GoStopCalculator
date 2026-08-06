package zero.friends.gostopcalculator.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.ads.MobileAds
import zero.friends.gostopcalculator.GoStopApplication
import zero.friends.gostopcalculator.navigation.AppNavigator
import zero.friends.gostopcalculator.util.log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) { status ->
            log.d("MobileAds.initialize completed. status=$status")
            (application as? GoStopApplication)?.androidAppComponent?.googleAdmob?.reload()
        }

        setContent {
            AppNavigator(
                onCloseApp = { finish() },
                onShowAd = { onCompleted ->
                    val admob = (application as? GoStopApplication)?.androidAppComponent?.googleAdmob
                    if (admob != null) {
                        admob.showAd(this@MainActivity) {
                            onCompleted()
                        }
                    } else {
                        log.e("googleAdmob instance is null in MainActivity!")
                        onCompleted()
                    }
                }
            )
        }
    }
}
