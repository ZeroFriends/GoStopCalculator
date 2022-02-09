package zero.friends.gostopcalculator

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import zero.friends.domain.util.Const
import zero.friends.domain.util.Const.RoundId
import zero.friends.gostopcalculator.di.AssistedViewModelEntryPoint
import zero.friends.gostopcalculator.ui.board.main.BoardScreen
import zero.friends.gostopcalculator.ui.board.prepare.PrepareScreen
import zero.friends.gostopcalculator.ui.board.result.CalculateScreen
import zero.friends.gostopcalculator.ui.board.result.DetailScreen
import zero.friends.gostopcalculator.ui.board.rule.RuleLogScreen
import zero.friends.gostopcalculator.ui.board.score.ScoreScreen
import zero.friends.gostopcalculator.ui.board.score.end.EndScreen
import zero.friends.gostopcalculator.ui.history.HistoryScreen
import zero.friends.gostopcalculator.ui.precondition.PlayerScreen
import zero.friends.gostopcalculator.ui.precondition.RuleScreen
import zero.friends.gostopcalculator.ui.splash.SplashScreen
import zero.friends.gostopcalculator.util.viewModelFactory

sealed interface Navigate {

    fun route(): String = requireNotNull(this::class.qualifiedName)

    /**
     * @param path : 목적지 path 를 route 할 때 사용 Navigate/{someData}
     * @sample
     * composable(
     *      route = Navigate.Board.Main.route(path = Const.GameId)
     * )
     */
    fun route(path: String): String = route() + "/{${path}}"

    /**
     * @param argument : 실제 데이터를 넣어 route 할 때 사용 Navigate/1
     * @sample
     * navController.navigate(route = Navigate.Board.Main.route(argument = game.id))
     */
    fun route(argument: Any): String = route() + "/${argument}"

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

        object Rule : Board
        object Detail : Board
        object Calculate : Board
    }
}
typealias endAds = () -> Unit

@Composable
fun Navigator(
    onBackPressed: () -> Unit,
    showAds: (endAds) -> Unit,
    entryPoint: AssistedViewModelEntryPoint
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Navigate.Splash.route()) {
        composable(Navigate.Splash.route()) {
            SplashScreen()
            LaunchedEffect(true) {
                delay(1500)
                navController.navigate(Navigate.History.route())
            }
        }
        composable(Navigate.History.route()) {
            HistoryScreen(
                onStartGame = {
                    navController.navigate(Navigate.Precondition.Player.route())
                },
                onShowRound = { game ->
                    navController.navigate(route = Navigate.Board.Main.route(game.id))
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
                onNext = { game ->
                    navController.navigate(Navigate.Board.Main.route(game.id))
                },
                onBack = { navController.navigateUp() },
            )
        }

        //Board
        composable(
            route = Navigate.Board.Main.route(path = Const.GameId),
            arguments = listOf(navArgument(Const.GameId) {
                type = NavType.LongType
                nullable = false
            }),
        ) {
            BoardScreen(
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
                    showAds {
                        navController.navigate(Navigate.Board.Calculate.route())
                    }
                },
                openRule = {
                    navController.navigate(Navigate.Board.Rule.route())
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
                },
                Exit = { gameId ->
                    navController.popBackStack()
                    navController.navigate(Navigate.Board.Main.route(gameId))
                }
            )
        }

        composable(Navigate.Board.End.route()) {
            EndScreen(
                onComplete = { gameId ->
                    showAds {
                        navController.popBackStack()
                        navController.navigate(Navigate.Board.Main.route(gameId))
                    }
                }
            )
        }

        composable(Navigate.Board.Detail.route()) {
            val roundId = navController.getLong(RoundId)
            DetailScreen(
                viewModel(factory = viewModelFactory { entryPoint.detailFactory().create(roundId) }),
                onBack = { navController.navigateUp() }
            )
        }

        composable(Navigate.Board.Calculate.route()) {
            CalculateScreen(onBack = { navController.navigateUp() })
        }

        composable(Navigate.Board.Rule.route()) {
            RuleLogScreen(onBack = { navController.navigateUp() })
        }

    }

}

private fun NavHostController.putLong(key: String, value: Long) {
    currentBackStackEntry?.arguments?.putLong(key, value)
}

private fun NavHostController.getLong(key: String): Long {
    return requireNotNull(previousBackStackEntry?.arguments?.getLong(key))
}

private fun NavHostController.putBoolean(key: String, value: Boolean) {
    currentBackStackEntry?.arguments?.putBoolean(key, value)
}

private fun NavHostController.getBoolean(key: String): Boolean {
    return requireNotNull(previousBackStackEntry?.arguments?.getBoolean(key))
}

