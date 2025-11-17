package zero.friends.gostopcalculator.ui.board.score

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.board.score.loser.LoserScreen
import zero.friends.gostopcalculator.ui.board.score.manual.ManualFullScreenDialog
import zero.friends.gostopcalculator.ui.board.score.scoring.ScoringScreen
import zero.friends.gostopcalculator.ui.board.score.selling.SellingScreen
import zero.friends.gostopcalculator.ui.board.score.winner.WinnerScreen
import zero.friends.gostopcalculator.ui.dialog.BasicDialog

/**
 * Score 관련 화면들의 Coordinator
 * Phase에 따라 적절한 Screen으로 라우팅
 */
@Composable
fun ScoreCoordinator(
    scoreViewModel: ScoreViewModel = hiltViewModel(),
    onBack: (gameId: Long?) -> Unit,
    onExit: (gameId: Long) -> Unit,
    onComplete: (gameId: Long, roundId: Long) -> Unit
) {
    val context = LocalContext.current
    val uiState by scoreViewModel.uiState().collectAsState()
    
    var openExtraDialog by remember { mutableStateOf(false) }
    var threeFuckDialog by remember { mutableStateOf(false) }
    var completeDialog by remember { mutableStateOf(false) }

    // ViewModel Events 처리
    LaunchedEffect(Unit) {
        scoreViewModel.escapeEvent()
            .onEach {
                scoreViewModel.deleteRound()
                onBack(uiState.game.id)
            }.launchIn(this)

        scoreViewModel.toast()
            .onEach {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }.launchIn(this)
    }

    // 뒤로가기 처리
    BackHandler(true, scoreViewModel::onBackPhase)

    // Extra Dialog 표시
    if (openExtraDialog) {
        when (uiState.phase) {
            is Selling -> SellingInfoDialog { openExtraDialog = false }
            else -> ManualFullScreenDialog { openExtraDialog = false }
        }
    }

    // 완료 Dialog
    if (completeDialog) {
        BasicDialog(
            confirmText = stringResource(id = android.R.string.ok),
            titleText = stringResource(R.string.dialog_text_complete),
            onClick = {
                scoreViewModel.calculateGameResult()
                onComplete(uiState.game.id, scoreViewModel.roundId)
            },
            onDismiss = {
                completeDialog = false
            }
        )
    }

    // 쓰리박 Dialog
    if (threeFuckDialog) {
        BasicDialog(
            confirmText = stringResource(id = android.R.string.ok),
            titleText = stringResource(id = R.string.three_fuck_title),
            text = stringResource(id = R.string.three_fuck_text),
            onDismiss = {
                threeFuckDialog = false
                uiState.threeFuckGamer?.let { gamer ->
                    scoreViewModel.selectScore(gamer, zero.friends.domain.model.ScoreOption.ThreeFuck)
                }
            },
            onClick = {
                threeFuckDialog = false
                scoreViewModel.calculateGameResult()
                onComplete(uiState.game.id, scoreViewModel.roundId)
            }
        )
    }

    // Phase에 따른 Screen 라우팅
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

