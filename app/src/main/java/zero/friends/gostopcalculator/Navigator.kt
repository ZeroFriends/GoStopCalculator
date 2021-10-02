package zero.friends.gostopcalculator

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zero.friends.gostopcalculator.main.MainScreen
import zero.friends.gostopcalculator.splash.SplashScreen

@Composable
fun Navigator() {
    MaterialTheme {
        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()

        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") {
                SplashScreen {
                    coroutineScope.launch {
                        delay(1000)
                        navController.navigate("main")
                    }
                }
            }
            composable("main") {
                MainScreen()
                BackHandler(true) {
                    (navController.context as? Activity)?.finish()
                }
            }
        }
    }
}