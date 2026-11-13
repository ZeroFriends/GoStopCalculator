package zero.friends.gostopcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.di.AppComponent
import zero.friends.gostopcalculator.di.createAppComponent
import zero.friends.gostopcalculator.theme.GoStopTheme
import zero.friends.gostopcalculator.ui.board.main.BoardScreen
import zero.friends.gostopcalculator.ui.board.main.BoardViewModel
import zero.friends.gostopcalculator.ui.board.prepare.PrepareScreen
import zero.friends.gostopcalculator.ui.board.prepare.PrepareViewModel
import zero.friends.gostopcalculator.ui.board.result.ResultScreen
import zero.friends.gostopcalculator.ui.board.result.ResultViewModel
import zero.friends.gostopcalculator.ui.board.result.ScreenType
import zero.friends.gostopcalculator.ui.board.rule.RuleLogScreen
import zero.friends.gostopcalculator.ui.board.rule.RuleLogViewModel
import zero.friends.gostopcalculator.ui.board.score.ScoreScreen
import zero.friends.gostopcalculator.ui.board.score.ScoreViewModel
import zero.friends.gostopcalculator.ui.board.score.manual.ManualScreen
import zero.friends.gostopcalculator.ui.board.score.manual.ManualViewModel
import zero.friends.gostopcalculator.ui.history.HistoryScreen
import zero.friends.gostopcalculator.ui.history.HistoryViewModel
import zero.friends.gostopcalculator.ui.precondition.PlayerScreen
import zero.friends.gostopcalculator.ui.precondition.PlayerViewModel
import zero.friends.gostopcalculator.ui.precondition.RuleScreen
import zero.friends.gostopcalculator.ui.precondition.RuleViewModel
import zero.friends.gostopcalculator.ui.splash.SplashScreen

fun NavController.safePopBackStack(onCloseApp: () -> Unit = {}) {
    if (!popBackStack()) {
        onCloseApp()
    }
}

@Composable
fun AppNavigator(
    appComponent: AppComponent = remember { createAppComponent() },
    onCloseApp: () -> Unit = {},
    onShowAd: (onCompleted: () -> Unit) -> Unit = { it() },
    onNativeNavigate: ((NativeNavCommand) -> Unit)? = null,
    onNativePop: (() -> Unit)? = null,
) {
    GoStopTheme {
        val navController = rememberNavController(
            startDestination = Route.Splash,
            onNativeNavigate = onNativeNavigate,
            onNativePop = onNativePop,
        )
        val navigateRoute: (Route, NativePopTarget, Boolean) -> Unit = { targetRoute, popTarget, inclusive ->
            navController.navigate(targetRoute) {
                when (popTarget) {
                    NativePopTarget.NONE -> Unit
                    NativePopTarget.HISTORY -> popUpTo<Route.History> { this.inclusive = inclusive }
                    NativePopTarget.BOARD_MAIN -> popUpTo<Route.Board.Main> { this.inclusive = inclusive }
                }
            }
        }
        val popRoute: () -> Unit = {
            navController.safePopBackStack(onCloseApp)
        }

        NavHost(
            navController = navController
        ) {
            composable<Route.Splash> {
                var isCleanUpDone by remember { mutableStateOf(false) }
                var isSplashAnimDone by remember { mutableStateOf(false) }

                fun tryNavigateToHistory() {
                    if (isCleanUpDone && isSplashAnimDone) {
                        navController.navigate(Route.History) {
                            popUpTo<Route.Splash> { inclusive = true }
                        }
                    }
                }

                SplashScreen(
                    onSplashFinished = {
                        isSplashAnimDone = true
                        tryNavigateToHistory()
                    }
                )

                LaunchedEffect(Unit) {
                    appComponent.cleanEmptyRoundsUseCase()
                    isCleanUpDone = true
                    tryNavigateToHistory()
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
                    },
                    onCloseApp = onCloseApp
                )
            }

            composable<Route.Precondition.Player> { route ->
                RouteScreenContent(
                    route = route,
                    appComponent = appComponent,
                    onNavigate = navigateRoute,
                    onBack = popRoute,
                    onShowAd = onShowAd,
                )
            }

            composable<Route.Precondition.Rule> { route ->
                RouteScreenContent(
                    route = route,
                    appComponent = appComponent,
                    onNavigate = navigateRoute,
                    onBack = popRoute,
                    onShowAd = onShowAd,
                )
            }

            composable<Route.Board.Main> { route ->
                RouteScreenContent(
                    route = route,
                    appComponent = appComponent,
                    onNavigate = navigateRoute,
                    onBack = popRoute,
                    onShowAd = onShowAd,
                )
            }

            composable<Route.Board.Prepare> { route ->
                RouteScreenContent(
                    route = route,
                    appComponent = appComponent,
                    onNavigate = navigateRoute,
                    onBack = popRoute,
                    onShowAd = onShowAd,
                )
            }

            composable<Route.Board.Score> { route ->
                RouteScreenContent(
                    route = route,
                    appComponent = appComponent,
                    onNavigate = navigateRoute,
                    onBack = popRoute,
                    onShowAd = onShowAd,
                )
            }

            composable<Route.Board.Calculate> { route ->
                RouteScreenContent(
                    route = route,
                    appComponent = appComponent,
                    onNavigate = navigateRoute,
                    onBack = popRoute,
                    onShowAd = onShowAd,
                )
            }

            composable<Route.Board.Detail> { route ->
                RouteScreenContent(
                    route = route,
                    appComponent = appComponent,
                    onNavigate = navigateRoute,
                    onBack = popRoute,
                    onShowAd = onShowAd,
                )
            }

            composable<Route.Board.RuleLog> { route ->
                RouteScreenContent(
                    route = route,
                    appComponent = appComponent,
                    onNavigate = navigateRoute,
                    onBack = popRoute,
                    onShowAd = onShowAd,
                )
            }

            composable<Route.Board.Manual> { route ->
                RouteScreenContent(
                    route = route,
                    appComponent = appComponent,
                    onNavigate = navigateRoute,
                    onBack = popRoute,
                    onShowAd = onShowAd,
                )
            }
        }
    }
}

