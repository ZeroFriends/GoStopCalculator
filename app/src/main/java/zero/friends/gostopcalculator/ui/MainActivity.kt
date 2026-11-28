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
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.friends.gostopcalculator.BuildConfig
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
                Color.argb(0x80, 0x1b, 0x1b, 0x1b),
            ),
        )

        val app = application as? GoStopApplication
        val appContext = applicationContext
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                MobileAds.initialize(appContext) { status ->
                    log.d("MobileAds.initialize completed. status=$status")
                    lifecycleScope.launch(Dispatchers.Main) {
                        app?.androidAppComponent?.googleAdmob?.reload()
                    }
                }
            }.onFailure { error ->
                log.e("MobileAds.initialize failed: ${error.message}")
            }
        }

        setContent {
            Box(modifier = Modifier.semantics { testTagsAsResourceId = true }) {
                val appComponent = app?.appComponent
                val showAd: ((() -> Unit) -> Unit) = { onCompleted ->
                    val admob = app?.androidAppComponent?.googleAdmob
                    if (admob != null) {
                        admob.showAd(this@MainActivity) {
                            onCompleted()
                        }
                    } else {
                        log.e("googleAdmob instance is null in MainActivity!")
                        onCompleted()
                    }
                }
                val shouldGateOfflineAd: () -> Boolean = {
                    app?.androidAppComponent?.googleAdmob?.shouldGateOfflineAd() == true
                }
                val onRetryNetwork: () -> Boolean = {
                    val admob = app?.androidAppComponent?.googleAdmob
                    val connected = admob?.isNetworkConnected() ?: true
                    if (connected) {
                        admob?.reload()
                    }
                    connected
                }
                val onContinueOffline: () -> Unit = {
                    app?.androidAppComponent?.googleAdmob?.markOfflineGateContinued()
                }
                if (appComponent != null) {
                    AppNavigator(
                        appComponent = appComponent,
                        showDebugAdToggle = BuildConfig.DEBUG,
                        onCloseApp = { finish() },
                        onShowAd = showAd,
                        shouldGateOfflineAd = shouldGateOfflineAd,
                        onRetryNetwork = onRetryNetwork,
                        onContinueOffline = onContinueOffline,
                    )
                } else {
                    AppNavigator(
                        showDebugAdToggle = BuildConfig.DEBUG,
                        onCloseApp = { finish() },
                        onShowAd = showAd,
                        shouldGateOfflineAd = shouldGateOfflineAd,
                        onRetryNetwork = onRetryNetwork,
                        onContinueOffline = onContinueOffline,
                    )
                }
            }
        }
    }
}
