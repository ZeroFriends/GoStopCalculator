package zero.friends.gostopcalculator.ui.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Game
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.created_at
import zero.friends.gostopcalculator.shared.generated.resources.delete
import zero.friends.gostopcalculator.shared.generated.resources.delete_caution_badge
import zero.friends.gostopcalculator.shared.generated.resources.delete_dialog_title
import zero.friends.gostopcalculator.shared.generated.resources.delete_game_action
import zero.friends.gostopcalculator.shared.generated.resources.empty_game
import zero.friends.gostopcalculator.shared.generated.resources.game_more_cd_format
import zero.friends.gostopcalculator.shared.generated.resources.guide
import zero.friends.gostopcalculator.shared.generated.resources.history
import zero.friends.gostopcalculator.shared.generated.resources.history_reorder_hint
import zero.friends.gostopcalculator.shared.generated.resources.ic_menu_book
import zero.friends.gostopcalculator.shared.generated.resources.ic_more_black
import zero.friends.gostopcalculator.shared.generated.resources.info_new_game_start
import zero.friends.gostopcalculator.shared.generated.resources.manage_game_label
import zero.friends.gostopcalculator.shared.generated.resources.manual_entry_home
import zero.friends.gostopcalculator.shared.generated.resources.move_down_action
import zero.friends.gostopcalculator.shared.generated.resources.move_up_action
import zero.friends.gostopcalculator.shared.generated.resources.new_game
import zero.friends.gostopcalculator.shared.generated.resources.progress
import zero.friends.gostopcalculator.shared.generated.resources.rename_confirm
import zero.friends.gostopcalculator.shared.generated.resources.rename_game_action
import zero.friends.gostopcalculator.shared.generated.resources.rename_game_helper
import zero.friends.gostopcalculator.shared.generated.resources.rename_game_placeholder
import zero.friends.gostopcalculator.shared.generated.resources.start
import zero.friends.gostopcalculator.shared.generated.resources.today_game
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBorder
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.EmptyHistory
import zero.friends.gostopcalculator.ui.common.GoStopButton
import zero.friends.gostopcalculator.ui.common.GoStopDivider
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.ui.common.SubTitleText
import zero.friends.gostopcalculator.ui.common.SystemBarAppearance
import zero.friends.gostopcalculator.ui.common.TitleText
import zero.friends.gostopcalculator.ui.dialog.BasicDialog
import zero.friends.gostopcalculator.ui.dialog.ItemActionBottomSheet
import zero.friends.gostopcalculator.ui.dialog.RenameDialog
import zero.friends.gostopcalculator.ui.history.guide.GuideFullScreenDialog
import zero.friends.gostopcalculator.util.GoogleAdmob

private sealed class HistoryEvent {


