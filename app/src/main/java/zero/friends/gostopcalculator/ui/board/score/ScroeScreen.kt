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
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.WinnerOption
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.*


sealed interface ScoreEvent {
    object Back : ScoreEvent
    class SelectScore(val gamer: Gamer, val option: ScoreOption) : ScoreEvent
}

@Composable
fun ScoreScreen(scoreViewModel: ScoreViewModel = hiltViewModel(), onBack: (gameId: Long) -> Unit) {
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
            buttonString = uiState.phase.getButtonText()
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
        LazyColumn(contentPadding = PaddingValues(2.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(uiState.playerResults) { index, item ->
                ScoringGamerItem(index, item, event)
            }

        }
    }
}

@Composable
fun ScoringGamerItem(index: Int, gamer: Gamer, event: (ScoreEvent) -> Unit = {}) {
    val color = colorResource(id = if (gamer.winnerOption.isNotEmpty()) R.color.gray else R.color.nero)

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
                color = colorResource(id = if (gamer.winnerOption.isEmpty()) R.color.orangey_red else R.color.gray)
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
                            if (gamer.winnerOption.contains(WinnerOption.Sell)) {
                                Spacer(modifier = Modifier.padding(3.dp))
                                OptionBox(WinnerOption.Sell.korean, colorResource(id = R.color.gray))
                            }
                        }
                    }
                }
                if (gamer.winnerOption.isEmpty()) {
                    Spacer(modifier = Modifier.padding(5.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ScoreOption.values(), key = { it.ordinal }) { option ->
                            OptionBox(
                                text = option.korean,
                                color = colorResource(id = R.color.non_click),
                                isSelected = gamer.scoreOption.contains(option),
                                onClick = { event(ScoreEvent.SelectScore(gamer, option)) }
                            )
                        }
                    }
                }

            }
        }
        if (gamer.winnerOption.isNotEmpty()) {
            NumberTextField(
                modifier = Modifier.weight(1f),
                endText = stringResource(R.string.page),
                isEnable = false,
                unFocusDeleteMode = true,
                hintColor = colorResource(id = R.color.gray)
            ) {

            }
        } else {

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
        )
    )
}

@Preview
@Composable
private fun Preview() {
    ScoreScreen(
        uiState = ScoreUiState(
            playerResults = listOf(
                Gamer(id = 0, name = "zero.dev", winnerOption = listOf(WinnerOption.Sell)),
                Gamer(
                    id = 1,
                    name = "dev",
                    scoreOption = listOf(ScoreOption.ThreeFuck, ScoreOption.FiveShine, ScoreOption.FirstDdadak)
                ),
                Gamer(id = 2, name = "zero", loserOption = listOf(LoserOption.GoBack, LoserOption.MongBak))
            )
        )
    )
}
