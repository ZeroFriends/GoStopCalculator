package zero.friends.gostopcalculator.ui.board.score

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.usecase.calculate.calculateGoMultiplier
import zero.friends.domain.usecase.calculate.calculateShakeBombMultiplier
import zero.friends.domain.usecase.calculate.calculateWinnerScore
import zero.friends.domain.usecase.calculate.isGoBakSoloCoverageMode
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.confirm_none
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_base_score
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_go_add_format
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_go_label
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_go_mult_format
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_go_none_format
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_gobak_exempt_badge
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_gobak_exempt_desc
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_gobak_solo_badge
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_gobak_solo_desc_only
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_gobak_solo_desc_with_bak
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_penalty_label
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_recheck
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_selling_label
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_shake_bomb_label
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_special_item_format
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_special_label
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_special_total_format
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_submit
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.confirm_round_title
import zero.friends.gostopcalculator.shared.generated.resources.ic_check_small
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_calculate
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_three_fuck
import zero.friends.gostopcalculator.shared.generated.resources.loser_gobak_exempt_with_bak_format
import zero.friends.gostopcalculator.shared.generated.resources.loser_penalty_multiplier_format
import zero.friends.gostopcalculator.shared.generated.resources.point
import zero.friends.gostopcalculator.shared.generated.resources.rule_bomb
import zero.friends.gostopcalculator.shared.generated.resources.rule_shake
import zero.friends.gostopcalculator.shared.generated.resources.rule_three_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_winner
import zero.friends.gostopcalculator.shared.generated.resources.selling_cards_sold_format
import zero.friends.gostopcalculator.shared.generated.resources.three_fuck_confirm_submit
import zero.friends.gostopcalculator.shared.generated.resources.three_fuck_payout_label
import zero.friends.gostopcalculator.shared.generated.resources.three_fuck_payout_value
import zero.friends.gostopcalculator.shared.generated.resources.three_fuck_status_label
import zero.friends.gostopcalculator.shared.generated.resources.three_fuck_status_value
import zero.friends.gostopcalculator.shared.generated.resources.three_fuck_summary_badge
import zero.friends.gostopcalculator.shared.generated.resources.three_fuck_text
import zero.friends.gostopcalculator.shared.generated.resources.three_fuck_title
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreNeutralBtnBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkButtonBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSummaryCardBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.util.localizedName
import zero.friends.gostopcalculator.util.rememberEffectiveBakSummary
import zero.friends.gostopcalculator.util.separateComma

