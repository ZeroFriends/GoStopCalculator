package zero.friends.gostopcalculator.ui.board.score.winner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.usecase.calculate.calculateShakeBombMultiplier
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.confirm_none
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_special_item_format
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_special_total_format
import zero.friends.gostopcalculator.shared.generated.resources.go
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_trophy
import zero.friends.gostopcalculator.shared.generated.resources.ic_menu_book
import zero.friends.gostopcalculator.shared.generated.resources.manual
import zero.friends.gostopcalculator.shared.generated.resources.next_step_2
import zero.friends.gostopcalculator.shared.generated.resources.point
import zero.friends.gostopcalculator.shared.generated.resources.result_chip_special_x2_format
import zero.friends.gostopcalculator.shared.generated.resources.rule_bomb
import zero.friends.gostopcalculator.shared.generated.resources.rule_loser
import zero.friends.gostopcalculator.shared.generated.resources.rule_shake
import zero.friends.gostopcalculator.shared.generated.resources.rule_winner
import zero.friends.gostopcalculator.shared.generated.resources.selling_cards_sold_format
import zero.friends.gostopcalculator.shared.generated.resources.selling_status_resting
import zero.friends.gostopcalculator.shared.generated.resources.winner_main_text
import zero.friends.gostopcalculator.shared.generated.resources.winner_multiplier_count_format
import zero.friends.gostopcalculator.shared.generated.resources.winner_special_multiplier_label
import zero.friends.gostopcalculator.shared.generated.resources.winner_status_confirmed
import zero.friends.gostopcalculator.shared.generated.resources.winner_sub_highlight
import zero.friends.gostopcalculator.shared.generated.resources.winner_sub_text
import zero.friends.gostopcalculator.shared.generated.resources.winner_total_multiplier_format
import zero.friends.gostopcalculator.ui.board.score.common.CompactNumericInputBox
import zero.friends.gostopcalculator.ui.board.score.common.DashedGamerCard
import zero.friends.gostopcalculator.ui.board.score.common.GamerListHeader
import zero.friends.gostopcalculator.ui.board.score.common.PlayerIndexBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreBorderNeutral
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBadgeBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreNeutralBtnBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePhaseLayout
import zero.friends.gostopcalculator.ui.board.score.common.ScorePlayerCard
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.ui.board.score.common.StepperNumberField
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
    onAdjustWinnerPoint: (Long, Long) -> Unit,
    onUpdateWinnerPoint: (Long, Long) -> Unit,
    onUpdateWinnerGo: (Long, Long) -> Unit,
    onCycleWinnerShake: (Long) -> Unit,
    onCycleWinnerBomb: (Long) -> Unit,
    onShowManual: () -> Unit
) {
    ScorePhaseLayout(
        gameTitle = game.name,
        mainText = stringResource(Res.string.winner_main_text),
        subText = stringResource(Res.string.winner_sub_text),
        highlightSubText = stringResource(Res.string.winner_sub_highlight),
        heroIcon = Res.drawable.ic_hero_trophy,
        buttonText = stringResource(Res.string.next_step_2),
        buttonEnabled = isNextEnabled,
        onBack = onBack,
        onExit = onExit,
        onButtonClick = onNext
    ) {
        WinnerGamerList(
            gamers = gamers,
            seller = seller,
            onAdjustWinnerPoint = onAdjustWinnerPoint,
            onUpdateWinnerPoint = onUpdateWinnerPoint,
            onUpdateWinnerGo = onUpdateWinnerGo,
            onCycleWinnerShake = onCycleWinnerShake,
            onCycleWinnerBomb = onCycleWinnerBomb,
            onShowManual = onShowManual
        )
    }
}

