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
import zero.friends.domain.model.*
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.*


sealed interface ScoreEvent {
    object Back : ScoreEvent
    class SelectScore(val gamer: Gamer, val option: ScoreOption) : ScoreEvent
    class SelectLoser(val gamer: Gamer, val option: LoserOption) : ScoreEvent
    object OnNext : ScoreEvent
    object Complete : ScoreEvent
    class OnUpdateWinnerPoint(val gamer: Gamer, val point: Int) : ScoreEvent
}

@Composable
fun ScoreScreen(
    scoreViewModel: ScoreViewModel = hiltViewModel(),
    onBack: (gameId: Long) -> Unit,
    onComplete: () -> Unit = {}
) {
    val scaffoldState = rememberScaffoldState()
    val uiState by scoreViewModel.uiState().collectAsState()
    BackHandler(true) {
        onBack(uiState.game.id)
    }

    ScoreScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        scoreEvent = { event ->
            when (event) {
                ScoreEvent.Back -> onBack(uiState.game.id)
                is ScoreEvent.SelectScore -> scoreViewModel.selectScore(event.gamer, event.option)
                ScoreEvent.OnNext -> scoreViewModel.onNext()
                is ScoreEvent.OnUpdateWinnerPoint -> scoreViewModel.updateWinner(event.gamer, event.point)
                is ScoreEvent.SelectLoser -> scoreViewModel.selectLoser(event.gamer, event.option)
                ScoreEvent.Complete -> onComplete()
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
            itemsIndexed(uiState.playerResults) { index, item ->
                ScoringGamerItem(index = index, gamer = item, uiState = uiState, event = event)
            }

        }
    }
}

@Composable
fun ScoringGamerItem(index: Int, gamer: Gamer, uiState: ScoreUiState, event: (ScoreEvent) -> Unit = {}) {
    val color = colorResource(id = if (gamer.isWinner()) R.color.gray else R.color.nero)

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
                color = colorResource(id = if (gamer.isWinner()) R.color.gray else R.color.orangey_red)
            )
            Column {
                Row {
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
                                color = color
                            )
                            if (gamer.isWinner()) {
                                val option = when {
                                    gamer.winnerOption != null -> requireNotNull(gamer.winnerOption)
                                    gamer.sellerOption != null -> requireNotNull(gamer.sellerOption)
                                    else -> throw IllegalStateException("Not Supported Option")
                                }.korean
                                Spacer(modifier = Modifier.padding(3.dp))
                                OptionBox(option, colorResource(id = R.color.gray))
                            }
                        }
                    }
                }
                if (!gamer.isWinner()) {
                    when (uiState.phase) {
                        is Scoring -> ToggleRow(phase = uiState.phase, gamer = gamer, event = event)
                        is Loser -> ToggleRow(phase = uiState.phase, gamer = gamer, event = event)
                    }
                }
            }
        }
        if (gamer.isWinner()) {
            NumberTextField(
                text = gamer.account.toString(),
                modifier = Modifier.weight(1f),
                endText = stringResource(
                    if (gamer.sellerOption != null) R.string.page
                    else R.string.point
                ),
                isEnable = false,
                unFocusDeleteMode = true,
                hintColor = colorResource(id = R.color.gray)
            )
        }
        if (!gamer.isWinner() && uiState.phase is Winner) {
            NumberTextField(
                modifier = Modifier.weight(1f),
                endText = stringResource(R.string.point),
                isEnable = true,
                unFocusDeleteMode = true,
                hintColor = colorResource(id = R.color.nero)
            ) {
                event(ScoreEvent.OnUpdateWinnerPoint(gamer, it))
            }
        }

    }
}

@Composable
private fun ToggleRow(
    phase: Phase,
    gamer: Gamer,
    event: (ScoreEvent) -> Unit
) {
    val options = when (phase) {
        is Scoring -> ScoreOption.values()
        is Loser -> LoserOption.values()
        else -> throw Exception()
    }
    Spacer(modifier = Modifier.padding(5.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options, key = { it.ordinal }) { option ->
            OptionBox(
                text = option.korean,
                color = colorResource(id = R.color.non_click),
                isSelected = (if (phase is Scoring) gamer.scoreOption else gamer.loserOption).contains(option),
                onClick = {
                    event(
                        if (phase is Scoring) ScoreEvent.SelectScore(gamer, option as ScoreOption)
                        else ScoreEvent.SelectLoser(gamer, option as LoserOption)
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

@Preview(showBackground = true)
@Composable
fun ItemPreview() {
    ScoringGamerItem(
        index = 0,
        gamer = Gamer(
            name = "zero.dev",
            scoreOption = listOf(ScoreOption.President, ScoreOption.FiveShine)
        ),
        uiState = ScoreUiState(phase = Scoring())
    )
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
