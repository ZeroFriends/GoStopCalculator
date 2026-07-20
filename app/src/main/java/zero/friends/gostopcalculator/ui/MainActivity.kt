package zero.friends.gostopcalculator.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dagger.hilt.android.AndroidEntryPoint
import zero.friends.gostopcalculator.AdCallback
import zero.friends.gostopcalculator.GoogleAdmob
import zero.friends.gostopcalculator.Navigator
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.theme.GoStopTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var googleAdmob: GoogleAdmob

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)

        setContent {
            val isDebugAdEnabled = remember {
                mutableStateOf(GoogleAdmob.isDebugAdEnabled)
            }
            GoStopTheme {
                Navigator(
                    onBackPressed = {
                        finish()
                    },
                    isDebugAdEnabled = isDebugAdEnabled.value,
                    onToggleDebugAd = {
                        GoogleAdmob.isDebugAdEnabled = !GoogleAdmob.isDebugAdEnabled
                        isDebugAdEnabled.value = GoogleAdmob.isDebugAdEnabled
                    }
                ) { endAds ->
                    googleAdmob.showAd(
                        adCallback = { adCallback ->
                            when (adCallback) {
                                AdCallback.LostNetwork -> toast(getString(R.string.msg_network))
                                else -> endAds()
                            }
                        },
                    )
                }
            }
        }

    }

    private fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
