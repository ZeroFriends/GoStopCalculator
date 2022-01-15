package zero.friends.gostopcalculator.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import zero.friends.gostopcalculator.ui.history.guide.GuideScreen
import zero.friends.gostopcalculator.ui.common.*
import zero.friends.gostopcalculator.ui.dialog.BasicDialog

private sealed class HistoryEvent {
    object StartGame : HistoryEvent()
    object ShowGuide : HistoryEvent()
    class ShowGame(val game: Game) : HistoryEvent()
    class ShowMore(val game: Game) : HistoryEvent()
}

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onStartGame: () -> Unit = {},
    onShowGame: (Game) -> Unit = {}
) {
    val uiState by viewModel.getUiState().collectAsState()
    var dialogGameId by remember {
        mutableStateOf<Long?>(null)
    }
    var showGuide by remember {
        mutableStateOf(false)
    }

    val value = dialogGameId
    if (value != null) {
        BasicDialog(
            onDismiss = { dialogGameId = null },
            onClick = {
                viewModel.deleteGame(value)
                dialogGameId = null
            },
            confirmText = stringResource(R.string.delete),
            titleText = stringResource(R.string.delete_dialog_title)
        )
    }

    if (showGuide) {
        GuideScreen(onDismiss = { showGuide = false })
    }

    HistoryScreen(uiState) { event ->
        when (event) {
            is HistoryEvent.ShowGame -> {
                onShowGame(event.game)
            }
            HistoryEvent.ShowGuide -> {
                showGuide = true
            }
            HistoryEvent.StartGame -> {
                onStartGame()
            }
            is HistoryEvent.ShowMore -> {
                dialogGameId = event.game.id
            }
        }
    }
}

@Composable
private fun HistoryScreen(uiState: HistoryUiState, event: (HistoryEvent) -> Unit = {}) {
    Column(modifier = Modifier.background(color = colorResource(id = R.color.white))) {
        NewGame(event)

        Divider(
            color = colorResource(id = R.color.light_gray),
            thickness = 10.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        History(
            uiState.games,
            onClick = { event(HistoryEvent.ShowGame(it)) },
            onClickMore = { event(HistoryEvent.ShowMore(it)) })
    }
}

@Composable
private fun NewGame(event: (HistoryEvent) -> Unit) {
    Column(Modifier.padding(16.dp)) {
        SubTitleText(stringResource(R.string.new_game))
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
                onClick = { event(HistoryEvent.ShowGuide) }
            )
        }
        Spacer(modifier = Modifier.padding(18.dp))
        GoStopButton(stringResource(id = R.string.start), onClick = { event(HistoryEvent.StartGame) })
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
                items(items = games, key = { it.id }) { game ->
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


@Preview("HistoryPreview", showBackground = true)
@Composable
private fun HistoryPreview() {
    HistoryScreen(HistoryUiState())
}

@Preview("HistoryPreviewWithItem", showBackground = true)
@Composable
private fun HistoryPreviewWithItem() {
    HistoryScreen(
        HistoryUiState(
            games = listOf(
                Game(1, "첫 번째 게임", "2021.11.22"),
                Game(2, "두 번째 게임", "2021.11.23"),
                Game(3, "세 번째 게임", "2021.11.24"),
                Game(4, "네 번째 게임", "2021.11.25")
            )
        )
    )
}