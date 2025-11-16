package zero.friends.gostopcalculator.ui.board.result

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DetailScreen(detailViewModel: DetailViewModel = hiltViewModel(), onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val uiState by detailViewModel.uiState().collectAsState()

    BackHandler(true) {
        onBack()
    }

    LaunchedEffect(uiState.game.id, uiState.roundId) {
        if (uiState.game.id != 0L && uiState.roundId != 0L) {
            val intent = ResultActivity.createDetailIntent(context, uiState.game.id, uiState.roundId)
            context.startActivity(intent)
            onBack()
        }
    }
}