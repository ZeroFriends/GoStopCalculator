package zero.friends.gostopcalculator.ui.board.score

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.ScoreOption
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.complete
import zero.friends.gostopcalculator.shared.generated.resources.dialog_text_complete
import zero.friends.gostopcalculator.shared.generated.resources.three_fuck_text
import zero.friends.gostopcalculator.shared.generated.resources.three_fuck_title
import zero.friends.gostopcalculator.ui.board.score.loser.LoserScreen
import zero.friends.gostopcalculator.ui.board.score.manual.ManualFullScreenDialog
import zero.friends.gostopcalculator.ui.board.score.manual.ManualViewModel
import zero.friends.gostopcalculator.ui.board.score.scoring.ScoringScreen
import zero.friends.gostopcalculator.ui.board.score.selling.SellingScreen
import zero.friends.gostopcalculator.ui.board.score.winner.WinnerScreen
import zero.friends.gostopcalculator.ui.dialog.BasicDialog

import zero.friends.gostopcalculator.ui.common.BackHandler

@Composable
fun ScoreScreen(
    scoreViewModel: ScoreViewModel,
    manualViewModel: ManualViewModel? = null,
    onBack: (gameId: Long?) -> Unit,
    onExit: (gameId: Long) -> Unit,
    onComplete: (gameId: Long, roundId: Long) -> Unit,
    onShowToast: (String) -> Unit = {},
    onOpenManual: (() -> Unit)? = null,
) {
    BackHandler(enabled = true) {
        scoreViewModel.onBackPhase()
    }

    val uiState by scoreViewModel.uiState().collectAsState()
    val scope = rememberCoroutineScope()

    var openExtraDialog by remember { mutableStateOf(false) }
    var threeFuckDialog by remember { mutableStateOf(false) }
    var completeDialog by remember { mutableStateOf(false) }
    var isNavigating by remember { mutableStateOf(false) }

    val openManualAction: () -> Unit = {
        if (onOpenManual != null) {
            onOpenManual()
        } else if (manualViewModel != null) {
            openExtraDialog = true
        }
    }

    val handleComplete: () -> Unit = {
        if (!isNavigating) {
            isNavigating = true
            scope.launch {
                scoreViewModel.calculateGameResult()
                onComplete(uiState.game.id, scoreViewModel.roundId)
            }
        }
    }

    val handleExit: () -> Unit = {
        if (!isNavigating) {
            isNavigating = true
            scope.launch {
                scoreViewModel.deleteRound()
                onExit(uiState.game.id)
            }
        }
    }

    LaunchedEffect(Unit) {
        scoreViewModel.escapeEvent()
            .onEach {
                if (!isNavigating) {
                    isNavigating = true
                    scoreViewModel.deleteRound()
                    onBack(uiState.game.id)
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
            else -> if (manualViewModel != null) {
                ManualFullScreenDialog(
                    manualViewModel = manualViewModel,
                    onDismiss = { openExtraDialog = false }
                )
            }
        }
    }

    if (completeDialog) {
        BasicDialog(
            confirmText = stringResource(Res.string.complete),
            titleText = stringResource(Res.string.dialog_text_complete),
            onClick = {
                completeDialog = false
                handleComplete()
            },
            onDismiss = {
                completeDialog = false
            }
        )
    }

    if (threeFuckDialog) {
        BasicDialog(
            confirmText = stringResource(Res.string.complete),
            titleText = stringResource(Res.string.three_fuck_title),
            text = stringResource(Res.string.three_fuck_text),
            onDismiss = {
                threeFuckDialog = false
                uiState.threeFuckGamer?.let { gamer ->
                    scoreViewModel.selectScore(gamer, ScoreOption.ThreeFuck)
                }
            },
            onClick = {
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
                onNext = scoreViewModel::onNextPhase,
                onBack = scoreViewModel::onBackPhase,
                onExit = handleExit,
                onUpdateSeller = scoreViewModel::updateSeller,
                onShowInfo = { openExtraDialog = true }
            )
        }
        is Scoring -> {
            ScoringScreen(
                game = uiState.game,
                gamers = uiState.playerResults,
                seller = uiState.seller,
                onNext = scoreViewModel::onNextPhase,
                onBack = scoreViewModel::onBackPhase,
                onExit = handleExit,
                onSelectScore = { gamer, option ->
                    scoreViewModel.selectScore(gamer, option) { shouldShowDialog ->
                        threeFuckDialog = shouldShowDialog
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
                onNext = scoreViewModel::onNextPhase,
                onBack = scoreViewModel::onBackPhase,
                onExit = handleExit,
                onUpdateWinner = scoreViewModel::updateWinner,
                onShowManual = openManualAction
            )
        }
        is Loser -> {
            LoserScreen(
                game = uiState.game,
                gamers = uiState.playerResults,
                seller = uiState.seller,
                winner = uiState.winner,
                onComplete = { completeDialog = true },
                onBack = scoreViewModel::onBackPhase,
                onExit = handleExit,
                onSelectLoser = scoreViewModel::selectLoser,
                onShowManual = openManualAction
            )
        }
    }
}
