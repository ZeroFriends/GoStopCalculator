package zero.friends.gostopcalculator.ui.board.score.winner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.board.score.common.BaseGamerItem
import zero.friends.gostopcalculator.ui.board.score.common.GamerListHeader
import zero.friends.gostopcalculator.ui.board.score.common.ScorePhaseLayout
import zero.friends.gostopcalculator.ui.common.NumberTextField

@Composable
fun WinnerScreen(
    game: Game,
    gamers: List<Gamer>,
    seller: Gamer?,
    isNextEnabled: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onExit: () -> Unit,
    onUpdateWinner: (Gamer, Long, Long) -> Unit,
    onShowManual: () -> Unit
) {
    ScorePhaseLayout(
        gameTitle = game.name,
        mainText = stringResource(R.string.winner_main_text),
        subText = stringResource(R.string.winner_sub_text),
        buttonText = stringResource(R.string.next_step_2),
        buttonEnabled = isNextEnabled,
        onBack = onBack,
        onExit = onExit,
        onButtonClick = onNext
    ) {
        WinnerGamerList(
            gamers = gamers,
            seller = seller,
            onUpdateWinner = onUpdateWinner,
            onShowManual = onShowManual
        )
    }
}

@Composable
private fun WinnerGamerList(
    gamers: List<Gamer>,
    seller: Gamer?,
    onUpdateWinner: (Gamer, Long, Long) -> Unit,
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
                    // 일반 플레이어는 승자 점수 입력 가능
                    WinnerGamerItem(
                        index = index,
                        gamer = gamer,
                        onUpdateWinner = onUpdateWinner
                    )
                }
            }
        }
    }
}

@Composable
private fun WinnerGamerItem(
    index: Int,
    gamer: Gamer,
    onUpdateWinner: (Gamer, Long, Long) -> Unit
) {
    BaseGamerItem(
        index = index,
        gamer = gamer,
        isEnabled = true
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberTextField(
                modifier = Modifier.weight(1f),
                text = gamer.score.toString(),
                endText = stringResource(R.string.point),
                isEnable = true,
                hintColor = colorResource(id = R.color.nero),
                onValueChange = { point ->
                    onUpdateWinner(gamer, point, gamer.go.toLong())
                }
            )
            NumberTextField(
                modifier = Modifier.width(88.dp),
                text = gamer.go.toString(),
                endText = stringResource(R.string.go),
                isEnable = true,
                hintColor = colorResource(id = R.color.nero),
                onValueChange = { go ->
                    onUpdateWinner(gamer, gamer.score.toLong(), go)
                }
            )
        }
    }
}
