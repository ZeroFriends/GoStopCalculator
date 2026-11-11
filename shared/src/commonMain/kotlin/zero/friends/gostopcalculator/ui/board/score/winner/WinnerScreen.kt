package zero.friends.gostopcalculator.ui.board.score.winner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.go
import zero.friends.gostopcalculator.shared.generated.resources.manual
import zero.friends.gostopcalculator.shared.generated.resources.next_step_2
import zero.friends.gostopcalculator.shared.generated.resources.point
import zero.friends.gostopcalculator.shared.generated.resources.winner_main_text
import zero.friends.gostopcalculator.shared.generated.resources.winner_sub_text
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.ui.board.score.common.BaseGamerItem
import zero.friends.gostopcalculator.ui.board.score.common.GamerListHeader
import zero.friends.gostopcalculator.ui.board.score.common.ScorePhaseLayout
import zero.friends.gostopcalculator.ui.common.NumberTextField
import zero.friends.gostopcalculator.util.localizedName

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
        mainText = stringResource(Res.string.winner_main_text),
        subText = stringResource(Res.string.winner_sub_text),
        buttonText = stringResource(Res.string.next_step_2),
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
            buttonText = stringResource(Res.string.manual),
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
                    BaseGamerItem(
                        index = index,
                        gamer = seller,
                        isEnabled = false,
                        badge = seller.sellerOption?.localizedName()
                    )
                } else {
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberTextField(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                text = gamer.score.toString(),
                endText = stringResource(Res.string.point),
                isEnable = true,
                hintColor = Nero,
                onValueChange = { point ->
                    onUpdateWinner(gamer, point, gamer.go.toLong())
                }
            )
            NumberTextField(
                modifier = Modifier
                    .width(84.dp)
                    .height(44.dp),
                text = gamer.go.toString(),
                endText = stringResource(Res.string.go),
                isEnable = true,
                hintColor = Nero,
                onValueChange = { go ->
                    onUpdateWinner(gamer, gamer.score.toLong(), go)
                }
            )
        }
    }
}
