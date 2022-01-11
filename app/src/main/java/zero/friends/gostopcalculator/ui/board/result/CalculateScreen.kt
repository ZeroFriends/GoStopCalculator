package zero.friends.gostopcalculator.ui.board.result

import androidx.activity.compose.BackHandler
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.gostopcalculator.R

@Composable
fun CalculateScreen(calculateViewModel: CalculateViewModel = hiltViewModel(), onBack: () -> Unit = {}) {
    val scaffoldState = rememberScaffoldState()
    val uiState by calculateViewModel.uiState().collectAsState()

    BackHandler(true) {
        onBack()
    }

    ResultScreen(
        scaffoldState = scaffoldState,
        gameName = uiState.game.name,
        gamers = uiState.gamers,
        onBack = onBack,
        title = stringResource(id = R.string.calculate_history_text)
    )
}