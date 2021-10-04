package zero.friends.gostopcalculator

import androidx.activity.compose.BackHandler
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zero.friends.gostopcalculator.ui.main.MainScreen
import zero.friends.gostopcalculator.ui.precondition.PlayerScreen
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
    MaterialTheme {
        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
        val splashJob = Job()

        NavHost(navController = navController, startDestination = Navigate.Splash.route) {
            composable(Navigate.Splash.route) {
                SplashScreen {
                    coroutineScope.launch(splashJob) {
                        delay(500)
                        navController.navigate(Navigate.Main.route)
                    }
                }
            }
            composable(Navigate.Main.route) {
                splashJob.cancel()
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
                PlayerScreen { navController.navigateUp() }
            }
        }
    }
}