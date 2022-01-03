package zero.friends.gostopcalculator.ui.board.result

import androidx.activity.compose.BackHandler
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.*
import zero.friends.domain.model.Target
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

@Preview
@Composable
private fun Preview() {
    ResultScreen(
        gameName = "helloWorld",
        gamers = listOf(
            Gamer(
                name = "송준영", calculate = listOf(
                    Target(name = "조재영", account = 1000),
                    Target(name = "김경민", account = -1000),
                ),
                account = 2000,
                winnerOption = WinnerOption.Winner
            ),
            Gamer(
                name = "김경민", calculate = listOf(
                    Target(name = "송준영", account = -1000),
                    Target(name = "조재영", account = -500),
                ),
                account = -2000,
                scoreOption = listOf(ScoreOption.SecondFuck),
                loserOption = listOf(LoserOption.LightBak)
            )
        ),
        title = stringResource(id = R.string.calculate_history_text)
    )
}