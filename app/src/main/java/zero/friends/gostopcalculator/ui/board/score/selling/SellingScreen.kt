package zero.friends.gostopcalculator.ui.board.score.selling

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
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
        mainText = stringResource(R.string.selling_main_text),
        subText = stringResource(R.string.selling_sub_text),
        buttonText = stringResource(R.string.next),
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
            buttonText = stringResource(R.string.selling_extra_button),
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
                        endText = stringResource(R.string.page),
                        isEnable = true,
                        hintColor = colorResource(id = R.color.nero),
                        onValueChane = { count ->
                            onUpdateSeller(gamer, count)
                        }
                    )
                }
            }
        }
    }
}

