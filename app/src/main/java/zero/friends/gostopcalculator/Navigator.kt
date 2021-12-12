package zero.friends.gostopcalculator

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import zero.friends.gostopcalculator.theme.GoStopTheme
import zero.friends.gostopcalculator.ui.board.BoardScreen
import zero.friends.gostopcalculator.ui.board.PrepareScreen
import zero.friends.gostopcalculator.ui.board.createBoardViewModel
import zero.friends.gostopcalculator.ui.board.createPrepareViewModel
import zero.friends.gostopcalculator.ui.main.MainScreen
import zero.friends.gostopcalculator.ui.precondition.PlayerScreen
import zero.friends.gostopcalculator.ui.precondition.RuleScreen
import zero.friends.gostopcalculator.ui.splash.SplashScreen

sealed interface Navigate {
    fun route() = findRoute()

    private fun findRoute() =
        (this::class.supertypes.first().toString().split(".").last() + "_" + this::class.simpleName)

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
                SplashScreen()
                LaunchedEffect(true) {
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
                        navController.currentBackStackEntry?.arguments?.putLong(Const.GameId, it.id)
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
                        navController.putGameId(it.id)
                        navController.navigate(Navigate.Board.Main.route())
                    },
                    onBack = { navController.navigateUp() },
                )
            }

            composable(Navigate.Board.Main.route()) {
                val gameId = navController.getGameId()
                BoardScreen(
                    createBoardViewModel(gameId),
                    onNext = {
                        navController.putGameId(it)
                        navController.navigate(Navigate.Board.Prepare.route())
                    },
                    onBack = {
                        navController.popBackStack()
                        navController.navigate(Navigate.Main.route())
                    }
                )
            }

            composable(Navigate.Board.Prepare.route()) {
                val gameId = navController.getGameId()
                PrepareScreen(createPrepareViewModel(gameId = gameId))
            }

        }
    }
}

private fun NavHostController.putGameId(gameId: Long) {
    currentBackStackEntry?.arguments?.putLong(Const.GameId, gameId)
}

private fun NavHostController.getGameId(): Long {
    return requireNotNull(previousBackStackEntry?.arguments?.getLong(Const.GameId))
}