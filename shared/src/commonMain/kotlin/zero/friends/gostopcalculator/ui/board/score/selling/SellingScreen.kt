package zero.friends.gostopcalculator.ui.board.score.selling

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.ic_help_outline
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_selling
import zero.friends.gostopcalculator.shared.generated.resources.next
import zero.friends.gostopcalculator.shared.generated.resources.page
import zero.friends.gostopcalculator.shared.generated.resources.rule_sell
import zero.friends.gostopcalculator.shared.generated.resources.selling_extra_button
import zero.friends.gostopcalculator.shared.generated.resources.selling_main_text
import zero.friends.gostopcalculator.shared.generated.resources.selling_status_resting
import zero.friends.gostopcalculator.shared.generated.resources.selling_status_waiting
import zero.friends.gostopcalculator.shared.generated.resources.selling_sub_highlight
import zero.friends.gostopcalculator.shared.generated.resources.selling_sub_text
import zero.friends.gostopcalculator.ui.board.score.common.GamerListHeader
import zero.friends.gostopcalculator.ui.board.score.common.PlayerIndexBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBadgeBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePhaseLayout
import zero.friends.gostopcalculator.ui.board.score.common.ScorePlayerCard
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.ui.board.score.common.StepperNumberField

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
        highlightSubText = stringResource(Res.string.selling_sub_highlight),
        heroIcon = Res.drawable.ic_hero_selling,
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
            onButtonClick = onShowInfo,
            playerCount = gamers.size,
            buttonIcon = Res.drawable.ic_help_outline
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(
                items = gamers,
                key = { _, item -> item.id }
            ) { index, gamer ->
                SellingGamerItem(
                    index = index,
                    gamer = gamer,
                    onUpdateSeller = onUpdateSeller
                )
            }
        }
    }
}

@Composable
private fun SellingGamerItem(
    index: Int,
    gamer: Gamer,
    onUpdateSeller: (Gamer, Long) -> Unit,
) {
    val isSeller = gamer.score > 0

    ScorePlayerCard(isHighlighted = isSeller) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                PlayerIndexBadge(
                    index = index,
                    containerColor = if (isSeller) ScorePrimaryRed else ScoreDisabledBadgeBg,
                    textColor = if (isSeller) Color.White else ScoreSubtleGray,
                    size = 28.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = gamer.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isSeller) {
                            StatusPillBadge(
                                text = stringResource(Res.string.rule_sell),
                                backgroundColor = ScorePrimaryRed,
                                textColor = Color.White,
                                fontSize = 10.sp,
                                horizontalPadding = 6.dp,
                                verticalPadding = 2.dp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isSeller) {
                            stringResource(Res.string.selling_status_resting)
                        } else {
                            stringResource(Res.string.selling_status_waiting)
                        },
                        fontSize = 11.5.sp,
                        fontWeight = if (isSeller) FontWeight.Medium else FontWeight.Normal,
                        color = if (isSeller) ScorePrimaryRed else ScoreSubtleGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            StepperNumberField(
                value = gamer.score.toLong(),
                endText = stringResource(Res.string.page),
                isHighlighted = isSeller,
                filledPlusWhenHighlighted = true,
                centerWidth = 58.dp,
                centerHeight = 36.dp,
                buttonSize = 30.dp,
                spacing = 6.dp,
                inputTestTag = "selling_score_input_${index}",
                onDecrement = {
                    onUpdateSeller(gamer, (gamer.score.toLong() - 1L).coerceAtLeast(0L))
                },
                onIncrement = {
                    onUpdateSeller(gamer, gamer.score.toLong() + 1L)
                },
                onValueChange = { count ->
                    onUpdateSeller(gamer, count)
                }
            )
        }
    }
}