@Composable
fun RouteScreenContent(
    route: Route,
    appComponent: AppComponent,
    onNavigate: (route: Route, popTarget: NativePopTarget, inclusive: Boolean) -> Unit,
    onBack: () -> Unit,
    onShowAd: (onCompleted: () -> Unit) -> Unit = { it() },
) {
    val coroutineScope = rememberCoroutineScope()
    val useNativeNavigation = LocalUseNativeNavigation.current

    when (route) {
        is Route.Precondition.Player -> {
            val playerViewModel: PlayerViewModel = viewModel { appComponent.playerViewModel }
            PlayerScreen(
                viewModel = playerViewModel,
                onNext = { players, gameName ->
                    val playersJson = Json.encodeToString(players)
                    onNavigate(
                        Route.Precondition.Rule(playersJson = playersJson, gameName = gameName),
                        NativePopTarget.NONE,
                        false,
                    )
                },
                onBack = onBack,
            )
        }

        is Route.Precondition.Rule -> {
            val players: List<Player> = runCatching {
                Json.decodeFromString<List<Player>>(route.playersJson)
            }.getOrDefault(emptyList())

            val ruleViewModel: RuleViewModel = viewModel(key = route.playersJson + route.gameName) {
                appComponent.ruleViewModelFactory(players, route.gameName)
            }

            RuleScreen(
                ruleViewModel = ruleViewModel,
                onNext = { gameId ->
                    onNavigate(
                        Route.Board.Main(gameId),
                        NativePopTarget.HISTORY,
                        false,
                    )
                },
                onBack = onBack,
            )
        }

        is Route.Board.Main -> {
            val boardViewModel: BoardViewModel = viewModel(key = route.gameId.toString()) {
                appComponent.boardViewModelFactory(route.gameId)
            }

            BoardScreen(
                boardViewModel = boardViewModel,
                onNext = { gameId ->
                    onNavigate(Route.Board.Prepare(gameId), NativePopTarget.NONE, false)
                },
                onBack = {
                    onNavigate(Route.History, NativePopTarget.HISTORY, true)
                },
                openDetailScreen = { gameId, roundId ->
                    onShowAd {
                        onNavigate(Route.Board.Detail(gameId, roundId), NativePopTarget.NONE, false)
                    }
                },
                openCalculated = { gameId ->
                    onShowAd {
                        onNavigate(Route.Board.Calculate(gameId), NativePopTarget.NONE, false)
                    }
                },
                openRule = { gameId ->
                    onNavigate(Route.Board.RuleLog(gameId), NativePopTarget.NONE, false)
                }
            )
        }

        is Route.Board.Prepare -> {
            val prepareViewModel: PrepareViewModel = viewModel(key = route.gameId.toString()) {
                appComponent.prepareViewModelFactory(route.gameId)
            }

            PrepareScreen(
                prepareViewModel = prepareViewModel,
                onComplete = { gameId, roundId ->
                    onNavigate(
                        Route.Board.Score(gameId, roundId),
                        NativePopTarget.BOARD_MAIN,
                        false,
                    )
                },
                onBack = onBack,
            )
        }

        is Route.Board.Score -> {
            val scoreViewModel: ScoreViewModel = viewModel(key = "${route.gameId}_${route.roundId}") {
                appComponent.scoreViewModelFactory(route.gameId, route.roundId)
            }
            val manualViewModel: ManualViewModel = viewModel(key = "${route.gameId}_${route.roundId}_manual") {
                appComponent.manualViewModel
            }

            ScoreScreen(
                scoreViewModel = scoreViewModel,
                manualViewModel = manualViewModel,
                onBack = { onBack() },
                onExit = { gameId ->
                    onNavigate(
                        Route.Board.Main(gameId),
                        NativePopTarget.BOARD_MAIN,
                        true,
                    )
                },
                onComplete = { gameId, roundId ->
                    onShowAd {
                        onNavigate(
                            Route.Board.Detail(gameId, roundId),
                            NativePopTarget.BOARD_MAIN,
                            false,
                        )
                    }
                },
                onOpenManual = if (useNativeNavigation) {
                    { onNavigate(Route.Board.Manual, NativePopTarget.NONE, false) }
                } else {
                    null
                },
            )
        }

        is Route.Board.Calculate -> {
            val resultViewModel: ResultViewModel = viewModel(key = "calculate_${route.gameId}") {
                appComponent.resultViewModelFactory(route.gameId, 0L, ScreenType.Calculate)
            }
            val uiState by resultViewModel.uiState.collectAsState()

            ResultScreen(
                uiState = uiState,
                roundTraceFormatter = appComponent.roundTraceFormatter,
                onBackClick = onBack,
                onShareClick = { graphicsLayer ->
                    coroutineScope.launch {
                        appComponent.screenCaptureManager.captureAndShare(graphicsLayer)
                        onShowAd { }
                    }
                }
            )
        }

        is Route.Board.Detail -> {
            val resultViewModel: ResultViewModel = viewModel(key = "detail_${route.gameId}_${route.roundId}") {
                appComponent.resultViewModelFactory(route.gameId, route.roundId, ScreenType.Detail)
            }
            val uiState by resultViewModel.uiState.collectAsState()

            ResultScreen(
                uiState = uiState,
                roundTraceFormatter = appComponent.roundTraceFormatter,
                onBackClick = {
                    onNavigate(
                        Route.Board.Main(route.gameId),
                        NativePopTarget.BOARD_MAIN,
                        true,
                    )
                },
                onShareClick = { graphicsLayer ->
                    coroutineScope.launch {
                        appComponent.screenCaptureManager.captureAndShare(graphicsLayer)
                        onShowAd { }
                    }
                }
            )
        }

        is Route.Board.RuleLog -> {
            val ruleLogViewModel: RuleLogViewModel = viewModel(key = route.gameId.toString()) {
                appComponent.ruleLogViewModelFactory(route.gameId)
            }

            RuleLogScreen(
                ruleLogViewModel = ruleLogViewModel,
                onBack = onBack,
            )
        }

        is Route.Board.Manual -> {
            val manualViewModel: ManualViewModel = viewModel(key = "manual_screen") {
                appComponent.manualViewModel
            }

            ManualScreen(
                manualViewModel = manualViewModel,
                onBack = onBack,
            )
        }

        is Route.Splash,
        is Route.History,
        is Route.Board.End -> Unit
    }
}
