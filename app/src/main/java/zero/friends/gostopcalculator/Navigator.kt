package zero.friends.gostopcalculator

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import zero.friends.gostopcalculator.theme.GoStopTheme
import zero.friends.gostopcalculator.ui.main.MainScreen
import zero.friends.gostopcalculator.ui.precondition.PlayerScreen
import zero.friends.gostopcalculator.ui.precondition.RuleScreen
import zero.friends.gostopcalculator.ui.splash.SplashScreen

sealed class Navigate(val route: String) {
    object Main : Navigate("main")
    object Splash : Navigate("splash")

    sealed class Precondition(route: String) : Navigate(route) {
        object Player : Precondition("precondition_player")
        object Load : Precondition("precondition_load")
        object Rule : Precondition("precondition_rule")
    }

    sealed class Board(route: String) : Navigate(route) {
        object Main : Board("board_main")
        object Prepare : Board("board_prepare")
        object Selling : Board("board_selling")
        object Score : Board("board_score")
        object Winner : Board("board_winner")
        object Loser : Board("board_loser")
        object End : Board("board_end")

        sealed class Setting(route: String) : Board(route) {
            object Rule : Setting("board_setting_rule")
            object AddRule : Setting("board_setting_add_rule")
            object Player : Setting("board_setting_player")
        }

        object Detail : Board("board_detail")
        object Calculate : Board("board_calculate")

    }


}

@Composable
fun Navigator(onBackPressed: () -> Unit) {
    GoStopTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Navigate.Splash.route) {
            composable(Navigate.Splash.route) {
                SplashScreen()
                LaunchedEffect(Unit) {
                    delay(500)
                    navController.navigate(Navigate.Main.route) {
                        popUpTo(Navigate.Splash.route)
                    }
                }
            }
            composable(Navigate.Main.route) {
                MainScreen(
                    onStartGame = {
                        navController.navigate(Navigate.Precondition.Player.route)
                    },
                    onShowGuide = {

                    }
                )
                BackHandler(true) {
                    onBackPressed()
                }
            }

            composable(Navigate.Precondition.Player.route) {
                PlayerScreen(
                    onNext = { navController.navigate(Navigate.Precondition.Rule.route) },
                    onBack = { navController.navigateUp() }
                )
            }

            composable(Navigate.Precondition.Rule.route) {
                RuleScreen(
                    onBack = { navController.navigateUp() }
                )
            }
        }
    }
}