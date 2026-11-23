package zero.friends.gostopcalculator.ui.precondition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.add_new_player
import zero.friends.gostopcalculator.shared.generated.resources.delete
import zero.friends.gostopcalculator.shared.generated.resources.game_setting_title
import zero.friends.gostopcalculator.shared.generated.resources.group_name
import zero.friends.gostopcalculator.shared.generated.resources.group_name_optional
import zero.friends.gostopcalculator.shared.generated.resources.ic_calendar_today
import zero.friends.gostopcalculator.shared.generated.resources.ic_delete_black
import zero.friends.gostopcalculator.shared.generated.resources.ic_mode_edit_black
import zero.friends.gostopcalculator.shared.generated.resources.ic_topbar_close
import zero.friends.gostopcalculator.shared.generated.resources.info_player_add
import zero.friends.gostopcalculator.shared.generated.resources.next
import zero.friends.gostopcalculator.shared.generated.resources.player
import zero.friends.gostopcalculator.shared.generated.resources.player_count_format
import zero.friends.gostopcalculator.shared.generated.resources.player_description
import zero.friends.gostopcalculator.shared.generated.resources.player_max_count_hint
import zero.friends.gostopcalculator.shared.generated.resources.player_title
import zero.friends.gostopcalculator.ui.board.score.common.PlayerIndexBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreBorderNeutral
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreNeutralBtnBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreRuleCardBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreRuleCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkButtonBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.background.AprilBackground
import zero.friends.gostopcalculator.ui.dialog.NameEditDialog
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier

private val DashedAddBorderColor = Color(0xFFFFB8B8)

private sealed interface PlayerClickEvent {

    object Back : PlayerClickEvent

    object AddPlayer : PlayerClickEvent
    class DeletePlayer(val player: Player) : PlayerClickEvent
    class EditPlayer(val player: Player) : PlayerClickEvent
    class Next(val groupName: String) : PlayerClickEvent
}

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onNext: (players: List<Player>, gameName: String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.error().collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    val uiState by viewModel.getUiState().collectAsState()

    var editingPlayer by remember {
        mutableStateOf<Player?>(null)
    }

    PlayerScreen(
        scaffoldState,
        uiState,
    ) { clickEvent ->
        when (clickEvent) {
            PlayerClickEvent.AddPlayer -> viewModel.addPlayer()
            PlayerClickEvent.Back -> onBack()
            is PlayerClickEvent.DeletePlayer -> viewModel.removePlayer(clickEvent.player)
            is PlayerClickEvent.Next -> {
                onNext(uiState.players, clickEvent.groupName.ifEmpty { uiState.currentTime })
            }
            is PlayerClickEvent.EditPlayer -> {
                editingPlayer = clickEvent.player
            }
        }
    }

    if (editingPlayer != null) {
        NameEditDialog(
            viewModel = viewModel,
            player = requireNotNull(editingPlayer),
            onClose = { editingPlayer = null }
        )
    }
}

@Composable
private fun PlayerScreen(
    scaffoldState: ScaffoldState,
    uiState: PlayerUiState,
    clickEvent: (PlayerClickEvent) -> Unit,
) {
    Scaffold(
        modifier = TabKeyboardDownModifier(),
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = stringResource(Res.string.game_setting_title, 1),
                onBack = { clickEvent.invoke(PlayerClickEvent.Back) },
            )
        }
    ) { innerPadding ->
        var gameName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

        AprilBackground(
            modifier = Modifier.padding(innerPadding),
            title = stringResource(Res.string.player_title),
            subTitle = stringResource(Res.string.player_description),
            buttonText = stringResource(Res.string.next),
            buttonEnabled = uiState.players.size > 1,
            onClick = { clickEvent(PlayerClickEvent.Next(gameName.text)) }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                GroupNameSection(
                    text = gameName,
                    hint = uiState.currentTime,
                    onValueChange = {
                        if (it.text.length <= 15) gameName = it
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                PlayerLazyColumn(
                    modifier = Modifier.weight(1f),
                    scrollIndex = uiState.scrollIndex,
                    players = uiState.players,
                    clickEvent = clickEvent
                )
            }
        }
    }
}

