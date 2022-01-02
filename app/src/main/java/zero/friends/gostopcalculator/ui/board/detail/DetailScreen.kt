package zero.friends.gostopcalculator.ui.board.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import zero.friends.domain.model.*
import zero.friends.domain.model.Target
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.di.entrypoint.EntryPoint
import zero.friends.gostopcalculator.ui.common.CalculatedBox
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.util.getEntryPointFromActivity

@Composable
fun createDetailViewModel(roundId: Long): DetailViewModel {
    val entryPoint = getEntryPointFromActivity<EntryPoint>()
    val factory = entryPoint.detailFactory()
    return viewModel(factory = DetailViewModel.provideFactory(factory, roundId))
}

@Composable
fun DetailScreen(detailViewModel: DetailViewModel = hiltViewModel(), onBack: () -> Unit = {}) {
    val scaffoldState = rememberScaffoldState()
    val uiState by detailViewModel.uiState().collectAsState()

    BackHandler(true) {
        onBack()
    }

    DetailScreen(
        scaffoldState = scaffoldState,
        gameName = uiState.game.name,
        gamers = uiState.gamers,
        onBack = onBack
    )
}

@Composable
private fun DetailScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    gameName: String = "",
    gamers: List<Gamer> = emptyList(),
    onBack: () -> Unit = {}
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = gameName,
                onBack = onBack,
                isRed = false
            )
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.detail_text),
                    fontSize = 24.sp,
                    color = colorResource(id = R.color.nero),
                    fontWeight = FontWeight.Bold
                )
            }
            itemsIndexed(gamers) { index, gamer ->
                CalculatedBox(index, gamer)
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    DetailScreen(
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
        gameName = "helloWorld"
    )
}