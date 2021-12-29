package zero.friends.gostopcalculator.ui.board.score.end

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.PlayerResult
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.ContentsCard
import zero.friends.gostopcalculator.ui.common.GoStopButtonBackground
import zero.friends.gostopcalculator.ui.common.PlayerItem

@Composable
fun EndScreen(
    endViewModel: EndViewModel = hiltViewModel(),
    onBack: (gameId: Long) -> Unit = {},
    onComplete: (gameId: Long) -> Unit = {}
) {
    val uiState by endViewModel.endUiState().collectAsState()
    val scaffoldState = rememberScaffoldState()
    BackHandler {
        onBack(uiState.game.id)
    }
    EndScreen(scaffoldState = scaffoldState, uiState = uiState, onClick = { onComplete(uiState.game.id) })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EndScreen(scaffoldState: ScaffoldState, uiState: EndUiState, onClick: () -> Unit = {}) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { CenterTextTopBar(text = stringResource(id = R.string.end_game)) }
    ) {
        GoStopButtonBackground(
            buttonString = stringResource(id = R.string.save),
            onClick = { onClick() }
        ) {
            FinishScreen(Modifier.align(Alignment.Center))
            ContentsCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 21.dp, horizontal = 18.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.sum_score),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                        itemsIndexed(uiState.players) { index, item ->
                            PlayerItem(index = index, playerResult = item)
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun FinishScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.ic_confetti), contentDescription = null)
        Spacer(modifier = Modifier.padding(6.dp))
        Text(
            text = stringResource(R.string.congratulation),
            fontSize = 24.sp,
            color = colorResource(id = R.color.nero),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Text(
            text = stringResource(R.string.end_description),
            fontSize = 14.sp,
            color = colorResource(id = R.color.gray38)
        )
    }
}


@Preview
@Composable
private fun Preview() {
    EndScreen(
        scaffoldState = rememberScaffoldState(),
        uiState = EndUiState(
            players = listOf(
                PlayerResult("zero", 1000),
                PlayerResult("하이", -1000),
                PlayerResult("재영", +1000),
                PlayerResult("준영", -1000),
                PlayerResult("준영", -500),
                PlayerResult("준영", -12300),
            )
        )
    )
}