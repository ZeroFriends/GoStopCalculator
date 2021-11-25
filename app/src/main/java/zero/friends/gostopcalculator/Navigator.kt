package zero.friends.gostopcalculator

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.gostopcalculator.theme.GoStopTheme
import zero.friends.gostopcalculator.ui.board.BoardScreen
import zero.friends.gostopcalculator.ui.main.MainScreen
import zero.friends.gostopcalculator.ui.precondition.PlayerScreen
import zero.friends.gostopcalculator.ui.precondition.RuleScreen
import zero.friends.gostopcalculator.ui.splash.SplashScreen

sealed interface Navigate {
    fun route() = this::class.supertypes.toString() + this::class.simpleName

    object Main : Navigate
    object Splash : Navigate

    sealed interface Precondition : Navigate {
        object Player : Precondition
        object Load : Precondition
        object Rule : Precondition
    }

    sealed interface Board : Navigate {
        object Main : Board
        object Prepare : Board
        object Selling : Board
        object Score : Board
        object Winner : Board
        object Loser : Board
        object End : Board

        sealed interface Setting : Board {
            object Rule : Setting
            object AddRule : Setting
            object Player : Setting
        }

        object Detail : Board
        object Calculate : Board
    }
}

@Composable
fun Navigator(onBackPressed: () -> Unit) {
    GoStopTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Navigate.Splash.route()) {
            composable(Navigate.Splash.route()) {
                val splashScope = rememberCoroutineScope()
                SplashScreen()
                splashScope.launch {
                    delay(500)
                    navController.navigate(Navigate.Main.route()) {
                        popUpTo(Navigate.Splash.route())
                    }
                }
            }
            composable(Navigate.Main.route()) {
                MainScreen(
                    onStartGame = {
                        navController.navigate(Navigate.Precondition.Player.route())
                    },
                    onShowGame = {
                        navController.currentBackStackEntry?.arguments?.putSerializable("game", it)
                        navController.navigate(Navigate.Board.Main.route())
                    }
                )

                BackHandler(true) {
                    onBackPressed()
                }
            }

            composable(Navigate.Precondition.Player.route()) {
                PlayerScreen(
                    onNext = { navController.navigate(Navigate.Precondition.Rule.route()) },
                    onBack = { navController.navigateUp() }
                )
            }

            composable(Navigate.Precondition.Rule.route()) {
                RuleScreen(
                    onNext = {
                        navController.currentBackStackEntry?.arguments?.putSerializable("game", it)
                        navController.navigate(Navigate.Board.Main.route())
                    },
                    onBack = { navController.navigateUp() },
                )
            }

            composable(Navigate.Board.Main.route()) {
                val game = navController.previousBackStackEntry?.arguments?.getSerializable("game") as Game
                BoardScreen(game, onBack = {
                    navController.popBackStack()
                    navController.navigate(Navigate.Main.route())
                })
            }
        }
    }
}