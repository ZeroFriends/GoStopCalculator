package zero.friends.gostopcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.di.AppComponent
import zero.friends.gostopcalculator.di.createAppComponent
import zero.friends.gostopcalculator.ui.board.main.BoardScreen
import zero.friends.gostopcalculator.ui.board.main.BoardViewModel
import zero.friends.gostopcalculator.ui.board.prepare.PrepareScreen
import zero.friends.gostopcalculator.ui.board.prepare.PrepareViewModel
import zero.friends.gostopcalculator.ui.board.rule.RuleLogScreen
import zero.friends.gostopcalculator.ui.board.rule.RuleLogViewModel
import zero.friends.gostopcalculator.ui.history.HistoryScreen
import zero.friends.gostopcalculator.ui.history.HistoryViewModel
import zero.friends.gostopcalculator.ui.precondition.PlayerScreen
import zero.friends.gostopcalculator.ui.precondition.PlayerViewModel
import zero.friends.gostopcalculator.ui.precondition.RuleScreen
import zero.friends.gostopcalculator.ui.precondition.RuleViewModel
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
                delay(3.seconds)
                navController.navigate(Route.History) {
                    popUpTo<Route.Splash> { inclusive = true }
                }
            }
        }

        composable<Route.History> {
            val historyViewModel: HistoryViewModel = viewModel { appComponent.historyViewModel }
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

        composable<Route.Precondition.Player> {
            val playerViewModel: PlayerViewModel = viewModel { appComponent.playerViewModel }
            PlayerScreen(
                viewModel = playerViewModel,
                onNext = { players, gameName ->
                    val playersJson = Json.encodeToString(players)
                    navController.navigate(Route.Precondition.Rule(playersJson = playersJson, gameName = gameName))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Precondition.Rule> {
            val route = requireNotNull(navController.currentRoute as? Route.Precondition.Rule)
            val players: List<Player> = runCatching {
                Json.decodeFromString<List<Player>>(route.playersJson)
            }.getOrDefault(emptyList())

            val ruleViewModel: RuleViewModel = viewModel(key = route.playersJson + route.gameName) {
                appComponent.ruleViewModelFactory(players, route.gameName)
            }

            RuleScreen(
                ruleViewModel = ruleViewModel,
                onNext = { gameId ->
                    navController.navigate(Route.Board.Main(gameId)) {
                        popUpTo<Route.History> { inclusive = false }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Board.Main> {
            val route = requireNotNull(navController.currentRoute as? Route.Board.Main)

            val boardViewModel: BoardViewModel = viewModel(key = route.gameId.toString()) {
                appComponent.boardViewModelFactory(route.gameId)
            }

            BoardScreen(
                boardViewModel = boardViewModel,
                onNext = { gameId ->
                    navController.navigate(Route.Board.Prepare(gameId))
                },
                onBack = {
                    navController.popBackStack()
                },
                openDetailScreen = { gameId, roundId ->
                    navController.navigate(Route.Board.Detail(gameId, roundId))
                },
                openCalculated = { gameId ->
                    navController.navigate(Route.Board.Calculate(gameId))
                },
                openRule = { gameId ->
                    navController.navigate(Route.Board.RuleLog(gameId))
                }
            )
        }

        composable<Route.Board.Prepare> {
            val route = requireNotNull(navController.currentRoute as? Route.Board.Prepare)

            val prepareViewModel: PrepareViewModel = viewModel(key = route.gameId.toString()) {
                appComponent.prepareViewModelFactory(route.gameId)
            }

            PrepareScreen(
                prepareViewModel = prepareViewModel,
                onComplete = { gameId, roundId ->
                    navController.navigate(Route.Board.Score(gameId, roundId)) {
                        popUpTo<Route.Board.Main> { inclusive = false }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Board.RuleLog> {
            val route = requireNotNull(navController.currentRoute as? Route.Board.RuleLog)

            val ruleLogViewModel: RuleLogViewModel = viewModel(key = route.gameId.toString()) {
                appComponent.ruleLogViewModelFactory(route.gameId)
            }

            RuleLogScreen(
                ruleLogViewModel = ruleLogViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
