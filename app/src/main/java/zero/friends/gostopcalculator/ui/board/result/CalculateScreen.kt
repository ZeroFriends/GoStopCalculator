package zero.friends.gostopcalculator.ui.board.result

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CalculateScreen(calculateViewModel: CalculateViewModel = hiltViewModel(), onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val uiState by calculateViewModel.uiState().collectAsState()

    BackHandler(true) {
        onBack()
    }

    LaunchedEffect(uiState.game.id) {
        if (uiState.game.id != 0L) {
            val intent = ResultActivity.createCalculateIntent(context, uiState.game.id)
            context.startActivity(intent)
            onBack()
        }
    }
}