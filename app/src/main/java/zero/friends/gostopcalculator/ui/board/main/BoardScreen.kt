package zero.friends.gostopcalculator.ui.board.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.PlayerResult
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.SellerOption
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.ContentsCard
import zero.friends.gostopcalculator.ui.common.EmptyHistory
import zero.friends.gostopcalculator.ui.common.PlayerItem
import zero.friends.gostopcalculator.ui.common.RoundBox
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
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
    val showBottomGradient by remember { derivedStateOf { historyListState.canScrollForward } }
    val showTopGradient by remember { derivedStateOf { historyListState.canScrollBackward } }

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
    ) { innerPadding ->
        GoStopButtonBackground(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            buttonString = stringResource(id = R.string.start_game),
            onClick = { event(BoardEvent.StartGame) },
            showGradient = showBottomGradient,
            contents = {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = historyListState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            IncomeHistory(
                                players = uiState.playerResults,
                                onClickCalculated = { event(BoardEvent.OpenCalculated) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(id = R.string.game_history),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        if (uiState.gameHistories.isEmpty()) {
                            item {
                                EmptyHistory(
                                    painter = painterResource(id = R.drawable.ic_error_outline_black),
                                    title = stringResource(R.string.empty_game_log),
                                    subTitle = stringResource(R.string.info_game_start)
                                )
                            }
                        } else {
                            val histories = uiState.gameHistories.toList().asReversed()
                            itemsIndexed(histories) { index, (roundId, gamer) ->
                                RoundBox(
                                    index = uiState.gameHistories.size - index - 1,
                                    gamers = gamer,
                                    onClickDetail = { event(BoardEvent.Detail(roundId)) },
                                    onClickMore = { event(BoardEvent.More(roundId)) }
                                )
                            }
                        }
                    }

                    if (showTopGradient) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .align(Alignment.TopCenter)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            LightGray,
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }
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
    onClickCalculated: () -> Unit = {}
) {
    ContentsCard(
        modifier = modifier.fillMaxWidth(),
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
                    modifier = Modifier.heightIn(max = 240.dp),
                    columns = GridCells.Fixed(2),
                ) {
                    itemsIndexed(players) { index: Int, item: PlayerResult ->
                        PlayerItem(index = index, playerResult = item)
                    }
                }
            }
        }
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