@Composable
private fun WinnerGamerList(
    gamers: List<Gamer>,
    seller: Gamer?,
    onAdjustWinnerPoint: (Long, Long) -> Unit,
    onUpdateWinnerPoint: (Long, Long) -> Unit,
    onUpdateWinnerGo: (Long, Long) -> Unit,
    onCycleWinnerShake: (Long) -> Unit,
    onCycleWinnerBomb: (Long) -> Unit,
    onShowManual: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        GamerListHeader(
            buttonText = stringResource(Res.string.manual),
            onButtonClick = onShowManual,
            playerCount = gamers.size,
            buttonIcon = Res.drawable.ic_menu_book
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
                if (gamer.id == seller?.id) {
                    DashedGamerCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
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
                                    containerColor = ScoreBorderNeutral,
                                    textColor = ScoreSubtleGray,
                                    size = 28.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = seller.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ScoreSubtleGray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        StatusPillBadge(
                                            text = seller.sellerOption?.localizedName().orEmpty(),
                                            backgroundColor = ScoreDisabledBadgeBg,
                                            textColor = ScoreSubtleGray,
                                            fontSize = 10.sp,
                                            horizontalPadding = 6.dp,
                                            verticalPadding = 2.dp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = stringResource(Res.string.selling_status_resting),
                                        fontSize = 11.5.sp,
                                        color = ScoreSubtleGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            StatusPillBadge(
                                text = stringResource(Res.string.selling_cards_sold_format, seller.score),
                                backgroundColor = ScoreDisabledBadgeBg,
                                textColor = ScoreMutedText,
                                fontSize = 11.5.sp,
                                horizontalPadding = 10.dp,
                                verticalPadding = 6.dp
                            )
                        }
                    }
                } else {
                    WinnerGamerItem(
                        index = index,
                        gamer = gamer,
                        onAdjustWinnerPoint = onAdjustWinnerPoint,
                        onUpdateWinnerPoint = onUpdateWinnerPoint,
                        onUpdateWinnerGo = onUpdateWinnerGo,
                        onCycleWinnerShake = onCycleWinnerShake,
                        onCycleWinnerBomb = onCycleWinnerBomb,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WinnerGamerItem(
    index: Int,
    gamer: Gamer,
    onAdjustWinnerPoint: (Long, Long) -> Unit,
    onUpdateWinnerPoint: (Long, Long) -> Unit,
    onUpdateWinnerGo: (Long, Long) -> Unit,
    onCycleWinnerShake: (Long) -> Unit,
    onCycleWinnerBomb: (Long) -> Unit,
) {
    val isWinner = gamer.score > 0 || gamer.go > 0

    ScorePlayerCard(isHighlighted = isWinner) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                ) {
                    PlayerIndexBadge(
                        index = index,
                        containerColor = if (isWinner) ScorePrimaryRed else ScoreNeutralBtnBg,
                        textColor = if (isWinner) Color.White else ScoreMutedText,
                        size = 26.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = gamer.name,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = ScoreTextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            StatusPillBadge(
                                text = stringResource(if (isWinner) Res.string.rule_winner else Res.string.rule_loser),
                                backgroundColor = if (isWinner) ScorePrimaryRed else ScoreDisabledBadgeBg,
                                textColor = if (isWinner) Color.White else ScoreMutedText,
                                fontSize = 10.sp,
                                horizontalPadding = 6.dp,
                                verticalPadding = 2.dp
                            )
                        }
                        if (isWinner) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(Res.string.winner_status_confirmed),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = ScorePrimaryRed,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepperNumberField(
                        value = gamer.score.toLong(),
                        endText = stringResource(Res.string.point),
                        isHighlighted = isWinner,
                        filledPlusWhenHighlighted = true,
                        centerWidth = 60.dp,
                        centerHeight = 34.dp,
                        buttonSize = 28.dp,
                        spacing = 4.dp,
                        inputTestTag = "winner_point_input_${index}",
                        onDecrement = { onAdjustWinnerPoint(gamer.id, -1L) },
                        onIncrement = { onAdjustWinnerPoint(gamer.id, 1L) },
                        onValueChange = { point -> onUpdateWinnerPoint(gamer.id, point) }
                    )
                    CompactNumericInputBox(
                        value = gamer.go.toLong(),
                        endText = stringResource(Res.string.go),
                        width = 50.dp,
                        height = 34.dp,
                        isHighlighted = isWinner && gamer.go > 0,
                        inputTestTag = "winner_go_input_${index}",
                        onValueChange = { go -> onUpdateWinnerGo(gamer.id, go) }
                    )
                }
            }

            if (isWinner) {
                val shakeCount = gamer.shakeCount
                val bombCount = gamer.bombCount
                val totalSpecialMult = calculateShakeBombMultiplier(shakeCount, bombCount)

                Divider(color = Color(0xFFF1F3F5), thickness = 1.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.winner_special_multiplier_label),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = ScoreMutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        if (shakeCount > 0 || bombCount > 0) {
                            StatusPillBadge(
                                text = stringResource(Res.string.winner_total_multiplier_format, totalSpecialMult),
                                backgroundColor = ScorePrimaryRed,
                                textColor = Color.White,
                                fontSize = 11.sp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 3.dp,
                            )
                        }
                    }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecialMultiplierChip(
                            name = stringResource(Res.string.rule_shake),
                            count = shakeCount,
                            tag = "winner_shake_chip_${index}",
                            onClick = { onCycleWinnerShake(gamer.id) }
                        )
                        SpecialMultiplierChip(
                            name = stringResource(Res.string.rule_bomb),
                            count = bombCount,
                            tag = "winner_bomb_chip_${index}",
                            onClick = { onCycleWinnerBomb(gamer.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecialMultiplierChip(
    name: String,
    count: Int,
    tag: String,
    onClick: () -> Unit,
) {
    val isActive = count > 0
    val mult = calculateShakeBombMultiplier(count, 0)
    val label = when {
        count <= 0 -> name
        count == 1 -> stringResource(Res.string.result_chip_special_x2_format, name)
        else -> stringResource(
            Res.string.winner_multiplier_count_format,
            name,
            count,
            mult,
        )
    }
    val noneState = stringResource(Res.string.confirm_none)
    val activeItemDesc = stringResource(Res.string.confirm_round_special_item_format, name, count)
    val activeStateDesc = stringResource(Res.string.confirm_round_special_total_format, activeItemDesc, mult)
    val shape = RoundedCornerShape(100.dp)
    Surface(
        modifier = Modifier
            .testTag(tag)
            .heightIn(min = 44.dp)
            .clip(shape)
            .semantics {
                stateDescription = if (isActive) activeStateDesc else noneState
            }
            .clickable(role = Role.Button, onClick = onClick),
        shape = shape,
        color = if (isActive) ScoreSoftPinkBg else ScoreNeutralBtnBg,
        border = BorderStroke(1.dp, if (isActive) ScoreSoftPinkBorder else ScoreBorderNeutral)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) ScorePrimaryRed else ScoreMutedText,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
