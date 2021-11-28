package zero.friends.gostopcalculator.ui.board

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import zero.friends.domain.model.Game
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.di.provider.ViewModelProvider
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.EmptyHistory
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.util.getEntryPointFromActivity

private sealed interface BoardEvent {
    object Back : BoardEvent
    object StartGame : BoardEvent
}

@Composable
fun createBoardViewModel(gameId: Long): BoardViewModel {
    val entryPoint = getEntryPointFromActivity<ViewModelProvider.FactoryEntryPoint>()
    val factory = entryPoint.boardFactory()
    return viewModel(factory = BoardViewModel.provideFactory(boardViewModelFactory = factory, gameId = gameId))
}

@Composable
fun BoardScreen(boardViewModel: BoardViewModel, onBack: () -> Unit = {}) {
    val scaffoldState = rememberScaffoldState()
    val uiState by boardViewModel.getUiState().collectAsState()

    BackHandler(true) {
        onBack()
    }
    BoardScreen(
        scaffoldState = scaffoldState,
        uiState = uiState
    ) { event ->
        when (event) {
            BoardEvent.Back -> onBack()
            BoardEvent.StartGame -> {}
        }
    }
}

@Composable
private fun BoardScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: BoardUiState = BoardUiState(),
    event: (BoardEvent) -> Unit = {}
) {

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = uiState.game.createdAt,
                onBack = { event(BoardEvent.Back) },
                isRed = false,
                onAction = { TODO("준영이한테 이거 왜있냐고 물어보기") }
            )
        }
    ) {
        BoardBackground(
            boxContents = {
                BoxContent()
            },
            contents = {
                Contents(uiState)
            },
            buttonText = stringResource(R.string.start_game),
            onClickButton = { event(BoardEvent.StartGame) }
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxContent(players: List<Player> = emptyList()) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.income_history),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            RoundedCornerText(text = stringResource(id = R.string.calculate_history))
        }
        LazyVerticalGrid(cells = GridCells.Adaptive(128.dp)) {
            itemsIndexed(players) { index: Int, item: Player ->
                PlayerItem(index = index, player = item)
            }
        }
    }
}

@Composable
private fun PlayerItem(index: Int, player: Player) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = player.name,
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.nero)
                    )
                }

            }
        }

        Text(modifier = Modifier.weight(1f), text = "원", textAlign = TextAlign.End)
    }
}

@Composable
private fun Contents(uiState: BoardUiState) {
    Column {
        Text(
            text = stringResource(id = R.string.game_history),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        if (uiState.gameHistory.isEmpty()) {
            EmptyHistory(
                painter = painterResource(id = R.drawable.ic_error_outline_black),
                title = stringResource(R.string.empty_game_log),
                subTitle = stringResource(R.string.info_game_start)
            )
        } else {
            LazyColumn {

            }
        }
    }
}

@Preview
@Composable
private fun PlayerItemPreview() {
    PlayerItem(index = 0, player = Player("Zero.dev"))
}

@Preview
@Composable
private fun BoxContentsPreview() {
    BoxContent()
}

@Preview
@Composable
private fun ContentsPreview() {
    Contents(uiState = BoardUiState())
}

@Composable
@Preview
private fun BoardScreenPreview() {
    BoardScreen(
        uiState = BoardUiState(game = Game(createdAt = "2020.11.26")),

        )
}