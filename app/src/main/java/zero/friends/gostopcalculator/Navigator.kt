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
import zero.friends.gostopcalculator.ui.board.createBoardViewModel
import zero.friends.gostopcalculator.ui.board.prepare.PrepareScreen
import zero.friends.gostopcalculator.ui.board.prepare.createPrepareViewModel
import zero.friends.gostopcalculator.ui.board.score.ScoreScreen
import zero.friends.gostopcalculator.ui.board.selling.SellingScreen
import zero.friends.gostopcalculator.ui.board.selling.createSellingViewModel
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
                    delay(1200)
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

            //PreCondition
            composable(Navigate.Precondition.Player.route()) {
                PlayerScreen(
                    onNext = { navController.navigate(Navigate.Precondition.Rule.route()) },
                    onBack = { navController.navigateUp() }
                )
            }

            composable(Navigate.Precondition.Rule.route()) {
                RuleScreen(
                    onNext = {
                        navController.putLong(Const.GameId, it.id)
                        navController.navigate(Navigate.Board.Main.route())
                    },
                    onBack = { navController.navigateUp() },
                )
            }

            //Board
            composable(Navigate.Board.Main.route()) {
                val gameId = navController.getLong(Const.GameId)
                BoardScreen(
                    createBoardViewModel(gameId),
                    onNext = {
                        navController.putLong(Const.GameId, it)
                        navController.navigate(Navigate.Board.Prepare.route())
                    },
                    onBack = {
                        navController.popBackStack()
                        navController.navigate(Navigate.Main.route())
                    }
                )
            }

            composable(Navigate.Board.Prepare.route()) {
                val gameId = navController.getLong(Const.GameId)
                PrepareScreen(
                    createPrepareViewModel(gameId = gameId),
                    onComplete = { skipSelling, roundId ->
                        if (skipSelling) navController.navigate(Navigate.Board.Score.route())
                        else {
                            navController.putLong(Const.RoundId, roundId)
                            navController.navigate(Navigate.Board.Selling.route())
                        }
                    },
                    onBack = { navController.navigateUp() }
                )
            }

            composable(Navigate.Board.Selling.route()) {
                val roundId = navController.getLong(Const.RoundId)
                SellingScreen(createSellingViewModel(roundId = roundId))
            }

            composable(Navigate.Board.Score.route()) {
                ScoreScreen()
            }

        }
    }
}

private fun NavHostController.putLong(key: String, value: Long) {
    currentBackStackEntry?.arguments?.putLong(key, value)
}

private fun NavHostController.getLong(key: String): Long {
    return requireNotNull(previousBackStackEntry?.arguments?.getLong(key))
}