package zero.friends.gostopcalculator.ui.board.main

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.PlayerResult
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.calculate_history
import zero.friends.gostopcalculator.shared.generated.resources.delete
import zero.friends.gostopcalculator.shared.generated.resources.delete_dialog_title
import zero.friends.gostopcalculator.shared.generated.resources.empty_game_log
import zero.friends.gostopcalculator.shared.generated.resources.game_history
import zero.friends.gostopcalculator.shared.generated.resources.ic_error_outline_black
import zero.friends.gostopcalculator.shared.generated.resources.income_history
import zero.friends.gostopcalculator.shared.generated.resources.info_game_start
import zero.friends.gostopcalculator.shared.generated.resources.rule_title_text
import zero.friends.gostopcalculator.shared.generated.resources.start_game
import zero.friends.gostopcalculator.navigation.LocalNativeTopBarHeight
import zero.friends.gostopcalculator.navigation.LocalUseNativeNavigation
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.ContentsCard
import zero.friends.gostopcalculator.ui.common.EmptyHistory
import zero.friends.gostopcalculator.ui.common.PlayerItem
import zero.friends.gostopcalculator.ui.common.RoundBox
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.ui.common.background.GoStopButtonBackground
import zero.friends.gostopcalculator.ui.common.BackHandler
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
    boardViewModel: BoardViewModel,
    onNext: (gameId: Long) -> Unit = {},
    onBack: () -> Unit = {},
    openDetailScreen: (gameId: Long, roundId: Long) -> Unit = { _, _ -> },
    openCalculated: (gameId: Long) -> Unit = {},
    openRule: (gameId: Long) -> Unit = {},
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val scaffoldState = rememberScaffoldState()

    val uiState by boardViewModel.getUiState().collectAsState()
    var openDialog by remember {
        mutableStateOf<Long?>(null)
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
            confirmText = stringResource(Res.string.delete),
            titleText = stringResource(Res.string.delete_dialog_title)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoardScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: BoardUiState = BoardUiState(),
    event: (BoardEvent) -> Unit = {},
) {
    val useNativeNavigation = LocalUseNativeNavigation.current
    val nativeTopBarHeight = if (useNativeNavigation) LocalNativeTopBarHeight.current else 0.dp
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
                actionText = stringResource(Res.string.rule_title_text),
                onAction = { event(BoardEvent.OpenRule) }
            )
        }
    ) { innerPadding ->
        GoStopButtonBackground(
            modifier = Modifier.padding(innerPadding),
            buttonString = stringResource(Res.string.start_game),
            onClick = { event(BoardEvent.StartGame) },
            showGradient = showBottomGradient,
        ) {
            LazyColumn(
                state = historyListState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(
                    top = 16.dp + nativeTopBarHeight,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                )
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
                        text = stringResource(Res.string.game_history),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                if (uiState.gameHistories.isEmpty()) {
                    item {
                        EmptyHistory(
                            painter = painterResource(Res.drawable.ic_error_outline_black),
                            title = stringResource(Res.string.empty_game_log),
                            subTitle = stringResource(Res.string.info_game_start)
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

            if (showTopGradient && !useNativeNavigation) {
                val topGradientColor = Color.White
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    topGradientColor,
                                    topGradientColor.copy(alpha = 0f)
                                )
                            )
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IncomeHistory(
    modifier: Modifier = Modifier,
    players: List<PlayerResult> = emptyList(),
    onClickCalculated: () -> Unit = {},
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
                        text = stringResource(Res.string.income_history),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    RoundedCornerText(
                        text = stringResource(Res.string.calculate_history),
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
