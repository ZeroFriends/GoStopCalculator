package zero.friends.gostopcalculator.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import zero.friends.gostopcalculator.AdCallback
import zero.friends.gostopcalculator.GoogleAdmob
import zero.friends.gostopcalculator.Navigator
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.theme.GoStopTheme
import zero.friends.gostopcalculator.util.getEntryPointFromActivity
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var googleAdmob: GoogleAdmob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GoStopTheme {
                Navigator(
                    onBackPressed = {
                        finish()
                    },
                    showAds = { endAds ->
                        googleAdmob.showAd(
                            adCallback = { adCallback ->
                                when (adCallback) {
                                    AdCallback.OnError -> {
                                        Toast.makeText(this, getString(R.string.ad_error), Toast.LENGTH_SHORT).show()
                                        endAds()
                                    }
                                    AdCallback.OnSuccess -> endAds()
                                }
                            },
                        )
                    },
                    entryPoint = getEntryPointFromActivity(),
                )
            }
        }

    }

}