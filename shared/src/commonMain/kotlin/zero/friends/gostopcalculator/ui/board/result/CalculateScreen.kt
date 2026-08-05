package zero.friends.gostopcalculator.ui.board.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun CalculateScreen(
    calculateViewModel: CalculateViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by calculateViewModel.uiState().collectAsState()

    LaunchedEffect(uiState.game.id) {
        if (uiState.game.id != 0L) {
            onBack()
        }
    }
}