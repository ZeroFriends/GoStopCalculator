package zero.friends.gostopcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.di.AppComponent
import zero.friends.gostopcalculator.di.createAppComponent
import zero.friends.gostopcalculator.theme.GoStopTheme
import zero.friends.gostopcalculator.ui.common.NetworkRequiredDialog
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
import zero.friends.gostopcalculator.ui.board.score.manual.ManualTab
import zero.friends.gostopcalculator.ui.history.HistoryScreen
import zero.friends.gostopcalculator.ui.history.HistoryViewModel
import zero.friends.gostopcalculator.ui.precondition.PlayerScreen
import zero.friends.gostopcalculator.ui.precondition.PlayerViewModel
import zero.friends.gostopcalculator.ui.precondition.RuleScreen
import zero.friends.gostopcalculator.ui.precondition.RuleViewModel
import zero.friends.gostopcalculator.ui.splash.SplashScreen

private data class PendingAdGateAction(
    val onProceed: () -> Unit,
    val onDismiss: () -> Unit = {},
)

@Composable
fun AppNavigator(
    appComponent: AppComponent = remember { createAppComponent() },
    showDebugAdToggle: Boolean = false,
    onCloseApp: () -> Unit = {},
    onShowAd: (onCompleted: () -> Unit) -> Unit = { it() },
    shouldGateOfflineAd: () -> Boolean = { false },
    onRetryNetwork: () -> Boolean = { true },
    onContinueOffline: () -> Unit = {},
    onNativeNavigate: ((NativeNavCommand) -> Unit)? = null,
    onNativePop: (() -> Unit)? = null,
) {
    GoStopTheme {
        val navController = rememberNavController(
            startDestination = Route.Splash,
            onNativeNavigate = onNativeNavigate,
            onNativePop = onNativePop,
        )
        var pendingAdGate by remember { mutableStateOf<PendingAdGateAction?>(null) }
        var showNetworkRetryError by remember { mutableStateOf(false) }
        var networkRetryAttempts by remember { mutableIntStateOf(0) }
        val currentOnShowAd by rememberUpdatedState(onShowAd)
        val currentShouldGateOfflineAd by rememberUpdatedState(shouldGateOfflineAd)
        val currentOnRetryNetwork by rememberUpdatedState(onRetryNetwork)
        val currentOnContinueOffline by rememberUpdatedState(onContinueOffline)

        fun clearAdGate() {
            showNetworkRetryError = false
            networkRetryAttempts = 0
            pendingAdGate = null
        }

        val requestAdGatedNavigation: (onDismiss: () -> Unit, onProceed: () -> Unit) -> Unit = remember {
            { onDismiss, onProceed ->
                if (currentShouldGateOfflineAd()) {
                    showNetworkRetryError = false
                    networkRetryAttempts = 0
                    pendingAdGate = PendingAdGateAction(
                        onProceed = onProceed,
                        onDismiss = onDismiss,
                    )
                } else {
                    currentOnShowAd(onProceed)
                }
            }
        }
        val navigateFromRoute: (Route, Route, NativePopTarget, Boolean) -> Unit = remember(navController) {
            { expectedRoute, targetRoute, popTarget, inclusive ->
                navController.navigateFrom(expectedRoute, targetRoute) {
                    if (targetRoute is Route.Board.Manual) {
                        launchSingleTop = true
                    }
                    when (popTarget) {
                        NativePopTarget.NONE -> Unit
                        NativePopTarget.HISTORY -> popUpTo<Route.History> { this.inclusive = inclusive }
                        NativePopTarget.BOARD_MAIN -> popUpTo<Route.Board.Main> { this.inclusive = inclusive }
                    }
                }
            }
        }
        val popFromRoute: (Route) -> Unit = remember(navController, onCloseApp) {
            { expectedRoute ->
                navController.popBackStackFrom(expectedRoute, onEmpty = onCloseApp)
            }
        }

        NavHost(
            navController = navController
        ) {
            composable<Route.Splash> {
                var isCleanUpDone by remember { mutableStateOf(false) }
                var isSplashAnimDone by remember { mutableStateOf(false) }

                fun tryNavigateToHistory() {
                    if (isCleanUpDone && isSplashAnimDone) {
                        navController.navigateFrom(Route.Splash, Route.History) {
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
                        navController.navigateFrom(Route.History, Route.Precondition.Player)
                    },
                    onShowRound = { game ->
                        navController.navigateFrom(Route.History, Route.Board.Main(game.id))
                    },
                    onOpenManual = {
                        navController.navigateFrom(Route.History, Route.Board.Manual()) {
                            launchSingleTop = true
                        }
                    },
                    onCloseApp = onCloseApp,
                    showDebugAdToggle = showDebugAdToggle,
                )
            }

            screenComposable<Route.Precondition.Player>(appComponent, navigateFromRoute, popFromRoute, requestAdGatedNavigation)
            screenComposable<Route.Precondition.Rule>(appComponent, navigateFromRoute, popFromRoute, requestAdGatedNavigation)
            screenComposable<Route.Board.Main>(appComponent, navigateFromRoute, popFromRoute, requestAdGatedNavigation)
            screenComposable<Route.Board.Prepare>(appComponent, navigateFromRoute, popFromRoute, requestAdGatedNavigation)
            screenComposable<Route.Board.Score>(appComponent, navigateFromRoute, popFromRoute, requestAdGatedNavigation)
            screenComposable<Route.Board.Calculate>(appComponent, navigateFromRoute, popFromRoute, requestAdGatedNavigation)
            screenComposable<Route.Board.Detail>(appComponent, navigateFromRoute, popFromRoute, requestAdGatedNavigation)
            screenComposable<Route.Board.RuleLog>(appComponent, navigateFromRoute, popFromRoute, requestAdGatedNavigation)
            screenComposable<Route.Board.Manual>(appComponent, navigateFromRoute, popFromRoute, requestAdGatedNavigation)
        }

        val activeGate = pendingAdGate
        if (activeGate != null) {
            NetworkRequiredDialog(
                showRetryError = showNetworkRetryError,
                retryAttemptCount = networkRetryAttempts,
                onRetry = {
                    if (pendingAdGate !== activeGate) return@NetworkRequiredDialog
                    if (currentOnRetryNetwork()) {
                        clearAdGate()
                        activeGate.onProceed()
                    } else {
                        showNetworkRetryError = true
                        networkRetryAttempts += 1
                    }
                },
                onContinueOffline = {
                    if (pendingAdGate !== activeGate) return@NetworkRequiredDialog
                    clearAdGate()
                    currentOnContinueOffline()
                    activeGate.onProceed()
                },
                onDismiss = {
                    if (pendingAdGate !== activeGate) return@NetworkRequiredDialog
                    clearAdGate()
                    activeGate.onDismiss()
                },
            )
        }
    }
}

private inline fun <reified T : Route> NavGraphBuilder.screenComposable(
    appComponent: AppComponent,
    noinline navigateFromRoute: (Route, Route, NativePopTarget, Boolean) -> Unit,
    noinline popFromRoute: (Route) -> Unit,
    noinline onShowAd: (onDismiss: () -> Unit, onCompleted: () -> Unit) -> Unit,
) {
    composable<T> { route ->
        RouteScreenContent(
            route = route,
            appComponent = appComponent,
            onNavigate = { target, popTarget, inclusive ->
                navigateFromRoute(route, target, popTarget, inclusive)
            },
            onBack = { popFromRoute(route) },
            onShowAd = onShowAd,
        )
    }
}

@Composable
fun RouteScreenContent(
    route: Route,
    appComponent: AppComponent,
    onNavigate: (route: Route, popTarget: NativePopTarget, inclusive: Boolean) -> Unit,
    onBack: () -> Unit,
    onShowAd: (onDismiss: () -> Unit, onCompleted: () -> Unit) -> Unit = { _, onCompleted -> onCompleted() },
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
            val players: List<Player> = remember(route.playersJson) {
                runCatching {
                    Json.decodeFromString<List<Player>>(route.playersJson)
                }.getOrDefault(emptyList())
            }

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
                onOpenManual = {
                    onNavigate(
                        Route.Board.Manual(initialTab = ManualTab.GameFlow.index),
                        NativePopTarget.NONE,
                        false,
                    )
                },
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
                    onShowAd({}) {
                        onNavigate(Route.Board.Detail(gameId, roundId), NativePopTarget.NONE, false)
                    }
                },
                openCalculated = { gameId ->
                    onShowAd({}) {
                        onNavigate(Route.Board.Calculate(gameId), NativePopTarget.NONE, false)
                    }
                },
                openRule = { gameId ->
                    onNavigate(Route.Board.RuleLog(gameId), NativePopTarget.NONE, false)
                },
                openManual = {
                    onNavigate(Route.Board.Manual(initialTab = ManualTab.CardScores.index), NativePopTarget.NONE, false)
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

            ScoreScreen(
                scoreViewModel = scoreViewModel,
                onBack = { onBack() },
                onExit = { gameId ->
                    onNavigate(
                        Route.Board.Main(gameId),
                        NativePopTarget.BOARD_MAIN,
                        true,
                    )
                },
                onComplete = { gameId, roundId ->
                    onShowAd(
                        {
                            onNavigate(
                                Route.Board.Main(gameId),
                                NativePopTarget.BOARD_MAIN,
                                true,
                            )
                        }
                    ) {
                        onNavigate(
                            Route.Board.Detail(gameId, roundId),
                            NativePopTarget.BOARD_MAIN,
                            false,
                        )
                    }
                },
                onOpenManual = if (useNativeNavigation) {
                    { initialTab ->
                        onNavigate(Route.Board.Manual(initialTab = initialTab), NativePopTarget.NONE, false)
                    }
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
            ManualScreen(
                initialTab = route.initialTab,
                onBack = onBack,
            )
        }

        is Route.Splash,
        is Route.History,
        is Route.Board.End -> Unit
    }
}
