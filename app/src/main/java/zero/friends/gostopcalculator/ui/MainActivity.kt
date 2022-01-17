package zero.friends.gostopcalculator.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import zero.friends.gostopcalculator.Navigator
import zero.friends.gostopcalculator.theme.GoStopTheme
import zero.friends.gostopcalculator.util.GoogleAdmob
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
                        googleAdmob.loadAds(endAds)
                    },
                )
            }
        }

    }

}