package zero.friends.gostopcalculator.ui.board.score

import android.widget.Toast
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import zero.friends.domain.model.*
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.DescriptionBox
import zero.friends.gostopcalculator.ui.common.NumberTextField
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.ui.common.background.GoStopButtonBackground
import zero.friends.gostopcalculator.ui.dialog.BasicDialog
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier


sealed interface ScoreEvent {
    object Back : ScoreEvent
    object OnNext : ScoreEvent
    class SelectScore(val gamer: Gamer, val option: ScoreOption) : ScoreEvent
    class SelectLoser(val gamer: Gamer, val option: LoserOption) : ScoreEvent
    object Complete : ScoreEvent
    class OnUpdateWinnerPoint(val gamer: Gamer, val point: Long) : ScoreEvent
    class OnUpdateSellerPoint(val gamer: Gamer, val count: Long) : ScoreEvent
    object OnClickSubButton : ScoreEvent
    object Exit : ScoreEvent
}

@Composable
fun ScoreScreen(
    scoreViewModel: ScoreViewModel = hiltViewModel(),
    onBack: (gameId: Long?) -> Unit,
    Exit: (gameId: Long) -> Unit,
    onComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val uiState by scoreViewModel.uiState().collectAsState()
    var openExtraDialog by remember {
        mutableStateOf(false)
    }
    var threeFuckDialog by remember {
        mutableStateOf(false)
    }
    var completeDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        scoreViewModel.escapeEvent()
            .onEach {
                onBack(uiState.game.id)
            }.launchIn(this)

        scoreViewModel.toast()
            .onEach {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }.launchIn(this)
    }

    BackHandler(true) {
        scoreViewModel.onBackPhase()
    }

    if (openExtraDialog) {
        when (uiState.phase) {
            is Selling -> ExtraActionDialog { openExtraDialog = false }
            else -> {
                //todo 설명서 페이지
            }
        }
    }

    if (completeDialog) {
        BasicDialog(
            confirmText = stringResource(id = android.R.string.ok),
            titleText = stringResource(R.string.dialog_text_complete),
            onClick = {
                scoreViewModel.calculateGameResult()
                onComplete()
            },
            onDismiss = {
                completeDialog = false
            }
        )
    }

    if (threeFuckDialog) {
        BasicDialog(
            confirmText = stringResource(id = android.R.string.ok),
            titleText = stringResource(id = R.string.three_fuck_title),
            text = stringResource(id = R.string.three_fuck_text),
            onDismiss = {
                threeFuckDialog = false
                scoreViewModel.selectScore(requireNotNull(uiState.threeFuckGamer), ScoreOption.ThreeFuck)
            },
            onClick = {
                threeFuckDialog = false
                scoreViewModel.calculateGameResult()
                onComplete()
            }
        )
    }

    ScoreScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        scoreEvent = { event ->
            when (event) {
                ScoreEvent.OnNext -> scoreViewModel.onNextPhase()
                ScoreEvent.Back -> scoreViewModel.onBackPhase()
                is ScoreEvent.SelectScore -> {
                    scoreViewModel.selectScore(event.gamer, event.option) {
                        threeFuckDialog = it
                    }
                }
                is ScoreEvent.OnUpdateWinnerPoint -> scoreViewModel.updateWinner(event.gamer, event.point)
                is ScoreEvent.SelectLoser -> scoreViewModel.selectLoser(event.gamer, event.option)
                ScoreEvent.Complete -> {
                    completeDialog = true
                }
                is ScoreEvent.OnUpdateSellerPoint -> {
                    scoreViewModel.updateSeller(event.gamer, event.count)
                }
                ScoreEvent.OnClickSubButton -> openExtraDialog = true
                ScoreEvent.Exit -> {
                    scoreViewModel.deleteRound()
                    Exit(uiState.game.id)
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
        modifier = TabKeyboardDownModifier(),
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = uiState.game.name,
                onBack = { scoreEvent(ScoreEvent.Back) },
                onAction = { scoreEvent(ScoreEvent.Exit) },
                actionIcon = painterResource(id = R.drawable.ic_topbar_close),
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
            RoundedCornerText(uiState.phase.extraButtonText(), onClick = {
                event(ScoreEvent.OnClickSubButton)
            })
        }
        Spacer(modifier = Modifier.padding(9.dp))

        LazyColumn(contentPadding = PaddingValues(2.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            itemsIndexed(
                items = uiState.playerResults,
                key = { _, item -> item.id }
            ) { index, gamer ->
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
                            index,
                            uiState.seller,
                            isSeller = true,
                            isEnable = false
                        )
                        else WinnerItem(index, gamer, isSeller = false, isEnable = true, event = event)
                    }
                    is Loser -> {
                        when (gamer.id) {
                            uiState.seller?.id -> WinnerItem(
                                index,
                                uiState.seller,
                                isSeller = true,
                                isEnable = false
                            )
                            uiState.winner?.id -> WinnerItem(
                                index,
                                uiState.winner,
                                isSeller = false,
                                isEnable = false
                            )
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
            modifier = Modifier.weight(1f),
            text = gamer.score.toString(),
            endText = stringResource(if (isSeller) R.string.page else R.string.point),
            isEnable = isEnable,
            hintColor = colorResource(id = if (isEnable) R.color.nero else R.color.gray),
            onValueChane = {
                event(
                    if (isSeller) ScoreEvent.OnUpdateSellerPoint(gamer, it)
                    else ScoreEvent.OnUpdateWinnerPoint(gamer, it)
                )
            }
        )

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

@Preview(showBackground = true)
@Composable
private fun WinnerItemPreview() {
    WinnerItem(index = 0, gamer = Gamer(name = "zero"), isSeller = true, isEnable = true)
}

@Preview(showBackground = true)
@Composable
private fun ScoringItemPreview() {
    ScoringItem(index = 0, gamer = Gamer(name = "zero"), toggleable = Loser)
}

@Preview
@Composable
private fun Preview() {
    ScoreScreen(
        uiState = ScoreUiState(
            playerResults = listOf(
                Gamer(id = 0, name = "zero.dev", sellerOption = SellerOption.Seller),
                Gamer(
                    id = 1,
                    name = "dev",
                    scoreOption = listOf(ScoreOption.FirstFuck, ScoreOption.FirstDdadak)
                ),
                Gamer(id = 2, name = "zero", loserOption = listOf(LoserOption.GoBak, LoserOption.MongBak)),
                Gamer(id = 3, name = "winner", winnerOption = WinnerOption.Winner)
            )
        )
    )
}
