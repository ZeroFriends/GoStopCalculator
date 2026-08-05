package zero.friends.gostopcalculator.ui.board.score.selling

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.next
import zero.friends.gostopcalculator.shared.generated.resources.page
import zero.friends.gostopcalculator.shared.generated.resources.selling_extra_button
import zero.friends.gostopcalculator.shared.generated.resources.selling_main_text
import zero.friends.gostopcalculator.shared.generated.resources.selling_sub_text
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.ui.board.score.common.BaseGamerItem
import zero.friends.gostopcalculator.ui.board.score.common.GamerListHeader
import zero.friends.gostopcalculator.ui.board.score.common.ScorePhaseLayout
import zero.friends.gostopcalculator.ui.common.NumberTextField

@Composable
fun SellingScreen(
    game: Game,
    gamers: List<Gamer>,
    isNextEnabled: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onExit: () -> Unit,
    onUpdateSeller: (Gamer, Long) -> Unit,
    onShowInfo: () -> Unit
) {
    ScorePhaseLayout(
        gameTitle = game.name,
        mainText = stringResource(Res.string.selling_main_text),
        subText = stringResource(Res.string.selling_sub_text),
        buttonText = stringResource(Res.string.next),
        buttonEnabled = isNextEnabled,
        onBack = onBack,
        onExit = onExit,
        onButtonClick = onNext
    ) {
        SellingGamerList(
            gamers = gamers,
            onUpdateSeller = onUpdateSeller,
            onShowInfo = onShowInfo
        )
    }
}

@Composable
private fun SellingGamerList(
    gamers: List<Gamer>,
    onUpdateSeller: (Gamer, Long) -> Unit,
    onShowInfo: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        GamerListHeader(
            buttonText = stringResource(Res.string.selling_extra_button),
            onButtonClick = onShowInfo
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
                BaseGamerItem(
                    index = index,
                    gamer = gamer,
                    isEnabled = true
                ) {
                    NumberTextField(
                        modifier = Modifier.weight(1f),
                        text = gamer.score.toString(),
                        endText = stringResource(Res.string.page),
                        isEnable = true,
                        hintColor = Nero,
                        onValueChange = { count ->
                            onUpdateSeller(gamer, count)
                        }
                    )
                }
            }
        }
    }
}
