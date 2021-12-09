package zero.friends.gostopcalculator.ui.board

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
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
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.PlayerResult
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.di.entrypoint.EntryPoint
import zero.friends.gostopcalculator.ui.common.*
import zero.friends.gostopcalculator.util.getEntryPointFromActivity

private sealed interface BoardEvent {
    object Back : BoardEvent
    object StartGame : BoardEvent
    object OpenDropDown : BoardEvent
}

@Composable
fun createBoardViewModel(gameId: Long): BoardViewModel {
    val entryPoint = getEntryPointFromActivity<EntryPoint>()
    val factory = entryPoint.boardFactory()
    return viewModel(factory = BoardViewModel.provideFactory(assistedFactory = factory, gameId = gameId))
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
        val contentsModifier = Modifier.padding(horizontal = 16.dp)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp, bottom = 20.dp)
        ) {

            Column {
                IncomeHistory(modifier = contentsModifier, uiState.playerList)
                Spacer(modifier = Modifier.padding(9.dp))
                GameHistory(modifier = contentsModifier, uiState)
            }

            GoStopButton(
                text = stringResource(R.string.start_game),
                modifier = contentsModifier.align(Alignment.BottomCenter),
                onClick = { event(BoardEvent.StartGame) }
            )
        }
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
private fun GameHistory(modifier: Modifier, uiState: BoardUiState) {
    Column(modifier = modifier) {
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
            Column(
                modifier = Modifier
            ) {
                uiState.gameHistory.values.forEachIndexed { index, gamer ->
                    RoundBox(index, gamer)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RoundBox(index: Int = 0, gamers: List<Gamer> = emptyList()) {
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
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.ic_more_black), contentDescription = null)
                }
            }
            LazyVerticalGrid(
                cells = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = 10.dp),
            ) {
                itemsIndexed(gamers) { index, gamer ->
                    GamerItem(index = index, gamer)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.light_gray))
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
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
            derivedStateOf {
                when {
                    gamer.account > 0 -> {
                        R.color.orangey_red
                    }
                    gamer.account < 0 -> {
                        R.color.blue
                    }
                    else -> {
                        R.color.nero
                    }
                }

            }
        }

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
            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                if (gamer.optional != null) Text(text = gamer.optional.toString(), fontSize = 8.sp)
                Text(
                    text = gamer.name,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.nero),
                )
            }

        }

        Text(
            text = "${gamer.account}원",
            textAlign = TextAlign.Center,
            color = colorResource(id = moneyColor),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
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
    RoundBox(gamers = listOf(Gamer(account = 10, optional = "광팜"), Gamer(account = -10, optional = "승자")))
}

@Composable
@Preview
private fun BoardScreenPreview() {
    BoardScreen(
        uiState = BoardUiState(
            game = Game(createdAt = "2020.11.26"),
            gameHistory = mapOf(
                0L to listOf(Gamer(name = "zero"), Gamer(name = "hello")),
                1L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
                2L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
                3L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
                4L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
                5L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
                6L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
                7L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
                8L to listOf(Gamer(name = "world"), Gamer(name = "Asdasdas")),
            ),
            playerList = listOf(PlayerResult("zero", 200), PlayerResult("hello", -100))
        )
    )
}