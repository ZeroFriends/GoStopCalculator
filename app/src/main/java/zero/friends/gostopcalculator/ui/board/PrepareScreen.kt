package zero.friends.gostopcalculator.ui.board

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.ContentsCard
import zero.friends.gostopcalculator.ui.common.GoStopButtonBackground

@Composable
fun PrepareScreen(prepareViewModel: PrepareViewModel = hiltViewModel()) {
    val scaffoldState = rememberScaffoldState()
    val uiState by prepareViewModel.uiState().collectAsState()
    PrepareScreen(
        scaffoldState = scaffoldState,
        uiState = uiState
    )
}

@Composable
private fun PrepareScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: PrepareUiState = PrepareUiState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = "",
                onBack = {},
                isRed = false,
            )
        }
    ) {
        GoStopButtonBackground(
            buttonString = R.string.complete,
            onClick = {},
            contents = {
                Column {
                    StartDescription()
                    Spacer(modifier = Modifier.padding(22.dp))
                    PlayerPickList()
                }
            }
        )

    }
}

@Composable
private fun StartDescription(modifier: Modifier = Modifier) {
    ContentsCard(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 36.dp, horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.start),
                fontSize = 20.sp,
                color = colorResource(id = R.color.nero),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = stringResource(id = R.string.start_description),
                fontSize = 14.sp,
                color = colorResource(id = R.color.nero),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PlayerPickList(modifier: Modifier = Modifier, players: List<Player> = emptyList()) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.player_list))
        Spacer(modifier = Modifier.padding(9.dp))
        LazyColumn {
            itemsIndexed(players) { index: Int, item: Player ->
                PlayerPickItem(index, item)
            }
        }
    }
}

@Composable
private fun PlayerPickItem(index: Int, item: Player) {
    TODO("Not yet implemented")
}


@Preview
@Composable
private fun PrepareScreenPreview() {
    PrepareScreen(rememberScaffoldState())
}