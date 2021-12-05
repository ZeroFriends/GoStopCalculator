package zero.friends.gostopcalculator.ui.board

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import zero.friends.domain.model.Game
import zero.friends.domain.model.PlayerResult
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.di.provider.ViewModelProvider
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.EmptyHistory
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.util.getEntryPointFromActivity

private sealed interface BoardEvent {
    object Back : BoardEvent
    object StartGame : BoardEvent
    object OpenDropDown : BoardEvent
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
            BoardEvent.OpenDropDown -> boardViewModel.openDropDown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        DropdownMenu(
            expanded = uiState.showMoreDropDown,
            onDismissRequest = { boardViewModel.closeDropDown() },
            offset = DpOffset(16.dp, 16.dp)
        ) {
            Text(text = "dropDown1")
            Text(text = "dropDown2")
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
                onAction = { event(BoardEvent.OpenDropDown) },
                actionIcon = painterResource(id = R.drawable.ic_more_black)
            )
        }
    ) {
        BoardBackground(
            boxContents = {
                BoxContent(uiState.playerList)
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
private fun BoxContent(
    players: List<PlayerResult> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
        LazyVerticalGrid(cells = GridCells.Fixed(2)) {
            itemsIndexed(players) { index: Int, item: PlayerResult ->
                PlayerItem(index = index, playerResult = item)
            }
        }
    }
}

@Composable
private fun PlayerItem(index: Int, playerResult: PlayerResult) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = playerResult.name,
                fontSize = 16.sp,
                color = colorResource(id = R.color.nero),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Text(
            text = "${playerResult.account}원",
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
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
                itemsIndexed(uiState.gameHistory) { i, r ->
                    Text(text = r.id.toString())
                }
            }
        }
    }
}


@Composable
@Preview
private fun BoardScreenPreview() {
    BoardScreen(
        uiState = BoardUiState(game = Game(createdAt = "2020.11.26")),

        )
}