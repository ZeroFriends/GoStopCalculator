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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.*
import zero.friends.gostopcalculator.ui.dialog.NameEditDialog

sealed class ClickEvent {
    object Back : ClickEvent()
    object AddPlayer : ClickEvent()
    class DeletePlayer(val player: Player) : ClickEvent()
    object LoadPlayer : ClickEvent()
    class EditPlayer(val player: Player) : ClickEvent()
    object Next : ClickEvent()
}

@Composable
fun PlayerScreen(viewModel: PlayerViewModel = hiltViewModel(), onBack: () -> Unit) {
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
            ClickEvent.AddPlayer -> viewModel.addPlayer()
            ClickEvent.Back -> {
                viewModel.clearGame()
                onBack()
            }
            is ClickEvent.DeletePlayer -> viewModel.deletePlayer(clickEvent.player)
            ClickEvent.LoadPlayer -> TODO()
            ClickEvent.Next -> TODO()
            is ClickEvent.EditPlayer -> viewModel.openDialog(player = clickEvent.player)

        }
    }

    if (uiState.dialogState.openDialog) {
        NameEditDialog {
            viewModel.closeDialog()
        }
    }
}

@Composable
private fun PlayerScreen(
    scaffoldState: ScaffoldState,
    uiState: PlayerUiState,
    clickEvent: (ClickEvent) -> Unit,
) {

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = String.format(stringResource(id = R.string.game_setting_title), 1),
                onBack = { clickEvent.invoke(ClickEvent.Back) },
                onAction = null
            )
        }
    ) {
        AprilBackground(
            title = stringResource(id = R.string.player_title),
            subTitle = stringResource(id = R.string.player_description)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 44.dp, start = 16.dp, end = 16.dp, bottom = 40.dp),
            ) {
                val (upper, button) = createRefs()

                Column(
                    modifier = Modifier
                        .constrainAs(upper) {
                            bottom.linkTo(button.top)
                            top.linkTo(parent.top)
                        }
                        .fillMaxHeight()
                ) {
                    val textFieldValue = remember {
                        mutableStateOf(TextFieldValue())
                    }
                    TitleOutlinedTextField(
                        title = stringResource(id = R.string.group_name),
                        hint = uiState.currentTime,
                        modifier = Modifier.padding(bottom = 17.dp)
                    ) { textFieldValue.value = it }

                    PlayerLazyColumn(
                        players = uiState.players,
                        clickEvent = clickEvent
                    )
                }

                GoStopButton(
                    text = stringResource(id = R.string.next),
                    buttonEnabled = uiState.players.size > 1,
                    modifier = Modifier
                        .constrainAs(button) {
                            top.linkTo(upper.bottom)
                            bottom.linkTo(parent.bottom)
                        },
                    onClick = { clickEvent(ClickEvent.Next) }
                )

            }
        }

    }
}

@Composable
private fun PlayerLazyColumn(
    players: List<Player>,
    clickEvent: (ClickEvent) -> Unit,
) {
    LazyColumn {
        item {
            PlayerBlock(modifier = Modifier.padding(bottom = 20.dp)) { clickEvent(ClickEvent.LoadPlayer) }
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
                onClick = { clickEvent(ClickEvent.AddPlayer) }
            )
        }
    }
}

@Composable
fun TitleOutlinedTextField(
    title: String,
    hint: String,
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
) {
    Column(modifier = modifier) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
        GoStopOutLinedTextField("", hint, onValueChane = onValueChange)
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
fun PlayerItem(index: Int, player: Player, clickEvent: (ClickEvent) -> Unit) {
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
                color = colorResource(id = R.color.nero)
            )
            IconButton(onClick = { clickEvent(ClickEvent.EditPlayer(player)) }) {
                Icon(painter = painterResource(id = R.drawable.ic_mode_edit_black), contentDescription = null)
            }
        }
        IconButton(onClick = { clickEvent(ClickEvent.DeletePlayer(player)) }) {
            Icon(painter = painterResource(id = R.drawable.ic_delete_black), contentDescription = null)
        }
    }
}

@Preview
@Composable
fun PlayerPreview() {
    PlayerScreen(
        scaffoldState = rememberScaffoldState(),
        uiState = PlayerUiState()
    ) {}
}