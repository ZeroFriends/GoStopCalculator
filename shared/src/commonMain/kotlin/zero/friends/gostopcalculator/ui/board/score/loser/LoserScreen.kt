package zero.friends.gostopcalculator.ui.board.score.loser

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.usecase.calculate.isGoBakSoloCoverageMode
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.ic_check_small
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_gavel
import zero.friends.gostopcalculator.shared.generated.resources.ic_menu_book
import zero.friends.gostopcalculator.shared.generated.resources.loser_gobak_exempt_badge_format
import zero.friends.gostopcalculator.shared.generated.resources.loser_gobak_exempt_notice
import zero.friends.gostopcalculator.shared.generated.resources.loser_gobak_exempt_with_bak_format
import zero.friends.gostopcalculator.shared.generated.resources.loser_gobak_solo_banner_format
import zero.friends.gostopcalculator.shared.generated.resources.loser_gobak_solo_only_badge
import zero.friends.gostopcalculator.shared.generated.resources.loser_gobak_solo_with_bak_format
import zero.friends.gostopcalculator.shared.generated.resources.loser_main_text
import zero.friends.gostopcalculator.shared.generated.resources.loser_no_penalty_badge
import zero.friends.gostopcalculator.shared.generated.resources.loser_penalty_multiplier_format
import zero.friends.gostopcalculator.shared.generated.resources.loser_select_penalty_hint
import zero.friends.gostopcalculator.shared.generated.resources.loser_sub_highlight
import zero.friends.gostopcalculator.shared.generated.resources.loser_sub_text
import zero.friends.gostopcalculator.shared.generated.resources.manual
import zero.friends.gostopcalculator.shared.generated.resources.next_step_3
import zero.friends.gostopcalculator.shared.generated.resources.rule_loser
import zero.friends.gostopcalculator.shared.generated.resources.rule_sell
import zero.friends.gostopcalculator.shared.generated.resources.rule_winner
import zero.friends.gostopcalculator.shared.generated.resources.selling_cards_sold_format
import zero.friends.gostopcalculator.shared.generated.resources.selling_status_resting
import zero.friends.gostopcalculator.shared.generated.resources.winner_penalty_exempt
import zero.friends.gostopcalculator.shared.generated.resources.winner_status_confirmed
import zero.friends.gostopcalculator.ui.board.score.common.DashedGamerCard
import zero.friends.gostopcalculator.ui.board.score.common.GamerListHeader
import zero.friends.gostopcalculator.ui.board.score.common.PlayerIndexBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreBorderNeutral
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDarkBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBadgeBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreNeutralBtnBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePhaseLayout
import zero.friends.gostopcalculator.ui.board.score.common.ScorePlayerCard
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkButtonBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.util.localizedName
import zero.friends.gostopcalculator.util.rememberEffectiveBakSummary

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
        mainText = stringResource(Res.string.loser_main_text),
        subText = stringResource(Res.string.loser_sub_text),
        highlightSubText = stringResource(Res.string.loser_sub_highlight),
        heroIcon = Res.drawable.ic_hero_gavel,
        buttonText = stringResource(Res.string.next_step_3),
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
    val losers = remember(gamers, winner?.id, seller?.id) {
        gamers.filter { it.id != winner?.id && it.id != seller?.id }
    }
    val loserCount = losers.size
    val goBakGamer = remember(losers, loserCount) {
        if (isGoBakSoloCoverageMode(loserCount)) {
            losers.firstOrNull { it.loserOption.contains(LoserOption.GoBak) }
        } else {
            null
        }
    }

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
                when (gamer.id) {
                    seller?.id -> SellerExemptCard(index = index, seller = seller)
                    winner?.id -> WinnerExemptCard(index = index, winner = winner)
                    else -> {
                        val otherLoserNames = losers
                            .filter { it.id != gamer.id }
                            .joinToString(", ") { it.name }
                        LoserGamerItem(
                            index = index,
                            gamer = gamer,
                            loserCount = loserCount,
                            goBakGamer = goBakGamer,
                            otherLoserNames = otherLoserNames,
                            onSelectLoser = onSelectLoser
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WinnerExemptCard(
    index: Int,
    winner: Gamer,
) {
    ScorePlayerCard(isHighlighted = true) {
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
                    containerColor = ScorePrimaryRed,
                    textColor = Color.White,
                    size = 28.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = winner.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        StatusPillBadge(
                            text = stringResource(Res.string.rule_winner),
                            backgroundColor = ScorePrimaryRed,
                            textColor = Color.White,
                            fontSize = 10.sp,
                            horizontalPadding = 6.dp,
                            verticalPadding = 2.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(Res.string.winner_status_confirmed),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = ScorePrimaryRed,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            StatusPillBadge(
                text = stringResource(Res.string.winner_penalty_exempt),
                backgroundColor = ScoreSoftPinkButtonBg,
                textColor = ScorePrimaryRed,
                borderColor = ScoreSoftPinkBorder,
                fontSize = 11.sp,
                horizontalPadding = 10.dp,
                verticalPadding = 5.dp
            )
        }
    }
}

@Composable
private fun SellerExemptCard(
    index: Int,
    seller: Gamer,
) {
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
                            text = stringResource(Res.string.rule_sell),
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
}

@Composable
private fun LoserGamerItem(
    index: Int,
    gamer: Gamer,
    loserCount: Int,
    goBakGamer: Gamer?,
    otherLoserNames: String,
    onSelectLoser: (Gamer, LoserOption) -> Unit
) {
    val bakSummary = rememberEffectiveBakSummary(gamer.loserOption, loserCount)
    val isGoBakSoloPayer = goBakGamer?.id == gamer.id
    val isGoBakExempted = goBakGamer != null && goBakGamer.id != gamer.id
    val hasPenalty = gamer.loserOption.isNotEmpty()

    ScorePlayerCard(
        borderColor = when {
            isGoBakSoloPayer -> ScorePrimaryRed
            hasPenalty -> ScoreSoftPinkBorder
            else -> ScoreCardBorder
        },
        borderWidth = if (isGoBakSoloPayer) 1.5.dp else 1.dp,
        elevation = if (isGoBakSoloPayer) 3.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerIndexBadge(
                    index = index,
                    containerColor = if (isGoBakSoloPayer) ScorePrimaryRed else ScoreDarkBadge,
                    textColor = Color.White,
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
                        StatusPillBadge(
                            text = stringResource(Res.string.rule_loser),
                            backgroundColor = ScoreSoftPinkBg,
                            textColor = ScorePrimaryRed,
                            fontSize = 10.sp,
                            horizontalPadding = 6.dp,
                            verticalPadding = 2.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(Res.string.loser_select_penalty_hint),
                        fontSize = 11.5.sp,
                        color = ScoreSubtleGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 배수 및 고박 독박/면제 배지를 별도 행에 배치해 320dp 좁은 화면 및 12자 이름에서도 잘림 없이 가독성 보장
            Row(modifier = Modifier.fillMaxWidth()) {
                when {
                    isGoBakSoloPayer -> {
                        val badgeText = if (bakSummary.hasEffectiveBaks) {
                            stringResource(
                                Res.string.loser_gobak_solo_with_bak_format,
                                bakSummary.names,
                                bakSummary.multiplier
                            )
                        } else {
                            stringResource(Res.string.loser_gobak_solo_only_badge)
                        }
                        StatusPillBadge(
                            text = badgeText,
                            backgroundColor = ScoreSoftPinkButtonBg,
                            textColor = ScorePrimaryRed,
                            borderColor = ScoreSoftPinkBorder,
                            leadingDotColor = ScorePrimaryRed,
                            fontSize = 11.sp,
                            horizontalPadding = 9.dp,
                            verticalPadding = 4.dp
                        )
                    }
                    isGoBakExempted -> {
                        val payerName = goBakGamer.name
                        val badgeText = if (bakSummary.hasEffectiveBaks) {
                            stringResource(
                                Res.string.loser_gobak_exempt_with_bak_format,
                                bakSummary.names,
                                bakSummary.multiplier,
                                payerName
                            )
                        } else {
                            stringResource(
                                Res.string.loser_gobak_exempt_badge_format,
                                payerName
                            )
                        }
                        StatusPillBadge(
                            text = badgeText,
                            backgroundColor = ScoreMintBg,
                            textColor = ScoreMintText,
                            fontSize = 11.sp,
                            horizontalPadding = 9.dp,
                            verticalPadding = 4.dp
                        )
                    }
                    hasPenalty -> {
                        StatusPillBadge(
                            text = stringResource(
                                Res.string.loser_penalty_multiplier_format,
                                bakSummary.names,
                                bakSummary.multiplier
                            ),
                            backgroundColor = ScoreSoftPinkButtonBg,
                            textColor = ScorePrimaryRed,
                            borderColor = ScoreSoftPinkBorder,
                            leadingDotColor = ScorePrimaryRed,
                            fontSize = 11.sp,
                            horizontalPadding = 9.dp,
                            verticalPadding = 4.dp
                        )
                    }
                    else -> {
                        StatusPillBadge(
                            text = stringResource(Res.string.loser_no_penalty_badge),
                            backgroundColor = ScoreNeutralBtnBg,
                            textColor = ScoreMutedText,
                            fontSize = 11.sp,
                            horizontalPadding = 9.dp,
                            verticalPadding = 4.dp
                        )
                    }
                }
            }

            LoserOptionRow(
                gamer = gamer,
                onSelectLoser = onSelectLoser
            )

            if (isGoBakSoloPayer && otherLoserNames.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = ScoreSoftPinkButtonBg,
                    border = BorderStroke(1.dp, ScoreSoftPinkBorder)
                ) {
                    Text(
                        text = stringResource(Res.string.loser_gobak_solo_banner_format, otherLoserNames),
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ScorePrimaryRed,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            } else if (isGoBakExempted) {
                Text(
                    text = stringResource(Res.string.loser_gobak_exempt_notice),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = ScoreMutedText,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun LoserOptionRow(
    gamer: Gamer,
    onSelectLoser: (Gamer, LoserOption) -> Unit
) {
    val options = LoserOption.entries
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 340.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        pair.forEach { option ->
                            LoserOptionChip(
                                gamer = gamer,
                                option = option,
                                onSelectLoser = onSelectLoser,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.forEach { option ->
                    LoserOptionChip(
                        gamer = gamer,
                        option = option,
                        onSelectLoser = onSelectLoser,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoserOptionChip(
    gamer: Gamer,
    option: LoserOption,
    onSelectLoser: (Gamer, LoserOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonShape = RoundedCornerShape(10.dp)
    val isSelected = gamer.loserOption.contains(option)
    val optionLabel = option.localizedName()
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(buttonShape)
            .semantics { contentDescription = "${gamer.name}, $optionLabel" }
            .toggleable(
                value = isSelected,
                role = Role.Checkbox,
                onValueChange = { onSelectLoser(gamer, option) }
            ),
        shape = buttonShape,
        color = if (isSelected) ScorePrimaryRed else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, ScoreBorderNeutral),
        elevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    painter = painterResource(Res.drawable.ic_check_small),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
                text = optionLabel,
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) Color.White else ScoreTextDark,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
