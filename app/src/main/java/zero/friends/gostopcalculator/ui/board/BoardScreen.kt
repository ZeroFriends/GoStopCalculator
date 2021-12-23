package zero.friends.gostopcalculator.ui.board

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import zero.friends.domain.model.*
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.di.entrypoint.EntryPoint
import zero.friends.gostopcalculator.ui.common.*
import zero.friends.gostopcalculator.ui.dialog.DeleteDialog
import zero.friends.gostopcalculator.util.getEntryPointFromActivity

private sealed interface BoardEvent {
    object Back : BoardEvent
    object StartGame : BoardEvent
    object OpenDropDown : BoardEvent
    object Detail : BoardEvent
    class More(val roundId: Long) : BoardEvent
}

@Composable
fun createBoardViewModel(gameId: Long): BoardViewModel {
    val entryPoint = getEntryPointFromActivity<EntryPoint>()
    val factory = entryPoint.boardFactory()
    return viewModel(factory = BoardViewModel.provideFactory(boardViewModelFactory = factory, gameId = gameId))
}

@Composable
fun BoardScreen(boardViewModel: BoardViewModel, onNext: (gameId: Long) -> Unit = {}, onBack: () -> Unit = {}) {
    val scaffoldState = rememberScaffoldState()
    val uiState by boardViewModel.getUiState().collectAsState()
    val dialogState by boardViewModel.dialogState().collectAsState()

    BackHandler(true) {
        onBack()
    }
    BoardScreen(
        scaffoldState = scaffoldState,
        uiState = uiState
    ) { event ->
        when (event) {
            BoardEvent.Back -> onBack()
            BoardEvent.StartGame -> onNext(uiState.game.id)
            BoardEvent.OpenDropDown -> boardViewModel.openDropDown()
            BoardEvent.Detail -> {}
            is BoardEvent.More -> {
                boardViewModel.openDialog(event.roundId)
            }
        }
    }

    if (dialogState != null) {
        DeleteDialog(
            onDismiss = {
                boardViewModel.closeDialog()
            },
            onClick = {
                boardViewModel.deleteRound()
                boardViewModel.closeDialog()
            }
        )
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

@OptIn(ExperimentalFoundationApi::class)
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
                text = uiState.game.name,
                onBack = { event(BoardEvent.Back) },
                isRed = false,
                onAction = { event(BoardEvent.OpenDropDown) },
                actionIcon = painterResource(id = R.drawable.ic_more_black)
            )
        }
    ) {
        GoStopButtonBackground(
            buttonString = stringResource(id = R.string.start_game),
            onClick = { event(BoardEvent.StartGame) },
            contents = {
                Column {
                    IncomeHistory(players = uiState.playerResults)
                    Spacer(modifier = Modifier.padding(9.dp))
                    GameHistory(uiState = uiState, event = event)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IncomeHistory(
    modifier: Modifier = Modifier,
    players: List<PlayerResult> = emptyList()
) {
    ContentsCard(
        modifier = modifier
            .fillMaxWidth(),
        boxContents = {
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
    )
}

@Composable
private fun PlayerItem(index: Int, playerResult: PlayerResult) {
    val moneyColor by remember(playerResult.account) {
        derivedStateOf { getMoneyColor(playerResult.account) }
    }
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = playerResult.name,
                fontSize = 14.sp,
                color = colorResource(id = R.color.nero),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Text(
            text = String.format(stringResource(id = R.string.price), playerResult.account),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterVertically),
            color = colorResource(id = moneyColor)
        )
    }
}

@Composable
private fun GameHistory(modifier: Modifier = Modifier, uiState: BoardUiState, event: (BoardEvent) -> Unit = {}) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.game_history),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        if (uiState.gameHistories.isEmpty()) {
            EmptyHistory(
                painter = painterResource(id = R.drawable.ic_error_outline_black),
                title = stringResource(R.string.empty_game_log),
                subTitle = stringResource(R.string.info_game_start)
            )
        } else {
            LazyColumn {
                itemsIndexed(uiState.gameHistories.toList()) { index, (roundId, gamer) ->
                    RoundBox(index, roundId, gamer, event)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RoundBox(
    index: Int = 0,
    roundId: Long = 0L,
    gamers: List<Gamer> = emptyList(),
    event: (BoardEvent) -> Unit = {}
) {
    ContentsCard(modifier = Modifier.padding(vertical = 10.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = String.format(stringResource(id = R.string.round, (index + 1))))
                IconButton(onClick = { event(BoardEvent.More(roundId)) }) {
                    Icon(painter = painterResource(id = R.drawable.ic_more_black), contentDescription = null)
                }
            }

            GridItems(data = gamers, nColumns = 2) { index, gamer ->
                GamerItem(index = index, gamer)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.light_gray))
                    .clickable { event(BoardEvent.Detail) }
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.detail),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun GamerItem(index: Int, gamer: Gamer) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val moneyColor by remember(gamer.account) {
            derivedStateOf { getMoneyColor(gamer.account) }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Column(
                modifier = Modifier.align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.Center
            ) {
                if (gamer.winnerOption.isNotEmpty()) {
                    Text(
                        text = gamer.winnerOption.joinToString(" ") { it.korean },
                        fontSize = 8.sp
                    )
                }
                Text(
                    text = gamer.name,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.nero),
                )
                if (gamer.scoreOption.isNotEmpty() || gamer.loserOption.isNotEmpty()) {
                    Text(
                        text = gamer.scoreOption.joinToString(" ") { it.korean } + gamer.loserOption.joinToString(" ") { it.korean },
                        fontSize = 8.sp,
                    )
                }
            }

        }

        Text(
            text = String.format(stringResource(id = R.string.price), gamer.account),
            textAlign = TextAlign.Center,
            color = colorResource(id = moneyColor),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

private fun getMoneyColor(account: Int) = when {
    account > 0 -> {
        R.color.orangey_red
    }
    account < 0 -> {
        R.color.blue
    }
    else -> {
        R.color.nero
    }
}


@Preview
@Composable
fun GamerItemPreview() {
    GamerItem(index = 0, gamer = Gamer())
}

@Preview(showBackground = true)
@Composable
private fun RoundBoxPreview() {
    RoundBox(
        gamers = listOf(
            Gamer(name = "조재영", account = 10, winnerOption = listOf(WinnerOption.Winner)),
            Gamer(name = "송준영", account = -10, winnerOption = listOf(WinnerOption.Sell)),
            Gamer(
                name = "송준영",
                account = -100000,
                scoreOption = listOf(
                    ScoreOption.FirstDdadak,
                    ScoreOption.FirstFuck,
                    ScoreOption.FiveShine,
                    ScoreOption.President,
                    ScoreOption.ThreeFuck
                )
            )
        )
    )
}

@Composable
@Preview
private fun BoardScreenPreview() {
    BoardScreen(
        uiState = BoardUiState(
            game = Game(createdAt = "2020.11.26"),
            gameHistories = mapOf(
                0L to listOf(Gamer(name = "zero"), Gamer(name = "hello")),
                1L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas"), Gamer(name = "zerowolrd")),
                2L to listOf(
                    Gamer(name = "world"),
                    Gamer(name = "Asdasdas"),
                    Gamer(name = "zerowolrd"),
                    Gamer(name = "zzzzz")
                ),
                3L to listOf(
                    Gamer(name = "world"),
                    Gamer(name = "Asdasdas"),
                    Gamer(name = "zerowolrd"),
                    Gamer(name = "zzzzz"),
                    Gamer(name = "sdlakfdsfkl")
                ),
                4L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
//                5L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
//                6L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
//                7L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
//                8L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
            ),
            playerResults = listOf(PlayerResult("hPlayer1", 200), PlayerResult("HPlayer2", -100))
        )
    )
}