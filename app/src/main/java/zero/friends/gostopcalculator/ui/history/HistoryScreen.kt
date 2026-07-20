package zero.friends.gostopcalculator.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import zero.friends.gostopcalculator.BuildConfig
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.*
import zero.friends.gostopcalculator.ui.dialog.BasicDialog
import zero.friends.gostopcalculator.ui.history.guide.GuideFullScreenDialog

private sealed class HistoryEvent {
    object StartGame : HistoryEvent()
    object ShowGuide : HistoryEvent()
    class ShowRound(val game: Game) : HistoryEvent()
    class ShowMore(val game: Game) : HistoryEvent()
}

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onStartGame: () -> Unit = {},
    onShowRound: (Game) -> Unit = {},
    isDebugAdEnabled: Boolean = true,
    onToggleDebugAd: (() -> Unit)? = null
) {
    val uiState by viewModel.getUiState().collectAsState()
    var dialogGameId by remember {
        mutableStateOf<Long?>(null)
    }
    var showGuide by remember {
        mutableStateOf(uiState.isFirstStart)
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
        GuideFullScreenDialog(onDismiss = { showGuide = false })
    }

    HistoryScreenContent(
        uiState = uiState,
        event = { event ->
            when (event) {
                is HistoryEvent.ShowRound -> onShowRound(event.game)
                HistoryEvent.ShowGuide -> showGuide = true
                HistoryEvent.StartGame -> onStartGame()
                is HistoryEvent.ShowMore -> dialogGameId = event.game.id
            }
        },
        isDebugAdEnabled = isDebugAdEnabled,
        onToggleDebugAd = onToggleDebugAd
    )
}

@Composable
private fun HistoryScreenContent(
    uiState: HistoryUiState,
    event: (HistoryEvent) -> Unit = {},
    isDebugAdEnabled: Boolean = true,
    onToggleDebugAd: (() -> Unit)? = null
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            NewGame(
                event = event,
                isDebugAdEnabled = isDebugAdEnabled,
                onToggleDebugAd = onToggleDebugAd
            )

            GoStopDivider(Modifier.padding(vertical = 16.dp))

            History(
                games = uiState.games,
                onClick = { event(HistoryEvent.ShowRound(it)) },
                onClickMore = { event(HistoryEvent.ShowMore(it)) }
            )
        }
    }
}

@Composable
private fun NewGame(
    event: (HistoryEvent) -> Unit,
    isDebugAdEnabled: Boolean = true,
    onToggleDebugAd: (() -> Unit)? = null
) {
    Column(Modifier.padding(16.dp)) {
        SubTitleText(stringResource(R.string.new_game))
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleText(text = stringResource(id = R.string.today_game))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (BuildConfig.DEBUG && onToggleDebugAd != null) {
                    RoundedCornerText(
                        text = if (isDebugAdEnabled) "Ad ON" else "Ad OFF",
                        color = if (isDebugAdEnabled) colorResource(id = R.color.orangey_red) else colorResource(id = R.color.dusty_gray),
                        fontSize = 14.sp,
                        onClick = onToggleDebugAd
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                RoundedCornerText(
                    text = stringResource(id = R.string.guide),
                    color = colorResource(id = R.color.orangey_red),
                    fontSize = 14.sp,
                    onClick = { event(HistoryEvent.ShowGuide) }
                )
            }
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
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = games, key = { it.id }) { game ->
                    RoundLog(game, onClick = { onClick(game) }, onClickMore = { onClickMore(game) })
                }
                item {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RoundLog(game: Game, onClick: () -> Unit = {}, onClickMore: () -> Unit = {}) {
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
    HistoryScreenContent(HistoryUiState())
}

@Preview("HistoryPreviewWithItem", showBackground = true)
@Composable
private fun HistoryPreviewWithItem() {
    HistoryScreenContent(
        HistoryUiState(
            games = listOf(
                Game(1, "즐거운 고스톱", "2022-01-01")
            )
        )
    )
}