@Composable
fun RoundConfirmDialog(
    winner: Gamer?,
    seller: Gamer?,
    gamers: List<Gamer>,
    onRecheck: () -> Unit,
    onConfirm: () -> Unit,
) {
    val pointUnit = stringResource(Res.string.point)
    val baseScore = winner?.score ?: 0
    val goCount = winner?.go ?: 0
    val shakeCount = winner?.shakeCount ?: 0
    val bombCount = winner?.bombCount ?: 0
    val totalScore = calculateWinnerScore(baseScore, goCount, shakeCount, bombCount)
    val shakeBombMult = calculateShakeBombMultiplier(shakeCount, bombCount)

    val goDisplay = when {
        goCount <= 0 -> stringResource(Res.string.confirm_round_go_none_format, 0)
        goCount <= 2 -> stringResource(Res.string.confirm_round_go_add_format, goCount)
        else -> stringResource(Res.string.confirm_round_go_mult_format, goCount, calculateGoMultiplier(goCount))
    }

    val losers = remember(gamers, winner?.id, seller?.id) {
        gamers.filter { it.id != winner?.id && it.id != seller?.id }
    }
    val loserCount = losers.size
    val goBakSoloPayer = remember(losers, loserCount) {
        if (isGoBakSoloCoverageMode(loserCount)) {
            losers.firstOrNull { it.loserOption.contains(LoserOption.GoBak) }
        } else {
            null
        }
    }
    val penalizedLosers = remember(losers) { losers.filter { it.loserOption.isNotEmpty() } }
    val specialOptionGamers = remember(gamers) { gamers.filter { it.scoreOption.isNotEmpty() } }

    ConfirmModalShell(
        icon = Res.drawable.ic_hero_calculate,
        title = stringResource(Res.string.confirm_round_title),
        subtitle = stringResource(Res.string.confirm_round_subtitle),
        recheckText = stringResource(Res.string.confirm_round_recheck),
        confirmText = stringResource(Res.string.confirm_round_submit),
        onRecheck = onRecheck,
        onConfirm = onConfirm
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = ScoreSummaryCardBg,
            border = BorderStroke(1.dp, ScoreCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        StatusPillBadge(
                            text = stringResource(Res.string.rule_winner),
                            backgroundColor = ScorePrimaryRed,
                            textColor = Color.White,
                            fontSize = 11.sp,
                            horizontalPadding = 8.dp,
                            verticalPadding = 3.dp
                        )
                        Text(
                            text = winner?.name.orEmpty(),
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "+${totalScore.toLong().separateComma()}$pointUnit",
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ScorePrimaryRed,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                SummaryKeyValueRow(
                    label = stringResource(Res.string.confirm_round_base_score),
                    value = "${baseScore.toLong().separateComma()}$pointUnit"
                )
                SummaryKeyValueRow(
                    label = stringResource(Res.string.confirm_round_go_label),
                    value = goDisplay
                )
                if (shakeCount > 0 || bombCount > 0) {
                    val activeItems = buildList {
                        if (shakeCount > 0) {
                            add(
                                stringResource(
                                    Res.string.confirm_round_special_item_format,
                                    stringResource(Res.string.rule_shake),
                                    shakeCount,
                                )
                            )
                        }
                        if (bombCount > 0) {
                            add(
                                stringResource(
                                    Res.string.confirm_round_special_item_format,
                                    stringResource(Res.string.rule_bomb),
                                    bombCount,
                                )
                            )
                        }
                    }.joinToString(" · ")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = stringResource(Res.string.confirm_round_shake_bomb_label),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = ScoreMutedText,
                            modifier = Modifier.padding(top = 2.dp, end = 8.dp)
                        )
                        FlowRow(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            StatusPillBadge(
                                text = stringResource(
                                    Res.string.confirm_round_special_total_format,
                                    activeItems,
                                    shakeBombMult,
                                ),
                                backgroundColor = ScoreSoftPinkBg,
                                textColor = ScorePrimaryRed,
                                fontSize = 11.5.sp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 3.dp,
                                maxLines = 2
                            )
                        }
                    }
                }

                if (goBakSoloPayer != null) {
                    GoBakSoloPayerSummaryCard(
                        payer = goBakSoloPayer,
                        loserCount = loserCount
                    )
                    losers
                        .filter { it.id != goBakSoloPayer.id }
                        .forEach { exempted ->
                            GoBakExemptedSummaryCard(
                                exempted = exempted,
                                payerName = goBakSoloPayer.name,
                                loserCount = loserCount
                            )
                        }
                } else {
                    NormalPenaltySummaryRow(
                        penalizedLosers = penalizedLosers,
                        loserCount = loserCount
                    )
                }

                if (seller != null) {
                    val soldText = stringResource(Res.string.selling_cards_sold_format, seller.score)
                    SummaryKeyValueRow(
                        label = stringResource(Res.string.confirm_round_selling_label),
                        value = "${seller.name} ($soldText)",
                        valueColor = ScoreMintText
                    )
                }

                if (specialOptionGamers.isNotEmpty()) {
                    val specialText = specialOptionGamers.map { gamer ->
                        val opts = gamer.scoreOption
                            .sortedBy { it.ordinal }
                            .map { it.localizedName() }
                            .joinToString(", ")
                        "${gamer.name}: $opts"
                    }.joinToString("\n")
                    SummaryKeyValueRow(
                        label = stringResource(Res.string.confirm_round_special_label),
                        value = specialText
                    )
                }
            }
        }
    }
}

@Composable
private fun GoBakSoloPayerSummaryCard(
    payer: Gamer,
    loserCount: Int,
) {
    val bakSummary = rememberEffectiveBakSummary(payer.loserOption, loserCount)
    val payerDesc = if (bakSummary.hasEffectiveBaks) {
        stringResource(Res.string.confirm_round_gobak_solo_desc_with_bak, bakSummary.names, bakSummary.multiplier)
    } else {
        stringResource(Res.string.confirm_round_gobak_solo_desc_only)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ScoreSoftPinkButtonBg,
        border = BorderStroke(1.dp, ScoreSoftPinkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = payer.name,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScoreTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )
                StatusPillBadge(
                    text = stringResource(Res.string.confirm_round_gobak_solo_badge),
                    backgroundColor = Color.White,
                    textColor = ScorePrimaryRed,
                    borderColor = ScoreSoftPinkBorder,
                    fontSize = 10.5.sp,
                    horizontalPadding = 7.dp,
                    verticalPadding = 2.dp
                )
            }
            Text(
                text = payerDesc,
                fontSize = 11.sp,
                color = ScoreMutedText
            )
        }
    }
}

