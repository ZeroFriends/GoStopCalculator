package zero.friends.gostopcalculator.ui.board.score.loser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.board.score.common.BaseGamerItem
import zero.friends.gostopcalculator.ui.board.score.common.GamerListHeader
import zero.friends.gostopcalculator.ui.board.score.common.OptionBox
import zero.friends.gostopcalculator.ui.board.score.common.ScorePhaseLayout

@Composable
fun LoserScreen(
    game: Game,
    gamers: List<Gamer>,
    seller: Gamer?,
    winner: Gamer?,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    onExit: () -> Unit,
    onSelectLoser: (Gamer, LoserOption) -> Unit,
    onShowManual: () -> Unit
) {
    ScorePhaseLayout(
        gameTitle = game.name,
        mainText = stringResource(R.string.loser_main_text),
        subText = stringResource(R.string.loser_sub_text),
        buttonText = stringResource(R.string.next_step_3),
        buttonEnabled = true,
        onBack = onBack,
        onExit = onExit,
        onButtonClick = onComplete
    ) {
        LoserGamerList(
            gamers = gamers,
            seller = seller,
            winner = winner,
            onSelectLoser = onSelectLoser,
            onShowManual = onShowManual
        )
    }
}

@Composable
private fun LoserGamerList(
    gamers: List<Gamer>,
    seller: Gamer?,
    winner: Gamer?,
    onSelectLoser: (Gamer, LoserOption) -> Unit,
    onShowManual: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        GamerListHeader(
            buttonText = stringResource(R.string.manual),
            onButtonClick = onShowManual
        )
        Spacer(modifier = Modifier.padding(9.dp))

        LazyColumn(
            contentPadding = PaddingValues(2.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = gamers,
                key = { _, item -> item.id }
            ) { index, gamer ->
                when (gamer.id) {
                    seller?.id -> {
                        // Seller는 비활성화 상태로 표시
                        BaseGamerItem(
                            index = index,
                            gamer = seller,
                            isEnabled = false,
                            badge = seller.sellerOption?.korean
                        )
                    }
                    winner?.id -> {
                        // Winner는 비활성화 상태로 표시
                        BaseGamerItem(
                            index = index,
                            gamer = winner,
                            isEnabled = false,
                            badge = winner.winnerOption?.korean
                        )
                    }
                    else -> {
                        // 패자는 박 선택 가능
                        LoserGamerItem(
                            index = index,
                            gamer = gamer,
                            onSelectLoser = onSelectLoser
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoserGamerItem(
    index: Int,
    gamer: Gamer,
    onSelectLoser: (Gamer, LoserOption) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
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

        LoserOptionRow(
            modifier = Modifier.padding(start = 10.dp),
            gamer = gamer,
            onSelectLoser = onSelectLoser
        )
    }
}

@Composable
private fun LoserOptionRow(
    modifier: Modifier = Modifier,
    gamer: Gamer,
    onSelectLoser: (Gamer, LoserOption) -> Unit
) {
    val options = LoserOption.values()

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options, key = { it.ordinal }) { option ->
            OptionBox(
                text = option.korean,
                color = colorResource(id = R.color.non_click),
                isSelected = gamer.loserOption.contains(option),
                onClick = {
                    onSelectLoser(gamer, option)
                }
            )
        }
    }
}

