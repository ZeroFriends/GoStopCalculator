package zero.friends.gostopcalculator.ui.splash

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.ExperimentalUnitApi
import dagger.hilt.android.AndroidEntryPoint
import zero.friends.gostopcalculator.Navigator

@AndroidEntryPoint
@ExperimentalUnitApi
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navigator {
                finish()
            }
        }
    }

}