@Composable
private fun GoBakExemptedSummaryCard(
    exempted: Gamer,
    payerName: String,
    loserCount: Int,
) {
    val bakSummary = rememberEffectiveBakSummary(exempted.loserOption, loserCount)
    val exemptedDesc = if (bakSummary.hasEffectiveBaks) {
        stringResource(
            Res.string.loser_gobak_exempt_with_bak_format,
            bakSummary.names,
            bakSummary.multiplier,
            payerName
        )
    } else {
        stringResource(Res.string.confirm_round_gobak_exempt_desc, payerName)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ScoreCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exempted.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ScoreTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )
                StatusPillBadge(
                    text = stringResource(Res.string.confirm_round_gobak_exempt_badge),
                    backgroundColor = ScoreMintBg,
                    textColor = ScoreMintText,
                    fontSize = 10.5.sp,
                    horizontalPadding = 7.dp,
                    verticalPadding = 2.dp
                )
            }
            Text(
                text = exemptedDesc,
                fontSize = 10.5.sp,
                color = ScoreMutedText
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NormalPenaltySummaryRow(
    penalizedLosers: List<Gamer>,
    loserCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = stringResource(Res.string.confirm_round_penalty_label),
            fontSize = 12.5.sp,
            color = ScoreMutedText,
            modifier = Modifier.padding(top = 2.dp, end = 8.dp)
        )
        if (penalizedLosers.isEmpty()) {
            Text(
                text = stringResource(Res.string.confirm_none),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = ScoreMutedText
            )
        } else {
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                penalizedLosers.forEach { loser ->
                    val bakSummary = rememberEffectiveBakSummary(loser.loserOption, loserCount)
                    val multLabel = stringResource(
                        Res.string.loser_penalty_multiplier_format,
                        bakSummary.names,
                        bakSummary.multiplier
                    )
                    StatusPillBadge(
                        text = "${loser.name} ($multLabel)",
                        backgroundColor = ScoreSoftPinkBg,
                        textColor = ScorePrimaryRed,
                        fontSize = 11.sp,
                        horizontalPadding = 8.dp,
                        verticalPadding = 3.dp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun ThreeFuckConfirmDialog(
    gamer: Gamer?,
    onRecheck: () -> Unit,
    onConfirm: () -> Unit,
) {
    ConfirmModalShell(
        icon = Res.drawable.ic_hero_three_fuck,
        title = stringResource(Res.string.three_fuck_title),
        subtitle = stringResource(Res.string.three_fuck_text),
        recheckText = stringResource(Res.string.confirm_round_recheck),
        confirmText = stringResource(Res.string.three_fuck_confirm_submit),
        onRecheck = onRecheck,
        onConfirm = onConfirm
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = ScoreSummaryCardBg,
            border = BorderStroke(1.dp, ScoreCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        StatusPillBadge(
                            text = stringResource(Res.string.rule_three_fuck),
                            backgroundColor = ScorePrimaryRed,
                            textColor = Color.White,
                            fontSize = 11.sp,
                            horizontalPadding = 8.dp,
                            verticalPadding = 3.dp
                        )
                        Text(
                            text = gamer?.name.orEmpty(),
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = stringResource(Res.string.three_fuck_summary_badge),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScorePrimaryRed,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                SummaryKeyValueRow(
                    label = stringResource(Res.string.three_fuck_payout_label),
                    value = stringResource(Res.string.three_fuck_payout_value),
                    valueColor = ScorePrimaryRed
                )
                SummaryKeyValueRow(
                    label = stringResource(Res.string.three_fuck_status_label),
                    value = stringResource(Res.string.three_fuck_status_value),
                    valueColor = ScoreMintText
                )
            }
        }
    }
}

@Composable
private fun SummaryKeyValueRow(
    label: String,
    value: String,
    valueColor: Color = ScoreTextDark,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            color = ScoreMutedText,
            modifier = Modifier
                .weight(0.42f, fill = false)
                .padding(end = 8.dp)
        )
        Text(
            text = value,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.58f, fill = false)
        )
    }
}

@Composable
private fun ConfirmModalShell(
    icon: DrawableResource,
    title: String,
    subtitle: String,
    recheckText: String,
    confirmText: String,
    onRecheck: () -> Unit,
    onConfirm: () -> Unit,
    summaryContent: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onRecheck,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .widthIn(max = 360.dp)
                .heightIn(max = 640.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ScoreSoftPinkBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = ScorePrimaryRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScoreTextDark,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = ScoreMutedText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))

                summaryContent()

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val pillShape = RoundedCornerShape(100.dp)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .clip(pillShape)
                            .clickable(role = Role.Button, onClick = onRecheck),
                        shape = pillShape,
                        color = ScoreNeutralBtnBg
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = recheckText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ScoreTextDark,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .clip(pillShape)
                            .clickable(role = Role.Button, onClick = onConfirm),
                        shape = pillShape,
                        color = ScorePrimaryRed,
                        elevation = 4.dp
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = confirmText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Icon(
                                painter = painterResource(Res.drawable.ic_check_small),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
