package zero.friends.gostopcalculator.ui.board.score

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import zero.friends.domain.model.*
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.*


sealed interface ScoreEvent {
    object Back : ScoreEvent
    object OnNext : ScoreEvent
    class SelectScore(val gamer: Gamer, val option: ScoreOption) : ScoreEvent
    class SelectLoser(val gamer: Gamer, val option: LoserOption) : ScoreEvent
    object Complete : ScoreEvent
    class OnUpdateWinnerPoint(val gamer: Gamer, val point: Int) : ScoreEvent
    class OnUpdateSellerPoint(val gamer: Gamer, val count: Int) : ScoreEvent
}

@Composable
fun ScoreScreen(
    scoreViewModel: ScoreViewModel = hiltViewModel(),
    onBack: (gameId: Long?) -> Unit,
    onComplete: () -> Unit = {}
) {
    val scaffoldState = rememberScaffoldState()
    val uiState by scoreViewModel.uiState().collectAsState()

    LaunchedEffect(key1 = Unit) {
        scoreViewModel.escapeEvent().collectLatest {
            onBack(uiState.game.id)
        }
    }

    BackHandler(true) {
        scoreViewModel.onBack()
    }

    ScoreScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        scoreEvent = { event ->
            when (event) {
                ScoreEvent.Back -> scoreViewModel.onBack()
                is ScoreEvent.SelectScore -> scoreViewModel.selectScore(event.gamer, event.option)
                ScoreEvent.OnNext -> scoreViewModel.onNext()
                is ScoreEvent.OnUpdateWinnerPoint -> scoreViewModel.updateWinner(event.gamer, event.point)
                is ScoreEvent.SelectLoser -> scoreViewModel.selectLoser(event.gamer, event.option)
                ScoreEvent.Complete -> {
                    scoreViewModel.calculateGameResult()
                    onComplete()
                }
                is ScoreEvent.OnUpdateSellerPoint -> {
                    scoreViewModel.updateSeller(event.gamer, event.count)
                }
            }
        }
    )
}

@Composable
private fun ScoreScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: ScoreUiState = ScoreUiState(),
    scoreEvent: (ScoreEvent) -> Unit = {}
) {

    Scaffold(
        modifier = Modifier,
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = uiState.game.name,
                onBack = { scoreEvent(ScoreEvent.Back) },
                isRed = false
            )
        }
    ) {
        GoStopButtonBackground(
            buttonString = uiState.phase.getButtonText(),
            buttonEnabled = uiState.phase.getEnableNext(),
            onClick = {
                if (uiState.phase is Loser) {
                    scoreEvent(ScoreEvent.Complete)
                } else {
                    scoreEvent(ScoreEvent.OnNext)
                }
            }
        ) {
            Column {
                DescriptionBox(mainText = uiState.phase.getMainText(), subText = uiState.phase.getSubText())
                Spacer(modifier = Modifier.padding(22.dp))
                GamerList(uiState, event = scoreEvent)
            }
        }
    }
}