    object StartGame : HistoryEvent()
    object ShowGuide : HistoryEvent()
    object ShowManual : HistoryEvent()
    class ShowRound(val game: Game) : HistoryEvent()
    class ShowMore(val game: Game) : HistoryEvent()
    class ReorderGames(val orderedGameIds: List<Long>) : HistoryEvent()
}

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onStartGame: () -> Unit = {},
    onShowRound: (Game) -> Unit = {},
    onOpenManual: () -> Unit = {},
    onCloseApp: () -> Unit = {},
    showDebugAdToggle: Boolean = false,
    onToggleDebugAd: (() -> Unit)? = null
) {
    val uiState by viewModel.getUiState().collectAsState()
    var actionSheetGameId by rememberSaveable { mutableStateOf<Long?>(null) }
    var lastSheetGameId by rememberSaveable { mutableStateOf<Long?>(null) }
    var renamingGameId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deletingGameId by rememberSaveable { mutableStateOf<Long?>(null) }
    var manualShowGuide by rememberSaveable {
        mutableStateOf(false)
    }
    val showGuide = manualShowGuide || uiState.isFirstStart

    val actionSheetGame = actionSheetGameId?.let { id -> uiState.games.firstOrNull { it.id == id } }
    val sheetGame = (actionSheetGameId ?: lastSheetGameId)?.let { id ->
        uiState.games.firstOrNull { it.id == id }
    }
    val renameTarget = renamingGameId?.let { id -> uiState.games.firstOrNull { it.id == id } }

    BackHandler(enabled = actionSheetGame == null) {
        onCloseApp()
    }

    val deleteTargetId = deletingGameId
    if (deleteTargetId != null) {
        BasicDialog(
            onDismiss = { deletingGameId = null },
            onClick = {
                viewModel.deleteGame(deleteTargetId)
                deletingGameId = null
            },
            confirmText = stringResource(Res.string.delete),
            titleText = stringResource(Res.string.delete_dialog_title)
        )
    }

    if (renameTarget != null) {
        RenameDialog(
            title = stringResource(Res.string.rename_game_action),
            initialValue = renameTarget.name,
            placeholder = stringResource(Res.string.rename_game_placeholder),
            helperText = stringResource(
                Res.string.rename_game_helper,
                HistoryViewModel.MAX_GAME_NAME_LENGTH,
            ),
            maxLength = HistoryViewModel.MAX_GAME_NAME_LENGTH,
            allowBlank = false,
            confirmText = stringResource(Res.string.rename_confirm),
            onConfirm = { newName ->
                viewModel.renameGame(renameTarget.id, newName)
                renamingGameId = null
            },
            onDismiss = { renamingGameId = null },
        )
    }

    if (showGuide) {
        GuideFullScreenDialog(
            onDismiss = {
                manualShowGuide = false
                viewModel.dismissGuide()
            }
        )
    }

    val toggleAd = onToggleDebugAd ?: {
        GoogleAdmob.isDebugAdEnabled = !GoogleAdmob.isDebugAdEnabled
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HistoryScreenContent(
            uiState = uiState,
            isGuideVisible = showGuide,
            showDebugAdToggle = showDebugAdToggle,
            event = { event ->
                when (event) {
                    is HistoryEvent.ShowRound -> onShowRound(event.game)
                    HistoryEvent.ShowGuide -> manualShowGuide = true
                    HistoryEvent.ShowManual -> onOpenManual()
                    HistoryEvent.StartGame -> onStartGame()
                    is HistoryEvent.ShowMore -> {
                        lastSheetGameId = event.game.id
                        actionSheetGameId = event.game.id
                    }
                    is HistoryEvent.ReorderGames -> viewModel.reorderGames(event.orderedGameIds)
                }
            },
            onToggleDebugAd = toggleAd
        )

        if (sheetGame != null) {
            ItemActionBottomSheet(
                visible = actionSheetGame != null,
                eyebrow = stringResource(Res.string.manage_game_label),
                title = sheetGame.name,
                subtitle = sheetGame.createdAt,
                renameLabel = stringResource(Res.string.rename_game_action),
                deleteLabel = stringResource(Res.string.delete_game_action),
                deleteCautionLabel = stringResource(Res.string.delete_caution_badge),
                onRename = {
                    if (actionSheetGameId != null) {
                        val targetId = sheetGame.id
                        actionSheetGameId = null
                        renamingGameId = targetId
                    }
                },
                onDelete = {
                    if (actionSheetGameId != null) {
                        val targetId = sheetGame.id
                        actionSheetGameId = null
                        deletingGameId = targetId
                    }
                },
                onDismiss = { actionSheetGameId = null },
            )
        }
    }
}

@Composable
private fun HistoryScreenContent(
    uiState: HistoryUiState,
    isGuideVisible: Boolean = false,
    showDebugAdToggle: Boolean = false,
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
                showDebugAdToggle = showDebugAdToggle,
                onToggleDebugAd = onToggleDebugAd
            )

            GoStopDivider(Modifier.padding(vertical = 16.dp))

            History(
                games = uiState.games,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onClick = { event(HistoryEvent.ShowRound(it)) },
                onClickMore = { event(HistoryEvent.ShowMore(it)) },
                onReorderGames = { event(HistoryEvent.ReorderGames(it)) },
            )
        }
    }
}

