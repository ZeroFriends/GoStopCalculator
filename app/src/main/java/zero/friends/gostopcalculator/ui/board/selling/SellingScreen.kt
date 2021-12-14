package zero.friends.gostopcalculator.ui.board.selling

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.DescriptionBox
import zero.friends.gostopcalculator.ui.common.GoStopButtonBackground
import zero.friends.gostopcalculator.ui.common.RoundedCornerText

private sealed interface SellingEvent {
    object Back : SellingEvent
    object Next : SellingEvent
}

@Composable
fun SellingScreen(
    sellingViewModel: SellingViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    val uiState by sellingViewModel.uiState().collectAsState()
    val scaffoldState = rememberScaffoldState()
    SellingScreen(scaffoldState = scaffoldState, uiState = uiState, event = { event ->
        when (event) {
            SellingEvent.Back -> onBack()
            SellingEvent.Next -> onNext()
        }
    })
}

@Composable
private fun SellingScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: SellingUiState = SellingUiState(),
    event: (SellingEvent) -> Unit = {}
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = uiState.game.name,
                onBack = { event(SellingEvent.Back) },
                isRed = false,
            )
        }
    ) {
        GoStopButtonBackground(
            buttonString = R.string.complete,
            buttonEnabled = false,//todo picked player>3 && 누군가 입력을 받앗을 때
            onClick = { event(SellingEvent.Next) },
            contents = {
                Column {
                    DescriptionBox(mainText = R.string.sell_shine, subText = R.string.sell_shine_description)
                    Spacer(modifier = Modifier.padding(22.dp))
                    GamerList(uiState = uiState, event = event)
                }
            }
        )

    }
}

@Composable
private fun GamerList(uiState: SellingUiState, modifier: Modifier = Modifier, event: (SellingEvent) -> Unit = {}) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.player_list),
                fontSize = 16.sp,
                color = colorResource(id = R.color.nero),
                fontWeight = FontWeight.Bold
            )
            RoundedCornerText(stringResource(id = R.string.can_celling), onClick = {})
        }
        Spacer(modifier = Modifier.padding(9.dp))
        LazyColumn(contentPadding = PaddingValues(2.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(uiState.gamers) { index: Int, gamer: Gamer ->
                GamerPickItem(
                    index = index,
                    player = gamer,
                )
            }
        }
    }
}

@Composable
private fun GamerPickItem(index: Int, player: Gamer) {
//todo 가능하다면 RuleItem을 SharedView로 빼볼까?
}


@Preview
@Composable
fun SellingScreenPreview() {
    SellingScreen(rememberScaffoldState())
}
