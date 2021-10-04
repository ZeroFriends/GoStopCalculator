package zero.friends.gostopcalculator.ui.precondition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.gostopcalculator.R
import zero.friends.domain.model.Player
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
            buttonEnabled = uiState.players.size > 1,
            onClickNextButton = { onClickNext() }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 44.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
            ) {
                val textFieldValue = remember {
                    mutableStateOf(TextFieldValue())
                }
                TitleOutlinedTextField(
                    title = stringResource(id = R.string.group_name),
                    hint = uiState.currentTime,
                    inputText = textFieldValue
                ) {
                    textFieldValue.value = it
                }
                Spacer(modifier = Modifier.padding(17.dp))
                PlayerBlock { onLoadPlayer() }
                Spacer(modifier = Modifier.padding(10.dp))
                PlayerLazyColumn(
                    players = uiState.players,
                    onClickEdit = {},
                    onClickDelete = {}
                )
                Spacer(modifier = Modifier.padding(5.dp))
                OutlinedButton(
                    onClick = onAddPlayer,
                    border = BorderStroke(1.dp, colorResource(id = R.color.nero)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.add_new_player),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.nero)
                    )
                }
            }
        }
//        openDialog.value = true
        if (openDialog.value) {
            NameEditDialog(openDialog)
        }

    }
}

@Composable
private fun PlayerLazyColumn(players: List<Player>, onClickEdit: () -> Unit, onClickDelete: () -> Unit) {
    if (players.isEmpty()) {
        Text(
            text = stringResource(id = R.string.info_player_add),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 28.dp, end = 28.dp)
        )
    } else {
        LazyColumn {
            this.items(players) { player ->
                PlayerItem(player, onClickEdit, onClickDelete)
            }
        }
    }
}

@Composable
fun TitleOutlinedTextField(
    title: String,
    hint: String,
    inputText: MutableState<TextFieldValue>,
    onValueChange: (TextFieldValue) -> Unit,
) {
    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.padding(4.dp))
    GoStopOutLinedTextField(inputText, onValueChange, hint)
}

@Composable
fun PlayerBlock(onLoadButtonClicked: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.player), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            SubActionOutLineButton(stringResource(id = R.string.load)) { onLoadButtonClicked() }
        }
    }
}

@Composable
fun PlayerItem(player: Player, onClickEdit: () -> Unit, onClickDelete: () -> Unit) {
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
        IconButton(onClick = onClickDelete) {
            Icon(painter = painterResource(id = R.drawable.ic_delete_black), contentDescription = null)
        }
    }
}

@Preview
@Composable
fun PlayerPreview() {
    PlayerScreen {

    }
}