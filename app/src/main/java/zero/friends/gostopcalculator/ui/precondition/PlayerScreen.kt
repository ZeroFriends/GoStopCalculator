package zero.friends.gostopcalculator.ui.precondition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.*

@Composable
fun PlayerScreen(viewModel: PlayerViewModel = hiltViewModel(), onBack: () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val uiState by viewModel.getUiState().collectAsState()

    PlayerScreen(
        scaffoldState,
        uiState,
        onBack,
        onAddPlayer = { viewModel.addPlayer() },
        onDeletePlayer = { viewModel.deletePlayer(it) },
        onLoadPlayer = {},
        onClickNext = {}
    )
}

@Composable
private fun PlayerScreen(
    scaffoldState: ScaffoldState,
    uiState: PlayerUiState,
    onBack: () -> Unit,
    onAddPlayer: () -> Unit,
    onDeletePlayer: (Player) -> Unit,
    onLoadPlayer: () -> Unit,
    onClickNext: () -> Unit,
) {
    val openDialog = remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = String.format(stringResource(id = R.string.game_setting_title), 1),
                onBack = onBack,
                onAction = null
            )
        }
    ) {
        AprilBackground(
            title = stringResource(id = R.string.player_title),
            subTitle = stringResource(id = R.string.player_description),
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .padding(top = 44.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
                    .fillMaxSize(),
            ) {
                val (upper, button) = createRefs()

                Column(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
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
                        inputText = textFieldValue,
                        modifier = Modifier.padding(bottom = 17.dp)
                    ) { textFieldValue.value = it }

                    PlayerLazyColumn(
                        players = uiState.players,
                        onLoadPlayer = {},
                        onAddPlayer = onAddPlayer,
                        onClickEdit = {},
                        onClickDelete = onDeletePlayer
                    )
                }

                GoStopButton(
                    text = stringResource(id = R.string.next),
                    buttonEnabled = uiState.players.size > 1,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .constrainAs(button) {
                            top.linkTo(upper.bottom)
                            bottom.linkTo(parent.bottom)
                        },
                    onClick = onClickNext
                )

            }
        }

//        openDialog.value = true
        if (openDialog.value) {
            NameEditDialog(openDialog)
        }

    }
}

@Composable
private fun PlayerLazyColumn(
    players: List<Player>,
    onLoadPlayer: () -> Unit,
    onAddPlayer: () -> Unit,
    onClickEdit: () -> Unit,
    onClickDelete: (Player) -> Unit,
) {
    LazyColumn {
        item {
            PlayerBlock(modifier = Modifier.padding(bottom = 10.dp)) { onLoadPlayer() }
        }

        if (players.isEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.info_player_add),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 28.dp, end = 28.dp)
                )
            }
        }

        this.items(players) { player ->
            PlayerItem(player, onClickEdit, onClickDelete)
        }
        item {
            GoStopExtraButton(
                text = stringResource(id = R.string.add_new_player),
                modifier = Modifier.padding(top = 5.dp),
                onClick = onAddPlayer
            )
        }
    }
}

@Composable
fun TitleOutlinedTextField(
    title: String,
    hint: String,
    inputText: MutableState<TextFieldValue>,
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
) {
    Column(modifier = modifier) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
        GoStopOutLinedTextField(inputText, onValueChange, hint)
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
fun PlayerItem(player: Player, onClickEdit: () -> Unit, onClickDelete: (Player) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = player.id,
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
            IconButton(onClick = onClickEdit) {
                Icon(painter = painterResource(id = R.drawable.ic_mode_edit_black), contentDescription = null)
            }
        }
        IconButton(onClick = { onClickDelete(player) }) {
            Icon(painter = painterResource(id = R.drawable.ic_delete_black), contentDescription = null)
        }
    }
}

@Preview
@Composable
fun PlayerPreview() {
    PlayerScreen(
        scaffoldState = rememberScaffoldState(),
        uiState = PlayerUiState(),
        {},
        {},
        {},
        {},
        {}
    )
}