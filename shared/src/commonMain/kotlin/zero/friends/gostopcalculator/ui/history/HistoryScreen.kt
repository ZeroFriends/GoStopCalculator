package zero.friends.gostopcalculator.ui.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Game
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.created_at
import zero.friends.gostopcalculator.shared.generated.resources.delete
import zero.friends.gostopcalculator.shared.generated.resources.delete_dialog_title
import zero.friends.gostopcalculator.shared.generated.resources.empty_game
import zero.friends.gostopcalculator.shared.generated.resources.guide
import zero.friends.gostopcalculator.shared.generated.resources.history
import zero.friends.gostopcalculator.shared.generated.resources.info_new_game_start
import zero.friends.gostopcalculator.shared.generated.resources.new_game
import zero.friends.gostopcalculator.shared.generated.resources.progress
import zero.friends.gostopcalculator.shared.generated.resources.start
import zero.friends.gostopcalculator.shared.generated.resources.today_game
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.EmptyHistory
import zero.friends.gostopcalculator.ui.common.GoStopButton
import zero.friends.gostopcalculator.ui.common.GoStopDivider
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.ui.common.SubTitleText
import zero.friends.gostopcalculator.ui.common.SystemBarAppearance
import zero.friends.gostopcalculator.ui.common.TitleText
import zero.friends.gostopcalculator.ui.dialog.BasicDialog
import zero.friends.gostopcalculator.ui.history.guide.GuideFullScreenDialog
import zero.friends.gostopcalculator.util.GoogleAdmob

private sealed class HistoryEvent {


    object StartGame : HistoryEvent()
    object ShowGuide : HistoryEvent()
    class ShowRound(val game: Game) : HistoryEvent()
    class ShowMore(val game: Game) : HistoryEvent()
}

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onStartGame: () -> Unit = {},
    onShowRound: (Game) -> Unit = {},
    onCloseApp: () -> Unit = {},
    onToggleDebugAd: (() -> Unit)? = null
) {
    BackHandler(enabled = true) {
        onCloseApp()
    }

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
            confirmText = stringResource(Res.string.delete),
            titleText = stringResource(Res.string.delete_dialog_title)
        )
    }

    if (showGuide) {
        GuideFullScreenDialog(onDismiss = { showGuide = false })
    }

    val toggleAd = onToggleDebugAd ?: {
        GoogleAdmob.isDebugAdEnabled = !GoogleAdmob.isDebugAdEnabled
    }

    HistoryScreenContent(
        uiState = uiState,
        isGuideVisible = showGuide,
        event = { event ->
            when (event) {
                is HistoryEvent.ShowRound -> onShowRound(event.game)
                HistoryEvent.ShowGuide -> showGuide = true
                HistoryEvent.StartGame -> onStartGame()
                is HistoryEvent.ShowMore -> dialogGameId = event.game.id
            }
        },
        onToggleDebugAd = toggleAd
    )
}

@Composable
private fun HistoryScreenContent(
    uiState: HistoryUiState,
    isGuideVisible: Boolean = false,
    event: (HistoryEvent) -> Unit = {},
    onToggleDebugAd: (() -> Unit)? = null
) {
    if (!isGuideVisible) {
        SystemBarAppearance(
            isLightStatusBar = true,
            isLightNavigationBar = true,
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            NewGame(
                event = event,
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
    onToggleDebugAd: (() -> Unit)? = null
) {
    Column(Modifier.padding(16.dp)) {
        SubTitleText(stringResource(Res.string.new_game))
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleText(text = stringResource(Res.string.today_game))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val isDebugAdEnabled = GoogleAdmob.isDebugAdEnabled
                val onToggle = onToggleDebugAd ?: {
                    GoogleAdmob.isDebugAdEnabled = !GoogleAdmob.isDebugAdEnabled
                }
                RoundedCornerText(
                    text = if (isDebugAdEnabled) "Ad ON" else "Ad OFF",
                    color = if (isDebugAdEnabled) Color(0xFFFF5252) else Color(0xFF9E9E9E),
                    fontSize = 14.sp,
                    onClick = onToggle
                )
                Spacer(modifier = Modifier.width(8.dp))
                RoundedCornerText(
                    text = stringResource(Res.string.guide),
                    color = Color(0xFFFF5252),
                    fontSize = 14.sp,
                    onClick = { event(HistoryEvent.ShowGuide) }
                )
            }
        }
        Spacer(modifier = Modifier.padding(18.dp))
        GoStopButton(stringResource(Res.string.start), onClick = { event(HistoryEvent.StartGame) })
    }
}

@Composable
private fun History(games: List<Game>, onClick: (Game) -> Unit, onClickMore: (Game) -> Unit) {
    Column(
        Modifier.padding(16.dp)
    ) {
        SubTitleText(text = stringResource(Res.string.history))
        TitleText(text = stringResource(Res.string.progress))
        if (games.isEmpty()) {
            EmptyHistory(
                title = stringResource(Res.string.empty_game),
                subTitle = stringResource(Res.string.info_new_game_start)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RoundLog(game: Game, onClick: () -> Unit = {}, onClickMore: () -> Unit = {}) {
    val hapticFeedback = LocalHapticFeedback.current
    Card(
        elevation = 6.dp,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.padding(vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClickMore()
                    }
                )
                .padding(start = 12.dp, end = 12.dp, top = 15.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Row {
                    Text(
                        text = stringResource(Res.string.created_at),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(text = game.createdAt, color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.padding(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = game.name,
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}