@Composable
fun GamerList(uiState: ScoreUiState, event: (ScoreEvent) -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth()
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
            RoundedCornerText(stringResource(id = R.string.score_guide), onClick = {})
        }
        Spacer(modifier = Modifier.padding(9.dp))

        LazyColumn(contentPadding = PaddingValues(2.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            itemsIndexed(uiState.playerResults) { index, gamer ->
                when (uiState.phase) {
                    is Selling -> WinnerItem(index, gamer, isSeller = true, isEnable = true, event)
                    is Scoring -> {
                        if (gamer.id == uiState.seller?.id) WinnerItem(
                            index,
                            uiState.seller,
                            isSeller = true,
                            isEnable = false
                        )
                        else ScoringItem(index, gamer, uiState.phase, event)
                    }
                    is Winner -> {
                        if (gamer.id == uiState.seller?.id) WinnerItem(
                            index, uiState.seller,
                            isSeller = true,
                            isEnable = false
                        )
                        else WinnerItem(index, gamer, isSeller = false, isEnable = true, event = event)
                    }
                    is Loser -> {
                        when (gamer.id) {
                            uiState.seller?.id -> WinnerItem(index, uiState.seller, isSeller = true, isEnable = false)
                            uiState.winner?.id -> WinnerItem(index, uiState.winner, isSeller = false, isEnable = false)
                            else -> ScoringItem(index, gamer, uiState.phase, event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WinnerItem(
    index: Int,
    gamer: Gamer,
    isSeller: Boolean,
    isEnable: Boolean,
    event: (ScoreEvent) -> Unit = {}
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
                    .padding(10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = if (isEnable) R.color.orangey_red else R.color.gray)
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
                        color = colorResource(id = if (isEnable) R.color.nero else R.color.gray)
                    )
                    if (!isEnable) {
                        Spacer(modifier = Modifier.padding(3.dp))
                        val optionName = if (isSeller) gamer.sellerOption else gamer.winnerOption
                        OptionBox(requireNotNull(optionName?.korean), colorResource(id = R.color.gray))
                    }
                }
            }

        }

        NumberTextField(
            text = if (isEnable) "" else gamer.account.toString(),
            modifier = Modifier.weight(1f),
            endText = stringResource(if (isSeller) R.string.page else R.string.point),
            isEnable = isEnable,
            unFocusDeleteMode = true,
            hintColor = colorResource(id = if (isEnable) R.color.nero else R.color.gray)
        ) {
            event(
                if (isSeller) ScoreEvent.OnUpdateSellerPoint(gamer, it)
                else ScoreEvent.OnUpdateWinnerPoint(gamer, it)
            )

        }

    }

}

@Composable
fun ScoringItem(index: Int, gamer: Gamer, toggleable: Phase.Toggleable, event: (ScoreEvent) -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Text(
                text = gamer.name,
                fontSize = 16.sp,
                color = colorResource(id = R.color.nero)
            )
        }
        ToggleRow(modifier = Modifier.padding(start = 10.dp), toggleable = toggleable, gamer = gamer, event = event)
    }
}

@Preview(showBackground = true)
@Composable
private fun WinnerItemPreview() {
    WinnerItem(index = 0, gamer = Gamer(name = "zero"), isSeller = true, isEnable = true)
}

@Preview(showBackground = true)
@Composable
private fun ScoringItemPreview() {
    ScoringItem(index = 0, gamer = Gamer(name = "zero"), toggleable = Scoring)
}


@Composable
private fun ToggleRow(
    modifier: Modifier = Modifier,
    toggleable: Phase.Toggleable,
    gamer: Gamer,
    event: (ScoreEvent) -> Unit
) {
    val options = when (toggleable) {
        Scoring -> ScoreOption.values()
        Loser -> LoserOption.values()
    }

    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options, key = { it.ordinal }) { option ->
            OptionBox(
                text = option.korean,
                color = colorResource(id = R.color.non_click),
                isSelected = when (toggleable) {
                    Loser -> gamer.loserOption
                    Scoring -> gamer.scoreOption
                }.contains(option),
                onClick = {
                    event(
                        when (toggleable) {
                            Loser -> ScoreEvent.SelectLoser(gamer, option as LoserOption)
                            Scoring -> ScoreEvent.SelectScore(gamer, option as ScoreOption)
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun OptionBox(text: String, color: Color, isSelected: Boolean = false, onClick: ((Boolean) -> Unit)? = null) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(width = 1.dp, color = if (isSelected) colorResource(id = R.color.orangey_red) else color),
        color = if (isSelected) colorResource(id = R.color.orangey_red) else MaterialTheme.colors.surface,
        modifier = Modifier.clickable(onClick = { onClick?.invoke(isSelected) }, enabled = onClick != null),
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
            text = text,
            color = if (isSelected) colorResource(id = R.color.white) else color
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ScoreScreen(
        uiState = ScoreUiState(
            playerResults = listOf(
                Gamer(id = 0, name = "zero.dev", sellerOption = SellerOption.Sell),
                Gamer(
                    id = 1,
                    name = "dev",
                    scoreOption = listOf(ScoreOption.ThreeFuck, ScoreOption.FiveShine, ScoreOption.FirstDdadak)
                ),
                Gamer(id = 2, name = "zero", loserOption = listOf(LoserOption.GoBack, LoserOption.MongBak)),
                Gamer(id = 3, name = "winner", winnerOption = WinnerOption.Winner)
            )
        )
    )
}