@Composable
private fun NewGame(
    event: (HistoryEvent) -> Unit,
    showDebugAdToggle: Boolean = false,
    onToggleDebugAd: (() -> Unit)? = null
) {
    Column(Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SubTitleText(stringResource(Res.string.new_game))
            if (showDebugAdToggle) {
                val isDebugAdEnabled = GoogleAdmob.isDebugAdEnabled
                val onToggle = onToggleDebugAd ?: {
                    GoogleAdmob.isDebugAdEnabled = !GoogleAdmob.isDebugAdEnabled
                }
                RoundedCornerText(
                    text = if (isDebugAdEnabled) "Ad ON" else "Ad OFF",
                    color = if (isDebugAdEnabled) Color(0xFFFF5252) else Color(0xFF9E9E9E),
                    fontSize = 12.sp,
                    onClick = onToggle
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                TitleText(text = stringResource(Res.string.today_game))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Surface(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 36.dp)
                        .clip(CircleShape)
                        .testTag("history_manual_button")
                        .clickable(role = Role.Button, onClick = { event(HistoryEvent.ShowManual) }),
                    shape = CircleShape,
                    color = ScoreSoftPinkBg,
                    border = BorderStroke(1.dp, ScoreSoftPinkBorder),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_menu_book),
                            contentDescription = null,
                            tint = ScorePrimaryRed,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(Res.string.manual_entry_home),
                            color = ScorePrimaryRed,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
                RoundedCornerText(
                    text = stringResource(Res.string.guide),
                    color = Color(0xFFFF5252),
                    fontSize = 14.sp,
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp),
                    onClick = { event(HistoryEvent.ShowGuide) }
                )
            }
        }
        Spacer(modifier = Modifier.padding(18.dp))
        GoStopButton(stringResource(Res.string.start), onClick = { event(HistoryEvent.StartGame) })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun History(
    games: List<Game>,
    modifier: Modifier = Modifier,
    onClick: (Game) -> Unit,
    onClickMore: (Game) -> Unit,
    onReorderGames: (List<Long>) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
        ) {
            SubTitleText(text = stringResource(Res.string.history))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    TitleText(text = stringResource(Res.string.progress))
                }
                if (games.size >= 2) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.history_reorder_hint),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ScoreMutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        if (games.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                EmptyHistory(
                    title = stringResource(Res.string.empty_game),
                    subTitle = stringResource(Res.string.info_new_game_start)
                )
            }
        } else {
            val lazyListState = rememberLazyListState()
            val hapticFeedback = LocalHapticFeedback.current
            val touchSlop = LocalViewConfiguration.current.touchSlop
            val density = LocalDensity.current
            val edgeThresholdPx = with(density) { 56.dp.toPx() }
            val maxAutoScrollStepPx = with(density) { 14.dp.toPx() }

            var displayGames by remember { mutableStateOf(games) }
            val latestGames by rememberUpdatedState(games)
            val latestOnReorderGames by rememberUpdatedState(onReorderGames)

            val dragState = rememberHistoryDragDropState(
                lazyListState = lazyListState,
                touchSlop = touchSlop,
                currentIdsProvider = { displayGames.map { it.id } },
                onMove = { fromIndex, toIndex ->
                    if (fromIndex in displayGames.indices && toIndex in displayGames.indices) {
                        val mutable = displayGames.toMutableList()
                        val moved = mutable.removeAt(fromIndex)
                        mutable.add(toIndex, moved)
                        displayGames = mutable
                        val firstIndex = lazyListState.firstVisibleItemIndex
                        val firstOffset = lazyListState.firstVisibleItemScrollOffset
                        if (fromIndex == firstIndex || toIndex == firstIndex) {
                            lazyListState.requestScrollToItem(firstIndex, firstOffset)
                        }
                    }
                },
                onCommit = {
                    val orderedIds = displayGames.map { it.id }
                    if (orderedIds != latestGames.map { it.id }) {
                        latestOnReorderGames(orderedIds)
                    }
                },
                onCancel = {
                    displayGames = latestGames
                },
            )

            val renderGames = if (dragState.isDragging) displayGames else games

            LaunchedEffect(dragState.draggingItemId) {
                if (dragState.draggingItemId == null) return@LaunchedEffect
                var lastFrameTime = 0L
                while (dragState.draggingItemId != null) {
                    val frameTime = withFrameMillis { it }
                    val frameScale = if (lastFrameTime == 0L) {
                        1f
                    } else {
                        ((frameTime - lastFrameTime).coerceIn(0L, 48L) / 16f)
                    }
                    lastFrameTime = frameTime
                    val scrollDelta = dragState.computeEdgeScrollDelta(
                        edgeThresholdPx = edgeThresholdPx,
                        maxStepPx = maxAutoScrollStepPx * frameScale,
                    )
                    if (scrollDelta != 0f) {
                        val consumed = lazyListState.scrollBy(scrollDelta)
                        dragState.onAutoScroll(consumed)
                    }
                }
            }

            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(dragState) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            if (latestGames.size < 2) return@awaitEachGesture
                            val longPress = awaitLongPressOrCancellation(down.id) ?: return@awaitEachGesture
                            if (!dragState.containsItemAt(longPress.position)) {
                                return@awaitEachGesture
                            }
                            displayGames = latestGames
                            val startedDrag = dragState.onDragStart(longPress.position)
                            if (!startedDrag) {
                                return@awaitEachGesture
                            }
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            try {
                                while (true) {
                                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                                    val change = event.changes.firstOrNull { it.id == down.id }
                                    if (change == null) {
                                        dragState.onDragCancel()
                                        break
                                    }
                                    val deltaY = change.position.y - change.previousPosition.y
                                    change.consume()
                                    if (!change.pressed) {
                                        dragState.onDragEnd()
                                        break
                                    }
                                    if (deltaY != 0f) {
                                        dragState.onDrag(deltaY)
                                    }
                                }
                            } catch (ce: CancellationException) {
                                dragState.onDragCancel()
                                throw ce
                            }
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(
                    items = renderGames,
                    key = { _, game -> game.id },
                    contentType = { _, _ -> "game_card" },
                ) { index, game ->
                    val isDragging = dragState.draggingItemId == game.id
                    val itemModifier = if (isDragging) {
                        Modifier
                            .zIndex(1f)
                            .graphicsLayer {
                                translationY = dragState.draggingItemOffset
                                scaleX = 1.02f
                                scaleY = 1.02f
                            }
                    } else {
                        Modifier.animateItem()
                    }
                    RoundLog(
                        index = index,
                        game = game,
                        isDragging = isDragging,
                        canMoveUp = index > 0,
                        canMoveDown = index < renderGames.lastIndex,
                        modifier = itemModifier,
                        onClick = {
                            if (!dragState.isDragging) onClick(game)
                        },
                        onClickMore = {
                            if (!dragState.isDragging) onClickMore(game)
                        },
                        onMoveUp = {
                            if (index > 0) {
                                val swapped = renderGames.toMutableList()
                                val item = swapped.removeAt(index)
                                swapped.add(index - 1, item)
                                onReorderGames(swapped.map { it.id })
                            }
                        },
                        onMoveDown = {
                            if (index < renderGames.lastIndex) {
                                val swapped = renderGames.toMutableList()
                                val item = swapped.removeAt(index)
                                swapped.add(index + 1, item)
                                onReorderGames(swapped.map { it.id })
                            }
                        },
                    )
                }
                item(key = "history_bottom_spacer", contentType = "spacer") {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }
}

