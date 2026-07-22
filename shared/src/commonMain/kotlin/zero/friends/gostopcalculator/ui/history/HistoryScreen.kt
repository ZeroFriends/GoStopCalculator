package zero.friends.gostopcalculator.ui.history

import androidx.compose.foundation.clickable
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
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.Game
import zero.friends.gostopcalculator.ui.common.EmptyHistory
import zero.friends.gostopcalculator.ui.common.GoStopButton
import zero.friends.gostopcalculator.ui.common.GoStopDivider
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.ui.common.SubTitleText
import zero.friends.gostopcalculator.ui.common.TitleText
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
    viewModel: HistoryViewModel,
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
            confirmText = "삭제",
            titleText = "게임을 삭제하시겠습니까?"
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
        SubTitleText("새로운 게임")
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleText(text = "오늘의 게임")
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onToggleDebugAd != null) {
                    RoundedCornerText(
                        text = if (isDebugAdEnabled) "Ad ON" else "Ad OFF",
                        color = if (isDebugAdEnabled) Color(0xFFFF5252) else Color(0xFF9E9E9E),
                        fontSize = 14.sp,
                        onClick = onToggleDebugAd
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                RoundedCornerText(
                    text = "가이드",
                    color = Color(0xFFFF5252),
                    fontSize = 14.sp,
                    onClick = { event(HistoryEvent.ShowGuide) }
                )
            }
        }
        Spacer(modifier = Modifier.padding(18.dp))
        GoStopButton("시작하기", onClick = { event(HistoryEvent.StartGame) })
    }
}

@Composable
private fun History(games: List<Game>, onClick: (Game) -> Unit, onClickMore: (Game) -> Unit) {
    Column(
        Modifier.padding(16.dp)
    ) {
        SubTitleText(text = "기록")
        TitleText(text = "진행중인 게임")
        if (games.isEmpty()) {
            EmptyHistory(
                title = "진행중인 게임이 없습니다.",
                subTitle = "새로운 게임을 시작해보세요!"
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
                        text = "생성일",
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
