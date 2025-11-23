package zero.friends.gostopcalculator.ui.board.score.scoring

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
import zero.friends.domain.model.*
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.board.score.common.BaseGamerItem
import zero.friends.gostopcalculator.ui.board.score.common.GamerListHeader
import zero.friends.gostopcalculator.ui.board.score.common.OptionBox
import zero.friends.gostopcalculator.ui.board.score.common.ScorePhaseLayout

@Composable
fun ScoringScreen(
    game: Game,
    gamers: List<Gamer>,
    seller: Gamer?,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onExit: () -> Unit,
    onSelectScore: (Gamer, ScoreOption) -> Unit,
    onShowManual: () -> Unit
) {
    ScorePhaseLayout(
        gameTitle = game.name,
        mainText = stringResource(R.string.scoring_main_text),
        subText = stringResource(R.string.scoring_sub_text),
        buttonText = stringResource(R.string.next_step_1),
        buttonEnabled = true,
        onBack = onBack,
        onExit = onExit,
        onButtonClick = onNext
    ) {
        ScoringGamerList(
            gamers = gamers,
            seller = seller,
            onSelectScore = onSelectScore,
            onShowManual = onShowManual
        )
    }
}

@Composable
private fun ScoringGamerList(
    gamers: List<Gamer>,
    seller: Gamer?,
    onSelectScore: (Gamer, ScoreOption) -> Unit,
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
                if (gamer.id == seller?.id) {
                    // Seller는 비활성화 상태로 표시
                    BaseGamerItem(
                        index = index,
                        gamer = seller,
                        isEnabled = false,
                        badge = seller.sellerOption?.korean
                    )
                } else {
                    // 일반 플레이어는 점수 선택 가능
                    ScoringGamerItem(
                        index = index,
                        gamer = gamer,
                        onSelectScore = onSelectScore
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoringGamerItem(
    index: Int,
    gamer: Gamer,
    onSelectScore: (Gamer, ScoreOption) -> Unit
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

        ScoreOptionRow(
            modifier = Modifier.padding(start = 10.dp),
            gamer = gamer,
            onSelectScore = onSelectScore
        )
    }
}

@Composable
private fun ScoreOptionRow(
    modifier: Modifier = Modifier,
    gamer: Gamer,
    onSelectScore: (Gamer, ScoreOption) -> Unit
) {
    val options = ScoreOption.values()

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options, key = { it.ordinal }) { option ->
            OptionBox(
                text = option.korean,
                color = colorResource(id = R.color.non_click),
                isSelected = gamer.scoreOption.contains(option),
                onClick = {
                    onSelectScore(gamer, option)
                }
            )
        }
    }
}

