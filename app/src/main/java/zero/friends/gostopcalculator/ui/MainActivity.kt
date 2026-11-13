package zero.friends.gostopcalculator.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.google.android.gms.ads.MobileAds
import zero.friends.gostopcalculator.GoStopApplication
import zero.friends.gostopcalculator.navigation.AppNavigator
import zero.friends.gostopcalculator.util.log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
            ),
        )

        MobileAds.initialize(this) { status ->
            log.d("MobileAds.initialize completed. status=$status")
            (application as? GoStopApplication)?.androidAppComponent?.googleAdmob?.reload()
        }

        setContent {
            Box(modifier = Modifier.semantics { testTagsAsResourceId = true }) {
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
}
