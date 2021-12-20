package zero.friends.gostopcalculator.ui.board.score

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun ScoreScreen(scoreViewModel: ScoreViewModel = hiltViewModel(), onBack: (gameId: Long) -> Unit) {
    val uiState by scoreViewModel.uiState().collectAsState()
    BackHandler(true) {
        onBack(uiState.game.id)
    }
}