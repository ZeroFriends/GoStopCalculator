package zero.friends.gostopcalculator.ui.board.selling

import androidx.activity.compose.BackHandler
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
import androidx.lifecycle.viewmodel.compose.viewModel
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.di.entrypoint.EntryPoint
import zero.friends.gostopcalculator.ui.common.*
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier
import zero.friends.gostopcalculator.util.getEntryPointFromActivity

private sealed interface SellingEvent {
    object Back : SellingEvent
    object Next : SellingEvent
    class OnChangeSeller(val gamer: Gamer, val count: Int) : SellingEvent
}

@Composable
fun createSellingViewModel(roundId: Long): SellingViewModel {
    val entryPoint = getEntryPointFromActivity<EntryPoint>()
    val factory = entryPoint.sellingFactory()
    return viewModel(factory = SellingViewModel.provideFactory(sellingViewModelFactory = factory, roundId = roundId))
}

@Composable
fun SellingScreen(
    sellingViewModel: SellingViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onNext: () -> Unit = {}
) {

    BackHandler(true) {
        onBack()
    }

    val uiState by sellingViewModel.uiState().collectAsState()
    val scaffoldState = rememberScaffoldState()
    SellingScreen(scaffoldState = scaffoldState, uiState = uiState, event = { event ->
        when (event) {
            SellingEvent.Back -> onBack()
            SellingEvent.Next -> {
                sellingViewModel.complete()
                onNext()
            }
            is SellingEvent.OnChangeSeller -> {
                sellingViewModel.onSaveSeller(event.gamer, event.count)
            }
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
        modifier = TabKeyboardDownModifier(),
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
            buttonEnabled = uiState.seller != null,
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
            itemsIndexed(
                key = { _, item -> item.id },
                items = uiState.gamers
            ) { index: Int, gamer: Gamer ->
                GamerPickItem(
                    index = index,
                    gamer = gamer,
                    onUpdateScore = { event(SellingEvent.OnChangeSeller(gamer = gamer, count = it)) }
                )
            }
        }
    }
}

@Composable
private fun GamerPickItem(
    index: Int = 0,
    gamer: Gamer = Gamer(),
    onUpdateScore: (Int) -> Unit = {}
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = gamer.name,
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.nero)
                    )
                }
            }
        }

        NumberTextField(
            modifier = Modifier.weight(1f),
            endText = stringResource(R.string.page),
            unFocusDeleteMode = true
        ) {
            onUpdateScore(it)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GamerPickItemPreview() {
    GamerPickItem(0, Gamer(name = "zero.dev"))
}

@Preview
@Composable
fun SellingScreenPreview() {
    SellingScreen(rememberScaffoldState())
}
