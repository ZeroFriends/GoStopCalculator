package zero.friends.gostopcalculator.ui.board.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by detailViewModel.uiState().collectAsState()

    LaunchedEffect(uiState.game.id, uiState.roundId) {
        if (uiState.game.id != 0L && uiState.roundId != 0L) {
            onBack()
        }
    }
}