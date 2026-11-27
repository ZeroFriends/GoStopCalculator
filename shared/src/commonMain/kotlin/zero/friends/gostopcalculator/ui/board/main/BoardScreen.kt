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
import androidx.compose.runtime.saveable.rememberSaveable
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
import zero.friends.gostopcalculator.shared.generated.resources.delete_caution_badge
import zero.friends.gostopcalculator.shared.generated.resources.delete_dialog_title
import zero.friends.gostopcalculator.shared.generated.resources.delete_round_action
import zero.friends.gostopcalculator.shared.generated.resources.empty_game_log
import zero.friends.gostopcalculator.shared.generated.resources.game_history
import zero.friends.gostopcalculator.shared.generated.resources.ic_error_outline_black
import zero.friends.gostopcalculator.shared.generated.resources.income_history
import zero.friends.gostopcalculator.shared.generated.resources.info_game_start
import zero.friends.gostopcalculator.shared.generated.resources.manage_round_label
import zero.friends.gostopcalculator.shared.generated.resources.rename_confirm
import zero.friends.gostopcalculator.shared.generated.resources.rename_round_action
import zero.friends.gostopcalculator.shared.generated.resources.rename_round_helper_format
import zero.friends.gostopcalculator.shared.generated.resources.rename_round_placeholder
import zero.friends.gostopcalculator.shared.generated.resources.round_number_format
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
import zero.friends.gostopcalculator.ui.dialog.ItemActionBottomSheet
import zero.friends.gostopcalculator.ui.dialog.RenameDialog

private data class RoundActionTarget(
    val roundId: Long,
    val roundNumber: Int,
    val roundName: String,
)

private sealed interface BoardEvent {


    object Back : BoardEvent
    object StartGame : BoardEvent
    class Detail(val roundId: Long) : BoardEvent
    class More(val target: RoundActionTarget) : BoardEvent
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
    val scaffoldState = rememberScaffoldState()

    val uiState by boardViewModel.getUiState().collectAsState()
    var actionSheetRoundId by rememberSaveable { mutableStateOf<Long?>(null) }
    var lastSheetRoundId by rememberSaveable { mutableStateOf<Long?>(null) }
    var renamingRoundId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deletingRoundId by rememberSaveable { mutableStateOf<Long?>(null) }

    val orderedRoundIds = uiState.gameHistories.keys.toList()
    fun resolveRoundTarget(roundId: Long?): RoundActionTarget? {
        if (roundId == null) return null
        val index = orderedRoundIds.indexOf(roundId)
        if (index < 0) return null
        return RoundActionTarget(
            roundId = roundId,
            roundNumber = index + 1,
            roundName = uiState.roundNames[roundId].orEmpty(),
        )
    }

    val actionSheetRound = resolveRoundTarget(actionSheetRoundId)
    val sheetTarget = resolveRoundTarget(actionSheetRoundId ?: lastSheetRoundId)
    val renameTarget = resolveRoundTarget(renamingRoundId)

    BackHandler(enabled = actionSheetRound == null) {
        onBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BoardScreen(
            scaffoldState = scaffoldState,
            uiState = uiState,
            isBottomSheetVisible = actionSheetRound != null,
        ) { event ->
            if (actionSheetRound != null) return@BoardScreen
            when (event) {
                BoardEvent.Back -> onBack()
                BoardEvent.StartGame -> onNext(uiState.game.id)
                is BoardEvent.Detail -> {
                    openDetailScreen(uiState.game.id, event.roundId)
                }

                is BoardEvent.More -> {
                    lastSheetRoundId = event.target.roundId
                    actionSheetRoundId = event.target.roundId
                }

                BoardEvent.OpenCalculated -> openCalculated(uiState.game.id)
                BoardEvent.OpenRule -> openRule(uiState.game.id)
            }
        }

        if (sheetTarget != null) {
            val defaultTitle = stringResource(Res.string.round_number_format, sheetTarget.roundNumber)
            val headerTitle = if (sheetTarget.roundName.isNotBlank()) {
                "$defaultTitle · ${sheetTarget.roundName}"
            } else {
                defaultTitle
            }
            ItemActionBottomSheet(
                visible = actionSheetRound != null,
                eyebrow = stringResource(Res.string.manage_round_label),
                title = headerTitle,
                renameLabel = stringResource(Res.string.rename_round_action),
                deleteLabel = stringResource(Res.string.delete_round_action),
                deleteCautionLabel = stringResource(Res.string.delete_caution_badge),
                onRename = {
                    if (actionSheetRoundId != null) {
                        val targetId = sheetTarget.roundId
                        actionSheetRoundId = null
                        renamingRoundId = targetId
                    }
                },
                onDelete = {
                    if (actionSheetRoundId != null) {
                        val roundId = sheetTarget.roundId
                        actionSheetRoundId = null
                        deletingRoundId = roundId
                    }
                },
                onDismiss = { actionSheetRoundId = null },
            )
        }
    }

    if (renameTarget != null) {
        val defaultTitle = stringResource(Res.string.round_number_format, renameTarget.roundNumber)
        RenameDialog(
            title = stringResource(Res.string.rename_round_action),
            badgeText = defaultTitle,
            initialValue = renameTarget.roundName,
            placeholder = stringResource(Res.string.rename_round_placeholder),
            helperText = stringResource(Res.string.rename_round_helper_format, defaultTitle),
            maxLength = BoardViewModel.MAX_ROUND_NAME_LENGTH,
            allowBlank = true,
            confirmText = stringResource(Res.string.rename_confirm),
            onConfirm = { newName ->
                boardViewModel.renameRound(renameTarget.roundId, newName)
                renamingRoundId = null
            },
            onDismiss = { renamingRoundId = null },
        )
    }

    if (deletingRoundId != null) {
        BasicDialog(
            onDismiss = {
                deletingRoundId = null
            },
            onClick = {
                val roundId = deletingRoundId
                deletingRoundId = null
                if (roundId != null) {
                    boardViewModel.deleteRound(roundId)
                }
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
    isBottomSheetVisible: Boolean = false,
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
                onAction = { event(BoardEvent.OpenRule) },
                swipeBackEnabled = !isBottomSheetVisible,
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
                    itemsIndexed(
                        items = histories,
                        key = { _, (roundId, _) -> roundId },
                        contentType = { _, _ -> "round_box" },
                    ) { index, (roundId, gamer) ->
                        val roundIndex = uiState.gameHistories.size - index - 1
                        val customName = uiState.roundNames[roundId].orEmpty()
                        RoundBox(
                            index = roundIndex,
                            roundName = customName,
                            gamers = gamer,
                            onClickDetail = { event(BoardEvent.Detail(roundId)) },
                            onClickMore = {
                                event(
                                    BoardEvent.More(
                                        RoundActionTarget(
                                            roundId = roundId,
                                            roundNumber = roundIndex + 1,
                                            roundName = customName,
                                        )
                                    )
                                )
                            }
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