@Composable
private fun GroupNameSection(
    text: TextFieldValue,
    hint: String,
    onValueChange: (TextFieldValue) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.group_name),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ScoreTextDark
            )
            StatusPillBadge(
                text = stringResource(Res.string.group_name_optional),
                backgroundColor = ScoreNeutralBtnBg,
                textColor = ScoreMutedText,
                horizontalPadding = 8.dp,
                verticalPadding = 3.dp,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = ScoreRuleCardBg,
            border = BorderStroke(1.dp, ScoreRuleCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_calendar_today),
                    contentDescription = null,
                    tint = ScoreSubtleGray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (text.text.isEmpty()) {
                        Text(
                            text = hint,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = ScoreMutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    BasicTextField(
                        value = text,
                        onValueChange = onValueChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = ScoreTextDark
                        ),
                        cursorBrush = SolidColor(ScoreTextDark),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (text.text.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(ScoreNeutralBtnBg)
                            .clickable(role = Role.Button) {
                                onValueChange(TextFieldValue(""))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_topbar_close),
                            contentDescription = stringResource(Res.string.delete),
                            tint = ScoreMutedText,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerLazyColumn(
    modifier: Modifier = Modifier,
    scrollIndex: Int,
    scrollState: LazyListState = rememberLazyListState(),
    players: List<Player>,
    clickEvent: (PlayerClickEvent) -> Unit,
) {
    LaunchedEffect(scrollIndex, players.size) {
        if (scrollIndex in players.indices) {
            val targetItemIndex = if (scrollIndex == players.lastIndex) {
                players.size + 1
            } else {
                scrollIndex + 1
            }
            scrollState.scrollToItem(targetItemIndex)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = scrollState,
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "players_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.player),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScoreTextDark
                    )
                    StatusPillBadge(
                        text = stringResource(Res.string.player_count_format, players.size),
                        backgroundColor = ScoreSoftPinkBg,
                        textColor = ScorePrimaryRed,
                        horizontalPadding = 8.dp,
                        verticalPadding = 3.dp,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = stringResource(Res.string.player_max_count_hint),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ScoreSubtleGray
                )
            }
        }

        if (players.isEmpty()) {
            item(key = "players_empty") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = ScoreRuleCardBg,
                    border = BorderStroke(1.dp, ScoreRuleCardBorder)
                ) {
                    Text(
                        text = stringResource(Res.string.info_player_add),
                        fontSize = 13.5.sp,
                        color = ScoreMutedText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 22.dp)
                    )
                }
            }
        }

        itemsIndexed(
            items = players,
            key = { _, player -> player.name }
        ) { index, player ->
            PlayerItem(index, player, clickEvent)
        }

        item(key = "add_player_button") {
            DashedAddPlayerButton(
                text = stringResource(Res.string.add_new_player),
                onClick = { clickEvent(PlayerClickEvent.AddPlayer) }
            )
        }
    }
}

@Composable
private fun PlayerItem(index: Int, player: Player, clickEvent: (PlayerClickEvent) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ScoreRuleCardBg,
        border = BorderStroke(1.dp, ScoreRuleCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                PlayerIndexBadge(
                    index = index,
                    containerColor = ScoreSoftPinkBg,
                    textColor = ScorePrimaryRed,
                    size = 26.dp,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = player.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScoreTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerActionIconButton(
                    icon = Res.drawable.ic_mode_edit_black,
                    contentDescription = "Edit Player",
                    tint = ScoreMutedText,
                    onClick = { clickEvent(PlayerClickEvent.EditPlayer(player)) }
                )
                PlayerActionIconButton(
                    icon = Res.drawable.ic_delete_black,
                    contentDescription = "Delete Player",
                    tint = ScoreSubtleGray,
                    onClick = { clickEvent(PlayerClickEvent.DeletePlayer(player)) }
                )
            }
        }
    }
}

@Composable
private fun PlayerActionIconButton(
    icon: DrawableResource,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ScoreBorderNeutral)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(icon),
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun DashedAddPlayerButton(
    text: String,
    onClick: () -> Unit,
) {
    val cornerRadius = 14.dp
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clip(shape)
            .background(ScoreSoftPinkButtonBg)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokePx = 1.5.dp.toPx()
            val dashPx = 6.dp.toPx()
            val gapPx = 4.dp.toPx()
            val radiusPx = cornerRadius.toPx()
            drawRoundRect(
                color = DashedAddBorderColor,
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(
                    width = strokePx,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f)
                )
            )
        }
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ScorePrimaryRed,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp)
        )
    }
}
