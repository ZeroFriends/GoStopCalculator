package zero.friends.gostopcalculator.ui.precondition

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.*
import zero.friends.gostopcalculator.ui.dialog.NameEditDialog

sealed class PlayerClickEvent {
    object Back : PlayerClickEvent()
    object AddPlayer : PlayerClickEvent()
    class DeletePlayer(val player: Player) : PlayerClickEvent()
    object LoadPlayer : PlayerClickEvent()
    class EditPlayer(val player: Player) : PlayerClickEvent()
    class Next(val groupName: String) : PlayerClickEvent()
}

@Composable
fun PlayerScreen(viewModel: PlayerViewModel = hiltViewModel(), onNext: () -> Unit, onBack: () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val uiState by viewModel.getUiState().collectAsState()

    BackHandler(true) {
        viewModel.clearGame()
        onBack()
    }

    PlayerScreen(
        scaffoldState,
        uiState
    ) { clickEvent ->
        when (clickEvent) {
            PlayerClickEvent.AddPlayer -> viewModel.addPlayer()
            PlayerClickEvent.Back -> {
                viewModel.clearGame()
                onBack()
            }
            is PlayerClickEvent.DeletePlayer -> viewModel.deletePlayer(clickEvent.player)
            PlayerClickEvent.LoadPlayer -> TODO()
            is PlayerClickEvent.Next -> {
                viewModel.editGameName(clickEvent.groupName)
                onNext()
            }
            is PlayerClickEvent.EditPlayer -> viewModel.openDialog(player = clickEvent.player)

        }
    }

    if (uiState.dialogState.openDialog) {
        NameEditDialog()
    }
}

@Composable
private fun PlayerScreen(
    scaffoldState: ScaffoldState,
    uiState: PlayerUiState,
    clickEvent: (PlayerClickEvent) -> Unit,
) {

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = String.format(stringResource(id = R.string.game_setting_title), 1),
                onBack = { clickEvent.invoke(PlayerClickEvent.Back) },
                onAction = null
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
                    hint = uiState.currentTime
                ) { gameName.value = it }

                PlayerLazyColumn(
                    players = uiState.players,
                    clickEvent = clickEvent
                )
            }
        }

    }
}

@Composable
private fun PlayerLazyColumn(
    players: List<Player>,
    clickEvent: (PlayerClickEvent) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(top = 35.dp, bottom = 12.dp)) {
        item {
            PlayerBlock(modifier = Modifier.padding(bottom = 20.dp)) { clickEvent(PlayerClickEvent.LoadPlayer) }
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
        itemsIndexed(players) { index, player ->
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
fun TitleOutlinedTextField(
    title: String,
    text: TextFieldValue,
    hint: String,
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
) {
    Column(modifier = modifier) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
        GoStopOutLinedTextField(text, hint, onValueChange = onValueChange)
    }
}

@Composable
fun PlayerBlock(modifier: Modifier = Modifier, onLoadButtonClicked: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = R.string.player), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        SubActionOutLineButton(stringResource(id = R.string.load)) { onLoadButtonClicked() }
    }
}

@Composable
fun PlayerItem(index: Int, player: Player, clickEvent: (PlayerClickEvent) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
                fontSize = 16.sp,
                color = colorResource(id = R.color.orangey_red))
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
fun PlayerItemPreView() {
    PlayerItem(index = 0, player = Player("hello"), clickEvent = {})
}

@Preview
@Composable
fun PlayerPreview() {
    PlayerScreen(
        scaffoldState = rememberScaffoldState(),
        uiState = PlayerUiState()
    ) {}
}