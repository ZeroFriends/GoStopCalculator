package zero.friends.gostopcalculator.ui.board.prepare

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.Game
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.DescriptionBox
import zero.friends.gostopcalculator.ui.common.GoStopButtonBackground

private sealed interface PrepareEvent {
    object Back : PrepareEvent
    object Complete : PrepareEvent
    class OnClickPlayer(val player: Player, val isCheck: Boolean) : PrepareEvent
}

@Composable
fun PrepareScreen(
    prepareViewModel: PrepareViewModel = hiltViewModel(),
    onComplete: (skipSelling: Boolean) -> Unit = { },
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val uiState by prepareViewModel.uiState().collectAsState()

    BackHandler(true) {
        prepareViewModel.deleteRound()
        onBack()
    }

    PrepareScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        event = { event ->
            when (event) {
                PrepareEvent.Back -> {
                    prepareViewModel.deleteRound()
                    onBack()
                }
                PrepareEvent.Complete -> {
                    onComplete(uiState.gamer.count() != 4)
                }
                is PrepareEvent.OnClickPlayer -> {
                    prepareViewModel.onClickPlayer(event.isCheck, event.player) {
                        Toast.makeText(context, context.getString(R.string.fail_over_gamer), Toast.LENGTH_SHORT).show()
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
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = uiState.game.name,
                onBack = { event(PrepareEvent.Back) },
                isRed = false,
            )
        }
    ) {
        GoStopButtonBackground(
            buttonString = R.string.complete,
            buttonEnabled = uiState.gamer.size >= 2,
            onClick = { event(PrepareEvent.Complete) },
            contents = {
                Column {
                    DescriptionBox(mainText = R.string.start, subText = R.string.start_description)
                    Spacer(modifier = Modifier.padding(22.dp))
                    PlayerPickList(uiState = uiState, event = event)
                }
            }
        )

    }
}

@Composable
private fun PlayerPickList(
    modifier: Modifier = Modifier,
    uiState: PrepareUiState,
    event: (PrepareEvent) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.player_list),
            fontSize = 16.sp,
            color = colorResource(id = R.color.nero),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(9.dp))
        LazyColumn(contentPadding = PaddingValues(2.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(items = uiState.players, key = { _, player -> player.id }) { index: Int, player: Player ->
                PlayerPickItem(
                    index = index,
                    player = player,
                    isCheck = uiState.gamer.any { player.id == it.playerId },
                    onClick = { event(PrepareEvent.OnClickPlayer(player, it)) })
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlayerPickItem(index: Int, player: Player, isCheck: Boolean = true, onClick: (Boolean) -> Unit = {}) {
    val modifier = if (isCheck) Modifier.background(
        color = colorResource(id = R.color.orangey_red).copy(alpha = 0.1f),
        shape = RoundedCornerShape(18.dp)
    )
    else Modifier

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(!isCheck) }
            .padding(18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Text(
                text = (index + 1).toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = player.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Surface(onClick = { onClick(!isCheck) }) {
            if (isCheck) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_check_circle_24_dp),
                    modifier = Modifier.background(color = colorResource(id = R.color.orangey_red).copy(alpha = 0.1f)),
                    contentDescription = null
                )
            } else {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_unchecked_circle_24_dp),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PickItemPreview() {
    PlayerPickItem(index = 1, player = Player(1, "zero"))
}

@Preview
@Composable
private fun PrepareScreenPreview() {
    PrepareScreen(
        rememberScaffoldState(),
        PrepareUiState(
            game = Game(1, "gameName"),
            players = listOf(
                Player(1, "zero"),
                Player(2, "hello"),
                Player(3, "조재영"),
            )
        )
    )
}