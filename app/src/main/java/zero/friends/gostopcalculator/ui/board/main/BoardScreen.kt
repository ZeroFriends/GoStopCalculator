package zero.friends.gostopcalculator.ui.board.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.*
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.*
import zero.friends.gostopcalculator.ui.common.background.GoStopButtonBackground
import zero.friends.gostopcalculator.ui.dialog.BasicDialog

private sealed interface BoardEvent {
    object Back : BoardEvent
    object StartGame : BoardEvent
    class Detail(val roundId: Long) : BoardEvent
    class More(val roundId: Long) : BoardEvent
    object OpenCalculated : BoardEvent
    object OpenRule : BoardEvent
}

@Composable
fun BoardScreen(
    boardViewModel: BoardViewModel = hiltViewModel(),
    onNext: (gameId: Long) -> Unit = {},
    onBack: () -> Unit = {},
    openDetailScreen: (gameId: Long, roundId: Long) -> Unit = { _, _ -> },
    openCalculated: (gameId: Long) -> Unit = {},
    openRule: (gameId: Long) -> Unit = {}
) {
    val scaffoldState = rememberScaffoldState()
    val uiState by boardViewModel.getUiState().collectAsState()
    var openDialog by remember {
        mutableStateOf<Long?>(null)
    }

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
            is BoardEvent.Detail -> {
                openDetailScreen(uiState.game.id, event.roundId)
            }
            is BoardEvent.More -> {
                openDialog = event.roundId
            }
            BoardEvent.OpenCalculated -> openCalculated(uiState.game.id)
            BoardEvent.OpenRule -> openRule(uiState.game.id)
        }
    }

    if (openDialog != null) {
        BasicDialog(
            onDismiss = {
                openDialog = null
            },
            onClick = {
                val roundId = openDialog
                requireNotNull(roundId)
                boardViewModel.deleteRound(roundId)
                openDialog = null
            },
            confirmText = stringResource(R.string.delete),
            titleText = stringResource(R.string.delete_dialog_title)
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoardScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: BoardUiState = BoardUiState(),
    event: (BoardEvent) -> Unit = {}
) {
    val historyListState = rememberLazyListState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = uiState.game.name,
                onBack = { event(BoardEvent.Back) },
                isRed = false,
                actionText = stringResource(id = R.string.rule_title_text),
                onAction = { event(BoardEvent.OpenRule) }
            )
        }
    ) {
        GoStopButtonBackground(
            buttonString = stringResource(id = R.string.start_game),
            onClick = { event(BoardEvent.StartGame) },
            contents = {
                Column {
                    IncomeHistory(
                        players = uiState.playerResults,
                        onClickCalculated = { event(BoardEvent.OpenCalculated) },
                        hide = historyListState.firstVisibleItemIndex != 0
                    )
                    Spacer(modifier = Modifier.padding(9.dp))
                    GameHistory(
                        uiState = uiState,
                        event = event,
                        lazyListState = historyListState
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IncomeHistory(
    modifier: Modifier = Modifier,
    players: List<PlayerResult> = emptyList(),
    onClickCalculated: () -> Unit = {},
    hide: Boolean
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
                    RoundedCornerText(
                        text = stringResource(id = R.string.calculate_history),
                        onClick = onClickCalculated
                    )
                }
                LazyVerticalGrid(
                    modifier = modifier.animateContentSize(),
                    cells = GridCells.Fixed(2)
                ) {
                    itemsIndexed(if (hide) emptyList() else players) { index: Int, item: PlayerResult ->
                        PlayerItem(index = index, playerResult = item)
                    }
                }
            }
        }
    )
}

@Composable
private fun GameHistory(
    modifier: Modifier = Modifier,
    uiState: BoardUiState,
    event: (BoardEvent) -> Unit = {},
    lazyListState: LazyListState
) {
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
            Spacer(modifier = Modifier.padding(5.dp))
            LazyColumn(state = lazyListState, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(uiState.gameHistories.toList()) { index, (roundId, gamer) ->
                    RoundBox(
                        index = uiState.gameHistories.size - index - 1,
                        gamers = gamer,
                        onClickDetail = { event(BoardEvent.Detail(roundId)) },
                        onClickMore = { event(BoardEvent.More(roundId)) }
                    )
                }
            }
        }
    }
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
                    Gamer(
                        name = "zerowolrd",
                        sellerOption = SellerOption.Seller,
                        scoreOption = listOf(ScoreOption.FirstFuck)
                    ),
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

@Composable
@Preview
private fun BoardScreenPreview2() {
    BoardScreen(
        uiState = BoardUiState(
            game = Game(createdAt = "2020.11.26"),
            playerResults = listOf(PlayerResult("hPlayer1", 200), PlayerResult("HPlayer2", -100))
        )
    )
}