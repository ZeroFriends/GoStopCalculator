package zero.friends.gostopcalculator.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import zero.friends.gostopcalculator.Navigator
import zero.friends.gostopcalculator.theme.GoStopTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoStopTheme {
                Navigator {
                    finish()
                }
            }
        }
    }

}