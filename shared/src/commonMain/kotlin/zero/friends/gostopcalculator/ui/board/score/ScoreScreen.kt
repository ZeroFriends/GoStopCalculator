package zero.friends.gostopcalculator.ui.board.score

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import zero.friends.domain.model.ScoreOption
import zero.friends.gostopcalculator.ui.board.score.loser.LoserScreen
import zero.friends.gostopcalculator.ui.board.score.manual.ManualFullScreenDialog
import zero.friends.gostopcalculator.ui.board.score.manual.ManualTab
import zero.friends.gostopcalculator.ui.board.score.scoring.ScoringScreen
import zero.friends.gostopcalculator.ui.board.score.selling.SellingScreen
import zero.friends.gostopcalculator.ui.board.score.winner.WinnerScreen

import zero.friends.gostopcalculator.ui.common.BackHandler

@Composable
fun ScoreScreen(
    scoreViewModel: ScoreViewModel,
    onBack: (gameId: Long?) -> Unit,
    onExit: (gameId: Long) -> Unit,
    onComplete: (gameId: Long, roundId: Long) -> Unit,
    onShowToast: (String) -> Unit = {},
    onOpenManual: ((initialTab: Int) -> Unit)? = null,
) {
    var openExtraDialog by rememberSaveable { mutableStateOf(false) }
    var threeFuckDialog by rememberSaveable { mutableStateOf(false) }
    var completeDialog by rememberSaveable { mutableStateOf(false) }
    var isNavigating by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = true) {
        if (!isNavigating) {
            scoreViewModel.onBackPhase()
        }
    }

    val uiState by scoreViewModel.uiState().collectAsState()
    val scope = rememberCoroutineScope()
    val defaultManualTab = defaultManualTabForPhase(uiState.phase).index

    val openManualAction: () -> Unit = {
        if (onOpenManual != null) {
            onOpenManual(defaultManualTab)
        } else {
            openExtraDialog = true
        }
    }

    val handleComplete: () -> Unit = {
        if (!isNavigating) {
            isNavigating = true
            scope.launch {
                scoreViewModel.calculateGameResult()
                onComplete(scoreViewModel.gameId, scoreViewModel.roundId)
            }
        }
    }

    val handleExit: () -> Unit = {
        if (!isNavigating) {
            isNavigating = true
            scope.launch {
                scoreViewModel.deleteRound()
                onExit(scoreViewModel.gameId)
            }
        }
    }

    LaunchedEffect(Unit) {
        scoreViewModel.escapeEvent()
            .onEach {
                if (!isNavigating) {
                    isNavigating = true
                    scoreViewModel.deleteRound()
                    onBack(scoreViewModel.gameId)
                }
            }.launchIn(this)

        scoreViewModel.toast()
            .onEach { message ->
                if (message != null) {
                    onShowToast(message)
                }
            }.launchIn(this)
    }

    if (openExtraDialog) {
        when (uiState.phase) {
            is Selling -> SellingInfoDialog { openExtraDialog = false }
            else -> ManualFullScreenDialog(
                initialTab = defaultManualTab,
                onDismiss = { openExtraDialog = false }
            )
        }
    }

    if (completeDialog) {
        RoundConfirmDialog(
            winner = uiState.winner,
            seller = uiState.seller,
            gamers = uiState.playerResults,
            onRecheck = {
                completeDialog = false
            },
            onConfirm = {
                completeDialog = false
                handleComplete()
            }
        )
    }

    if (threeFuckDialog) {
        ThreeFuckConfirmDialog(
            gamer = uiState.threeFuckGamer,
            onRecheck = {
                threeFuckDialog = false
                if (!isNavigating) {
                    uiState.threeFuckGamer?.let { gamer ->
                        scoreViewModel.selectScore(gamer, ScoreOption.ThreeFuck)
                    }
                }
            },
            onConfirm = {
                threeFuckDialog = false
                handleComplete()
            }
        )
    }

    when (val phase = uiState.phase) {
        is Selling -> {
            SellingScreen(
                game = uiState.game,
                gamers = uiState.playerResults,
                isNextEnabled = phase.getEnableNext(),
                onNext = { if (!isNavigating) scoreViewModel.onNextPhase(phase) },
                onBack = { if (!isNavigating) scoreViewModel.onBackPhase() },
                onExit = handleExit,
                onUpdateSeller = { gamer, count ->
                    if (!isNavigating) scoreViewModel.updateSeller(gamer, count)
                },
                onShowInfo = { openExtraDialog = true }
            )
        }
        is Scoring -> {
            ScoringScreen(
                game = uiState.game,
                gamers = uiState.playerResults,
                seller = uiState.seller,
                onNext = { if (!isNavigating) scoreViewModel.onNextPhase(phase) },
                onBack = { if (!isNavigating) scoreViewModel.onBackPhase() },
                onExit = handleExit,
                onSelectScore = { gamer, option ->
                    if (!isNavigating) {
                        scoreViewModel.selectScore(gamer, option) { shouldShowDialog ->
                            threeFuckDialog = shouldShowDialog
                        }
                    }
                },
                onShowManual = openManualAction
            )
        }
        is Winner -> {
            WinnerScreen(
                game = uiState.game,
                gamers = uiState.playerResults,
                seller = uiState.seller,
                isNextEnabled = phase.getEnableNext(),
                onNext = { if (!isNavigating) scoreViewModel.onNextPhase(phase) },
                onBack = { if (!isNavigating) scoreViewModel.onBackPhase() },
                onExit = handleExit,
                onAdjustWinnerPoint = { gamerId, delta ->
                    if (!isNavigating) scoreViewModel.adjustWinnerPoint(gamerId, delta)
                },
                onUpdateWinnerPoint = { gamerId, point ->
                    if (!isNavigating) scoreViewModel.updateWinnerPoint(gamerId, point)
                },
                onUpdateWinnerGo = { gamerId, go ->
                    if (!isNavigating) scoreViewModel.updateWinnerGo(gamerId, go)
                },
                onCycleWinnerShake = { gamerId ->
                    if (!isNavigating) scoreViewModel.cycleWinnerShake(gamerId)
                },
                onCycleWinnerBomb = { gamerId ->
                    if (!isNavigating) scoreViewModel.cycleWinnerBomb(gamerId)
                },
                onShowManual = openManualAction
            )
        }
        is Loser -> {
            LoserScreen(
                game = uiState.game,
                gamers = uiState.playerResults,
                seller = uiState.seller,
                winner = uiState.winner,
                onComplete = { if (!isNavigating) completeDialog = true },
                onBack = { if (!isNavigating) scoreViewModel.onBackPhase() },
                onExit = handleExit,
                onSelectLoser = { gamer, option ->
                    if (!isNavigating) scoreViewModel.selectLoser(gamer, option)
                },
                onShowManual = openManualAction
            )
        }
    }
}

internal fun defaultManualTabForPhase(phase: Phase): ManualTab = when (phase) {
    is Selling -> ManualTab.MonthlyCards
    is Scoring, is Winner -> ManualTab.GoAndBonus
    is Loser -> ManualTab.Penalties
}
