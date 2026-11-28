package zero.friends.gostopcalculator.ui.board.prepare

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.navigation.LocalNativeTopBarHeight
import zero.friends.gostopcalculator.navigation.LocalUseNativeNavigation
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.complete
import zero.friends.gostopcalculator.shared.generated.resources.fail_over_gamer
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_group
import zero.friends.gostopcalculator.shared.generated.resources.player_list
import zero.friends.gostopcalculator.shared.generated.resources.prepare_create_round_error
import zero.friends.gostopcalculator.shared.generated.resources.prepare_highlight_2_to_4
import zero.friends.gostopcalculator.shared.generated.resources.prepare_participating_badge
import zero.friends.gostopcalculator.shared.generated.resources.prepare_selected_count_format
import zero.friends.gostopcalculator.shared.generated.resources.start
import zero.friends.gostopcalculator.shared.generated.resources.start_description
import zero.friends.gostopcalculator.ui.board.score.common.PlayerIndexBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCanvasBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBadgeBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreHeroCard
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScorePlayerCard
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.background.GoStopButtonBackground

private sealed interface PrepareEvent {
    object Back : PrepareEvent
    object Complete : PrepareEvent
    class OnClickPlayer(val player: Player, val isCheck: Boolean) : PrepareEvent
}

@Composable
fun PrepareScreen(
    prepareViewModel: PrepareViewModel,
    onComplete: (gameId: Long, roundId: Long) -> Unit = { _, _ -> },
    onBack: () -> Unit = {},
    onShowToast: (String) -> Unit = {}
) {
    val scaffoldState = rememberScaffoldState()
    val uiState by prepareViewModel.uiState().collectAsState()
    val failOverGamerAlert = stringResource(Res.string.fail_over_gamer)
    val createRoundErrorAlert = stringResource(Res.string.prepare_create_round_error)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = !uiState.isSubmitting) {
        onBack()
    }

    PrepareScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        event = { event ->
            when (event) {
                PrepareEvent.Back -> {
                    if (!uiState.isSubmitting) {
                        onBack()
                    }
                }
                PrepareEvent.Complete -> {
                    prepareViewModel.createNewRound(
                        onCreate = onComplete,
                        onError = {
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(createRoundErrorAlert)
                            }
                        }
                    )
                }
                is PrepareEvent.OnClickPlayer -> {
                    prepareViewModel.onClickPlayer(event.isCheck, event.player) {
                        onShowToast(failOverGamerAlert)
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(failOverGamerAlert)
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun PrepareScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: PrepareUiState = PrepareUiState(),
    event: (PrepareEvent) -> Unit = {},
) {
    val topBarPadding = if (LocalUseNativeNavigation.current) LocalNativeTopBarHeight.current else 0.dp

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = ScoreCanvasBg,
        topBar = {
            CenterTextTopBar(
                text = uiState.game.name,
                onBack = { event(PrepareEvent.Back) },
                isRed = false,
                swipeBackEnabled = !uiState.isSubmitting,
            )
        }
    ) { innerPadding ->
        GoStopButtonBackground(
            modifier = Modifier.padding(innerPadding),
            buttonString = stringResource(Res.string.complete),
            buttonEnabled = uiState.selectedPlayers.size >= 2 && !uiState.isSubmitting,
            showGradient = true,
            backgroundColor = ScoreCanvasBg,
            onClick = { event(PrepareEvent.Complete) },
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topBarPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item(key = "prepare_hero") {
                    ScoreHeroCard(
                        icon = Res.drawable.ic_hero_group,
                        mainText = stringResource(Res.string.start),
                        subText = stringResource(Res.string.start_description),
                        highlightText = stringResource(Res.string.prepare_highlight_2_to_4)
                    )
                }

                item(key = "prepare_list_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.player_list),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreMutedText
                        )
                        StatusPillBadge(
                            text = stringResource(
                                Res.string.prepare_selected_count_format,
                                uiState.selectedPlayers.size,
                                uiState.players.size
                            ),
                            backgroundColor = ScoreSoftPinkBg,
                            textColor = ScorePrimaryRed,
                            horizontalPadding = 10.dp,
                            verticalPadding = 4.dp,
                            fontSize = 11.5.sp
                        )
                    }
                }

                itemsIndexed(
                    items = uiState.players,
                    key = { _, player -> player.id }
                ) { index: Int, player: Player ->
                    val isCheck = uiState.selectedPlayers.any { player.id == it.id }
                    PlayerPickItem(
                        index = index,
                        player = player,
                        isCheck = isCheck,
                        enabled = !uiState.isSubmitting,
                        onClick = { event(PrepareEvent.OnClickPlayer(player, !isCheck)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerPickItem(
    index: Int,
    player: Player,
    isCheck: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    ScorePlayerCard(
        isHighlighted = isCheck,
        enabled = enabled,
        onClick = onClick,
        clickRole = Role.Checkbox,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                PlayerIndexBadge(
                    index = index,
                    containerColor = if (isCheck) ScoreSoftPinkBg else ScoreDisabledBadgeBg,
                    textColor = if (isCheck) ScorePrimaryRed else ScoreSubtleGray,
                    size = 26.dp,
                    shape = CircleShape
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = player.name,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCheck) ScoreTextDark else ScoreMutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (isCheck) {
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusPillBadge(
                        text = stringResource(Res.string.prepare_participating_badge),
                        backgroundColor = ScoreSoftPinkBg,
                        textColor = ScorePrimaryRed,
                        horizontalPadding = 8.dp,
                        verticalPadding = 2.dp,
                        fontSize = 11.sp
                    )
                }
            }
            SelectionCheckCircle(checked = isCheck)
        }
    }
}

@Composable
private fun SelectionCheckCircle(checked: Boolean) {
    if (checked) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(ScorePrimaryRed),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(12.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.14f, size.height * 0.52f)
                    lineTo(size.width * 0.42f, size.height * 0.80f)
                    lineTo(size.width * 0.88f, size.height * 0.24f)
                }
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.5.dp, ScoreDisabledBorder, CircleShape)
        )
    }
}
