package zero.friends.gostopcalculator.ui.board.score

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.DescriptionBox
import zero.friends.gostopcalculator.ui.common.GoStopButtonBackground


@Composable
fun ScoreScreen(scoreViewModel: ScoreViewModel = hiltViewModel(), onBack: (gameId: Long) -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val uiState by scoreViewModel.uiState().collectAsState()
    BackHandler(true) {
        onBack(uiState.game.id)
    }

    ScoreScreen(scaffoldState, uiState)
}

@Composable
private fun ScoreScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: ScoreUiState = ScoreUiState()
) {
    Scaffold(
        modifier = Modifier,
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = uiState.game.name,
                onBack = {},
                isRed = false
            )
        }
    ) {
        GoStopButtonBackground(
            buttonString = uiState.buttonText
        ) {
            Column {
                DescriptionBox(mainText = "점수기록", subText = "운ㅋ")
                Spacer(modifier = Modifier.padding(22.dp))
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ScoreScreen(uiState = ScoreUiState())
}
