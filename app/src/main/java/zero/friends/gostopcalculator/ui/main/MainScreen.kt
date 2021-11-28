package zero.friends.gostopcalculator.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.Game
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.*

private sealed class MainEvent {
    object StartGame : MainEvent()
    object ShowGuide : MainEvent()
    class ShowGame(val game: Game) : MainEvent()
    class ShowMore(val game: Game) : MainEvent()
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onStartGame: () -> Unit = {},
    onShowGame: (Game) -> Unit = {}
) {
    val uiState by viewModel.getUiState().collectAsState()
    val dialogState by viewModel.getDialogState().collectAsState()

    if (dialogState.openDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeDialog() },
            title = { Text(text = "삭제하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    val gameId = dialogState.gameId
                    requireNotNull(gameId)
                    viewModel.deleteGame(gameId)
                    viewModel.closeDialog()
                }) {
                    Text(text = "삭제")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.closeDialog() }) {
                    Text(text = "취소")
                }
            }
        )
    }

    MainScreen(uiState) { event ->
        when (event) {
            is MainEvent.ShowGame -> {
                onShowGame(event.game)
            }
            MainEvent.ShowGuide -> {
                //TODO on Show guide
            }
            MainEvent.StartGame -> {
                onStartGame()
            }
            is MainEvent.ShowMore -> {
                viewModel.openDialog(event.game.id)
            }
        }
    }
}

@Composable
private fun MainScreen(uiState: MainUiState, event: (MainEvent) -> Unit = {}) {
    Column {
        NewGame(event)

        Divider(
            color = colorResource(id = R.color.gray),
            thickness = 10.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        History(
            uiState.games,
            onClick = { event(MainEvent.ShowGame(it)) },
            onClickMore = { event(MainEvent.ShowMore(it)) })
    }
}

@Composable
private fun NewGame(event: (MainEvent) -> Unit) {
    Column(Modifier.padding(16.dp)) {
        SubTitleText("NEW GAME")
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleText(text = stringResource(id = R.string.today_game))
            RoundedCornerText(
                text = stringResource(id = R.string.guide),
                color = colorResource(id = R.color.orangey_red),
                fontSize = 14.sp,
                onButtonClicked = { event(MainEvent.ShowGuide) }
            )
        }
        Spacer(modifier = Modifier.padding(18.dp))
        GoStopButton(stringResource(id = R.string.start), onClick = { event(MainEvent.StartGame) })
    }
}


@Composable
private fun History(games: List<Game>, onClick: (Game) -> Unit, onClickMore: (Game) -> Unit) {
    Column(
        Modifier.padding(16.dp)
    ) {
        SubTitleText(text = stringResource(id = R.string.history))
        TitleText(text = stringResource(id = R.string.progress))
        if (games.isEmpty()) {
            EmptyHistory(
                painter = painterResource(id = R.drawable.ic_onodofu),
                title = stringResource(id = R.string.empty_game),
                subTitle = stringResource(id = R.string.info_new_game_start)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp)
            ) {
                items(games) { game ->
                    GameLog(game, onClick = { onClick(game) }, onClickMore = { onClickMore(game) })
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun GameLogPreview() {
    GameLog(Game(0, "gameTitle", "2021.11.21"))
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GameLog(game: Game, onClick: () -> Unit = {}, onClickMore: () -> Unit = {}) {
    Card(
        elevation = 6.dp,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.padding(vertical = 6.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 15.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Row {
                    Text(
                        text = stringResource(R.string.created_at),
                        color = colorResource(id = R.color.gray),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(text = game.createdAt, color = colorResource(id = R.color.gray), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.padding(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = game.name,
                        color = colorResource(id = R.color.nero),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                }

            }
            Icon(
                painter = painterResource(id = R.drawable.ic_more_black),
                contentDescription = null,
                modifier = Modifier.clickable(onClick = onClickMore)
            )
        }
    }
}


@Preview("MainPreview", showBackground = true)
@Composable
private fun MainPreview() {
    MainScreen(MainUiState())
}

@Preview("MainPreviewWithItem", showBackground = true)
@Composable
private fun MainPreviewWithItem() {
    MainScreen(
        MainUiState(
            games = listOf(
                Game(1, "첫 번째 게임", "2021.11.22"),
                Game(2, "두 번째 게임", "2021.11.23"),
                Game(3, "세 번째 게임", "2021.11.24"),
                Game(4, "네 번째 게임", "2021.11.25")
            )
        )
    )
}