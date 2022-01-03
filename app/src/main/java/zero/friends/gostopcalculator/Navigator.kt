package zero.friends.gostopcalculator

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import zero.friends.domain.util.Const
import zero.friends.domain.util.Const.RoundId
import zero.friends.gostopcalculator.ui.board.main.BoardScreen
import zero.friends.gostopcalculator.ui.board.main.createBoardViewModel
import zero.friends.gostopcalculator.ui.board.prepare.PrepareScreen
import zero.friends.gostopcalculator.ui.board.result.CalculateScreen
import zero.friends.gostopcalculator.ui.board.result.DetailScreen
import zero.friends.gostopcalculator.ui.board.result.createDetailViewModel
import zero.friends.gostopcalculator.ui.board.score.ScoreScreen
import zero.friends.gostopcalculator.ui.board.score.end.EndScreen
import zero.friends.gostopcalculator.ui.history.HistoryScreen
import zero.friends.gostopcalculator.ui.precondition.PlayerScreen
import zero.friends.gostopcalculator.ui.precondition.RuleScreen
import zero.friends.gostopcalculator.ui.splash.SplashScreen

sealed interface Navigate {
    fun route() = findRoute()

    private fun findRoute() =
        (this::class.supertypes.first().toString().split(".").last() + "_" + this::class.simpleName)

    object History : Navigate
    object Splash : Navigate

    sealed interface Precondition : Navigate {
        object Player : Precondition
        object Rule : Precondition
    }

    sealed interface Board : Navigate {
        object Main : Board
        object Prepare : Board
        object Score : Board
        object End : Board

        object Detail : Board
        object Calculate : Board
    }
}

@Composable
fun Navigator(onBackPressed: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Navigate.Splash.route()) {
        composable(Navigate.Splash.route()) {
            SplashScreen()
            LaunchedEffect(true) {
                delay(1200)
                navController.navigate(Navigate.History.route()) {
                    popUpTo(Navigate.Splash.route())
                }
            }
        }
        composable(Navigate.History.route()) {
            HistoryScreen(
                onStartGame = {
                    navController.navigate(Navigate.Precondition.Player.route())
                },
                onShowGame = {
                    navController.putLong(Const.GameId, it.id)
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
                    navController.navigate(Navigate.Board.Prepare.route())
                },
                onBack = {
                    navController.popBackStack()
                    navController.navigate(Navigate.History.route())
                },
                openDetailScreen = {
                    navController.putLong(RoundId, it)
                    navController.navigate(Navigate.Board.Detail.route())
                },
                openCalculated = {
                    navController.navigate(Navigate.Board.Calculate.route())
                }
            )
        }

        composable(Navigate.Board.Prepare.route()) {
            PrepareScreen(
                onComplete = {
                    navController.navigate(Navigate.Board.Score.route())
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable(Navigate.Board.Score.route()) {
            ScoreScreen(
                onBack = {
                    navController.navigateUp()
                },
                onComplete = {
                    navController.navigate(Navigate.Board.End.route())
                }
            )
        }

        composable(Navigate.Board.End.route()) {
            EndScreen(
                onBack = {
                    navController.popBackStack()
                    navController.putLong(Const.GameId, it)
                    navController.navigate(Navigate.Board.Main.route())
                },
                onComplete = {
                    navController.popBackStack()
                    navController.putLong(Const.GameId, it)
                    navController.navigate(Navigate.Board.Main.route())
                }
            )
        }

        composable(Navigate.Board.Detail.route()) {
            val roundId = navController.getLong(RoundId)
            DetailScreen(createDetailViewModel(roundId = roundId), onBack = { navController.navigateUp() })
        }

        composable(Navigate.Board.Calculate.route()) {
            CalculateScreen(onBack = { navController.navigateUp() })
        }

    }

}

private fun NavHostController.putLong(key: String, value: Long) {
    currentBackStackEntry?.arguments?.putLong(key, value)
}

private fun NavHostController.getLong(key: String): Long {
    return requireNotNull(previousBackStackEntry?.arguments?.getLong(key))
}