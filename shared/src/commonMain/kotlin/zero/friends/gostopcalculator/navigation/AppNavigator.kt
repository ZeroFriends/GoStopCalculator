package zero.friends.gostopcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import zero.friends.gostopcalculator.ui.splash.SplashScreen

@Composable
fun AppNavigator() {
    val navController = rememberNavController(startDestination = Route.Splash)
    NavHost(
        navController = navController
    ) {
        composable<Route.Splash> {
            SplashScreen()
            LaunchedEffect(Unit) {
                delay(1500)
                navController.navigate(Route.History) {
                    popUpTo<Route.Splash> { inclusive = true }
                }
            }
        }

        composable<Route.History> {
            // Placeholder until HistoryScreen migration
        }
    }
}
