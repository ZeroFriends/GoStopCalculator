package zero.friends.gostopcalculator

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import zero.friends.domain.util.Const
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

sealed interface Navigate {

    fun route() = requireNotNull(this::class.qualifiedName)

    /**
     * @param argument : 실제 데이터를 넣어 route 할 때 사용 Navigate/1
     * @sample
     * navController.navigate(argument = Navigate.Board.Main.route(argument = game.id))
     */
    fun route(vararg argument: Any) = route() + argument.joinToString("") { "/$it" }

    /**
     * @param path : 목적지 path 를 route 할 때 사용 Navigate/{someData}
     * @sample
     * composable(
     *      route = Navigate.Board.Main.route(path = Const.GameId)
     * )
     */
    fun destination(vararg path: Any) = route() + path.joinToString("") { "/{$it}" }

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
    showAds: (endAds) -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Navigate.Splash.route()) {
        composable(Navigate.Splash.destination()) {
            SplashScreen()
            LaunchedEffect(true) {
                delay(1500)
                navController.navigate(Navigate.History.route())
            }
        }
        composable(Navigate.History.destination()) {
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
        composable(Navigate.Precondition.Player.destination()) {
            PlayerScreen(
                onNext = { navController.navigate(Navigate.Precondition.Rule.route()) },
                onBack = { navController.navigateUp() }
            )
        }

        composable(Navigate.Precondition.Rule.destination()) {
            RuleScreen(
                onNext = { game ->
                    navController.navigate(Navigate.Board.Main.route(game.id))
                },
                onBack = { navController.navigateUp() },
            )
        }

        //Board
        composable(
            route = Navigate.Board.Main.destination(Const.GameId),
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
                openDetailScreen = { roundId ->
                    navController.navigate(Navigate.Board.Detail.route(roundId))
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

        composable(Navigate.Board.Prepare.destination()) {
            PrepareScreen(
                onComplete = {
                    navController.navigate(Navigate.Board.Score.route())
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable(Navigate.Board.Score.destination()) {
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

        composable(Navigate.Board.End.destination()) {
            EndScreen(
                onComplete = { gameId ->
                    showAds {
                        navController.popBackStack()
                        navController.navigate(Navigate.Board.Main.route(gameId))
                    }
                }
            )
        }

        composable(
            route = Navigate.Board.Detail.destination(Const.RoundId),
            arguments = listOf(navArgument(Const.RoundId) {
                type = NavType.LongType
                nullable = false
            })
        ) {
            DetailScreen(
                onBack = { navController.navigateUp() }
            )
        }

        composable(Navigate.Board.Calculate.destination()) {
            CalculateScreen(onBack = { navController.navigateUp() })
        }

        composable(Navigate.Board.Rule.destination()) {
            RuleLogScreen(onBack = { navController.navigateUp() })
        }

    }

}

