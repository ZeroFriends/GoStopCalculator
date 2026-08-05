package zero.friends.gostopcalculator.ui.precondition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.add_new_player
import zero.friends.gostopcalculator.shared.generated.resources.group_name
import zero.friends.gostopcalculator.shared.generated.resources.ic_delete_black
import zero.friends.gostopcalculator.shared.generated.resources.ic_mode_edit_black
import zero.friends.gostopcalculator.shared.generated.resources.info_player_add
import zero.friends.gostopcalculator.shared.generated.resources.next
import zero.friends.gostopcalculator.shared.generated.resources.player
import zero.friends.gostopcalculator.shared.generated.resources.player_description
import zero.friends.gostopcalculator.shared.generated.resources.player_title
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.theme.OrangeyRed
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.GoStopExtraButton
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.TitleOutlinedTextField
import zero.friends.gostopcalculator.ui.common.background.AprilBackground
import zero.friends.gostopcalculator.ui.dialog.NameEditDialog
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier

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
                text = "게임 설정 (1/2)",
                onBack = { clickEvent.invoke(PlayerClickEvent.Back) },
            )
        }
    ) { innerPadding ->
        var gameName by remember {
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
            Column {
                TitleOutlinedTextField(
                    title = stringResource(Res.string.group_name),
                    text = gameName,
                    hint = uiState.currentTime,
                    onValueChange = {
                        if (it.text.length <= 15) gameName = it
                    }
                )

                PlayerLazyColumn(
                    scrollIndex = uiState.scrollIndex,
                    players = uiState.players,
                    clickEvent = clickEvent
                )
            }
        }
    }
}

@Composable
private fun PlayerLazyColumn(
    scrollIndex: Int,
    scrollState: LazyListState = rememberLazyListState(),
    players: List<Player>,
    clickEvent: (PlayerClickEvent) -> Unit,
) {
    LaunchedEffect(scrollIndex) {
        if (scrollIndex in players.indices) {
            scrollState.scrollToItem(scrollIndex)
        }
    }

    LazyColumn(state = scrollState, contentPadding = PaddingValues(top = 30.dp, bottom = 12.dp)) {
        item {
            Text(text = stringResource(Res.string.player), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (players.isEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.info_player_add),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        itemsIndexed(items = players) { index, player ->
            PlayerItem(index, player, clickEvent)
        }

        item {
            GoStopExtraButton(
                text = stringResource(Res.string.add_new_player),
                modifier = Modifier.padding(top = 10.dp),
                onClick = { clickEvent(PlayerClickEvent.AddPlayer) }
            )
        }
    }
}

@Composable
private fun PlayerItem(index: Int, player: Player, clickEvent: (PlayerClickEvent) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
                fontSize = 16.sp,
                color = OrangeyRed
            )
            Text(
                text = player.name,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 6.dp),
                fontSize = 16.sp,
                color = Nero,
                maxLines = 1
            )
            IconButton(onClick = { clickEvent(PlayerClickEvent.EditPlayer(player)) }) {
                Icon(
                    painter = painterResource(Res.drawable.ic_mode_edit_black),
                    contentDescription = "Edit Player",
                    tint = Nero
                )
            }
        }
        IconButton(onClick = { clickEvent(PlayerClickEvent.DeletePlayer(player)) }) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete_black),
                contentDescription = "Delete Player",
                tint = Nero
            )
        }
    }
}
