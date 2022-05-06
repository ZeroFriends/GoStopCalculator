package zero.friends.gostopcalculator.ui.precondition

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collect
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.GoStopExtraButton
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
    viewModel: PlayerViewModel = hiltViewModel(),
    onNext: (players: List<Player>, gameName: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val uiState by viewModel.getUiState().collectAsState()

    var editingPlayer by remember {
        mutableStateOf<Player?>(null)
    }

    LaunchedEffect(true) {
        viewModel.error().collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler(true) {
        onBack()
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
                viewModel.editGameName(clickEvent.groupName)
                onNext(uiState.players, uiState.gameName.ifEmpty { uiState.currentTime })
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
    val context = LocalContext.current

    Scaffold(
        modifier = TabKeyboardDownModifier(),
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = String.format(stringResource(id = R.string.game_setting_title), 1),
                onBack = { clickEvent.invoke(PlayerClickEvent.Back) },
            )
        }
    ) {
        val gameName = remember {
            mutableStateOf(TextFieldValue(uiState.gameName))
        }

        AprilBackground(
            title = stringResource(id = R.string.player_title),
            subTitle = stringResource(id = R.string.player_description),
            buttonText = stringResource(id = R.string.next),
            buttonEnabled = uiState.players.size > 1,
            onClick = { clickEvent(PlayerClickEvent.Next(gameName.value.text)) }
        ) {
            Column {
                TitleOutlinedTextField(
                    title = stringResource(id = R.string.group_name),
                    text = gameName.value,
                    hint = uiState.currentTime,
                    onValueChange = {
                        if (it.text.length <= 15) gameName.value = it
                        else Toast.makeText(
                            context,
                            context.getString(R.string.over_game_name_alert),
                            Toast.LENGTH_SHORT
                        ).show()
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
        scrollState.scrollToItem(scrollIndex)
    }

    LazyColumn(state = scrollState, contentPadding = PaddingValues(top = 30.dp, bottom = 12.dp)) {
        item {
            Text(text = stringResource(id = R.string.player), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (players.isEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.info_player_add),
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
                text = stringResource(id = R.string.add_new_player),
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
                color = colorResource(id = R.color.orangey_red)
            )
            Text(
                text = player.name,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 6.dp),
                fontSize = 16.sp,
                color = colorResource(id = R.color.nero),
                maxLines = 1
            )
            IconButton(onClick = { clickEvent(PlayerClickEvent.EditPlayer(player)) }) {
                Icon(painter = painterResource(id = R.drawable.ic_mode_edit_black), contentDescription = null)
            }
        }
        IconButton(onClick = { clickEvent(PlayerClickEvent.DeletePlayer(player)) }) {
            Icon(painter = painterResource(id = R.drawable.ic_delete_black), contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerItemPreView() {
    PlayerItem(index = 0, player = Player(name = "hello"), clickEvent = {})
}

@Preview
@Composable
private fun PlayerPreview() {
    PlayerScreen(
        scaffoldState = rememberScaffoldState(),
        uiState = PlayerUiState(),
    ) {}
}