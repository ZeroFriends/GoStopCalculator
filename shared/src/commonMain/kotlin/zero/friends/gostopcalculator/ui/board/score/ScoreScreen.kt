package zero.friends.gostopcalculator.ui.board.score

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    manualViewModel: ManualViewModel,
    onBack: (gameId: Long?) -> Unit,
    onExit: (gameId: Long) -> Unit,
    onComplete: (gameId: Long, roundId: Long) -> Unit,
    onShowToast: (String) -> Unit = {}
) {
    BackHandler(enabled = true) {
        onBack(null)
    }

    val uiState by scoreViewModel.uiState().collectAsState()


    var openExtraDialog by remember { mutableStateOf(false) }
    var threeFuckDialog by remember { mutableStateOf(false) }
    var completeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scoreViewModel.escapeEvent()
            .onEach {
                scoreViewModel.deleteRound()
                onBack(uiState.game.id)
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
                manualViewModel = manualViewModel,
                onDismiss = { openExtraDialog = false }
            )
        }
    }

    if (completeDialog) {
        BasicDialog(
            confirmText = stringResource(Res.string.complete),
            titleText = stringResource(Res.string.dialog_text_complete),
            onClick = {
                scoreViewModel.calculateGameResult()
                onComplete(uiState.game.id, scoreViewModel.roundId)
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
                scoreViewModel.calculateGameResult()
                onComplete(uiState.game.id, scoreViewModel.roundId)
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
                onExit = {
                    scoreViewModel.deleteRound()
                    onExit(uiState.game.id)
                },
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
                onExit = {
                    scoreViewModel.deleteRound()
                    onExit(uiState.game.id)
                },
                onSelectScore = { gamer, option ->
                    scoreViewModel.selectScore(gamer, option) { shouldShowDialog ->
                        threeFuckDialog = shouldShowDialog
                    }
                },
                onShowManual = { openExtraDialog = true }
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
                onExit = {
                    scoreViewModel.deleteRound()
                    onExit(uiState.game.id)
                },
                onUpdateWinner = scoreViewModel::updateWinner,
                onShowManual = { openExtraDialog = true }
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
                onExit = {
                    scoreViewModel.deleteRound()
                    onExit(uiState.game.id)
                },
                onSelectLoser = scoreViewModel::selectLoser,
                onShowManual = { openExtraDialog = true }
            )
        }
    }
}