@Composable
private fun RoundLog(
    index: Int,
    game: Game,
    isDragging: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onClickMore: () -> Unit = {},
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
) {
    val cardShape = RoundedCornerShape(18.dp)
    val elevation by animateDpAsState(if (isDragging) 12.dp else 6.dp)
    val moveUpLabel = stringResource(Res.string.move_up_action)
    val moveDownLabel = stringResource(Res.string.move_down_action)
    Card(
        elevation = elevation,
        shape = cardShape,
        border = if (isDragging) BorderStroke(1.5.dp, ScorePrimaryRed) else null,
        modifier = modifier.padding(vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .semantics {
                    if (!isDragging) {
                        val actions = buildList {
                            if (canMoveUp) {
                                add(CustomAccessibilityAction(moveUpLabel) { onMoveUp(); true })
                            }
                            if (canMoveDown) {
                                add(CustomAccessibilityAction(moveDownLabel) { onMoveDown(); true })
                            }
                        }
                        if (actions.isNotEmpty()) {
                            customActions = actions
                        }
                    }
                }
                .clickable(enabled = !isDragging, onClick = onClick)
                .padding(start = 12.dp, end = 6.dp, top = 15.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            ) {
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
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .sizeIn(minWidth = 44.dp, minHeight = 44.dp)
                    .clip(CircleShape)
                    .testTag("game_more_button_${index}")
                    .clickable(enabled = !isDragging, role = Role.Button, onClick = onClickMore),
                shape = CircleShape,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.ic_more_black),
                        contentDescription = stringResource(Res.string.game_more_cd_format, game.name),
                    )
                }
            }
        }
    }
}
