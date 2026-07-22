package zero.friends.gostopcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import zero.friends.gostopcalculator.di.AppComponent
import zero.friends.gostopcalculator.di.createAppComponent
import zero.friends.gostopcalculator.ui.history.HistoryScreen
import zero.friends.gostopcalculator.ui.splash.SplashScreen
import kotlin.time.Duration.Companion.seconds

@Composable
fun AppNavigator(
    appComponent: AppComponent = remember { createAppComponent() },
) {
    val navController = rememberNavController(startDestination = Route.Splash)
    NavHost(
        navController = navController
    ) {
        composable<Route.Splash> {
            SplashScreen()
            LaunchedEffect(Unit) {
                delay(2.seconds)
                navController.navigate(Route.History) {
                    popUpTo<Route.Splash> { inclusive = true }
                }
            }
        }

        composable<Route.History> {
            val historyViewModel = remember { appComponent.historyViewModel }
            HistoryScreen(
                viewModel = historyViewModel,
                onStartGame = {
                    navController.navigate(Route.Precondition.Player)
                },
                onShowRound = { game ->
                    navController.navigate(Route.Board.Main(game.id))
                }
            )
        }
    }
}
