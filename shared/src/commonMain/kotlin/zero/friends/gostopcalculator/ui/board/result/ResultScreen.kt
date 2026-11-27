package zero.friends.gostopcalculator.ui.board.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.Target
import zero.friends.domain.model.TraceTag
import zero.friends.domain.usecase.calculate.calculateShakeBombMultiplier
import zero.friends.domain.usecase.calculate.effectiveBakOptions
import zero.friends.domain.usecase.calculate.isGoBakSoloCoverageMode
import zero.friends.gostopcalculator.navigation.LocalNativeTopBarHeight
import zero.friends.gostopcalculator.navigation.LocalUseNativeNavigation
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.calculate_history_text
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_calculate
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_trophy
import zero.friends.gostopcalculator.shared.generated.resources.result_badge_even
import zero.friends.gostopcalculator.shared.generated.resources.result_badge_net_gain
import zero.friends.gostopcalculator.shared.generated.resources.result_badge_net_loss
import zero.friends.gostopcalculator.shared.generated.resources.result_badge_transfer_guide
import zero.friends.gostopcalculator.shared.generated.resources.result_base_pot_badge
import zero.friends.gostopcalculator.shared.generated.resources.result_breakdown_header
import zero.friends.gostopcalculator.shared.generated.resources.result_breakdown_winner_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.result_chip_bak_x2_format
import zero.friends.gostopcalculator.shared.generated.resources.result_chip_go_format
import zero.friends.gostopcalculator.shared.generated.resources.result_chip_gobak_solo
import zero.friends.gostopcalculator.shared.generated.resources.result_chip_special_x2_format
import zero.friends.gostopcalculator.shared.generated.resources.result_detail_badge_breakdown_transfer
import zero.friends.gostopcalculator.shared.generated.resources.result_detail_section_players
import zero.friends.gostopcalculator.shared.generated.resources.result_detail_title
import zero.friends.gostopcalculator.shared.generated.resources.result_no_transfer
import zero.friends.gostopcalculator.shared.generated.resources.result_player_count_badge_format
import zero.friends.gostopcalculator.shared.generated.resources.result_player_count_short_format
import zero.friends.gostopcalculator.shared.generated.resources.result_section_players
import zero.friends.gostopcalculator.shared.generated.resources.result_seller_badge_cards_format
import zero.friends.gostopcalculator.shared.generated.resources.result_settlement_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.result_transfer_receive_badge
import zero.friends.gostopcalculator.shared.generated.resources.result_transfer_send_badge
import zero.friends.gostopcalculator.shared.generated.resources.rule_bomb
import zero.friends.gostopcalculator.shared.generated.resources.rule_seller_role
import zero.friends.gostopcalculator.shared.generated.resources.rule_shake
import zero.friends.gostopcalculator.shared.generated.resources.rule_three_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_winner
import zero.friends.gostopcalculator.shared.generated.resources.share
import zero.friends.gostopcalculator.shared.generated.resources.trace_go_bak_exempt_tag
import zero.friends.gostopcalculator.shared.generated.resources.winner_multiplier_count_format
import zero.friends.gostopcalculator.shared.generated.resources.won
import zero.friends.gostopcalculator.theme.Blue
import zero.friends.gostopcalculator.ui.board.score.common.PlayerIndexBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreBorderNeutral
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCanvasBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBadgeBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSummaryCardBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.util.getMoneyColor
import zero.friends.gostopcalculator.util.separateComma
import zero.friends.gostopcalculator.util.stringRes
import zero.friends.gostopcalculator.util.toRule
import kotlin.math.abs

private val ResultSoftBlueBg = Color(0xFFEFF6FF)
private val ResultSoftBlueBorder = Color(0xFFD6E8FF)
private val ResultGoBakRed = Color(0xFFDC2626)
private val ResultGoBakRowBg = Color(0xFFFFF5F5)
private val ResultWinnerInsetBg = Color(0xFFFFF8F8)
private val ResultWinnerInsetBorder = Color(0xFFFFE4E4)
private val ResultExemptInsetBg = Color(0xFFF3FBFA)
private val ResultExemptInsetBorder = Color(0xFFD1ECE9)
private val ResultAmberAccent = Color(0xFF9A3412)
private val ResultAmberCardBg = Color(0xFFFFFDF5)
private val ResultAmberBadgeBg = Color(0xFFFEF3C7)
private val ResultAmberBorder = Color(0xFFFDE68A)

fun Modifier.drawGraphicsLayer(graphicsLayer: GraphicsLayer): Modifier =
    this.drawWithContent {
        graphicsLayer.record {
            this@drawWithContent.drawContent()
        }
        drawLayer(graphicsLayer)
    }

@Composable
fun ResultScreen(
    uiState: ResultUiState,
    roundTraceFormatter: RoundTraceFormatter? = null,
    onBackClick: () -> Unit = {},
    onShareClick: (GraphicsLayer) -> Unit = {}
) {
    val stableOnBack = remember(onBackClick) { onBackClick }
    BackHandler(enabled = true) {
        stableOnBack()
    }

    val graphicsLayer = rememberGraphicsLayer()
    val stableOnShare = remember(graphicsLayer, onShareClick) {
        { onShareClick(graphicsLayer) }
    }
    val useNativeNavigation = LocalUseNativeNavigation.current
    val topBarPadding = if (useNativeNavigation) LocalNativeTopBarHeight.current else 0.dp

    Scaffold(
        backgroundColor = ScoreCanvasBg,
        topBar = {
            CenterTextTopBar(
                text = uiState.gameName,
                isRed = false,
                isRedBackground = false,
                onBack = stableOnBack,
                onAction = stableOnShare,
                actionText = stringResource(Res.string.share)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (topBarPadding > 0.dp) {
                Spacer(modifier = Modifier.height(topBarPadding))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawGraphicsLayer(graphicsLayer)
                    .background(ScoreCanvasBg)
            ) {
                ResultContent(
                    uiState = uiState,
                    roundTraceFormatter = roundTraceFormatter
                )
            }
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun ResultContent(
    uiState: ResultUiState,
    roundTraceFormatter: RoundTraceFormatter?
) {
    val isCalculateMode = when (uiState.screenType) {
        ScreenType.CALCULATE, ScreenType.Calculate -> true
        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isCalculateMode) {
            ResultHeroCard(
                icon = Res.drawable.ic_hero_calculate,
                title = stringResource(Res.string.calculate_history_text),
                subtitle = stringResource(Res.string.result_settlement_subtitle),
                playerCount = uiState.gamers.size,
            )

            if (uiState.gamers.isNotEmpty()) {
                ResultSectionHeader(
                    title = stringResource(Res.string.result_section_players),
                    badgeText = stringResource(Res.string.result_badge_transfer_guide),
                )
            }

            uiState.gamers.forEachIndexed { index, gamer ->
                val stableKey = when {
                    gamer.playerId != 0L -> "player:${gamer.playerId}"
                    gamer.id != 0L -> "gamer:${gamer.id}"
                    else -> "idx:$index:${gamer.name}"
                }
                key(stableKey) {
                    GamerResultCard(
                        index = index,
                        gamer = gamer,
                    )
                }
            }
        } else {
            val trace = uiState.roundTrace
            val traceTexts = rememberTraceTexts()
            val fallbackFormatter = remember { RoundTraceFormatter() }
            val formatter = roundTraceFormatter ?: fallbackFormatter
            val potSummary = remember(trace, traceTexts, formatter) {
                trace?.let { formatter.toRoundBasePotSummary(it, traceTexts) }
            }
            val traceTermByGamerId = remember(trace) {
                trace?.terms?.associateBy { it.gamerId }.orEmpty()
            }
            val winnerId = remember(trace, uiState.gamers) {
                trace?.winnerId
                    ?: uiState.gamers.firstOrNull { it.winnerOption != null }?.id
                    ?: uiState.gamers.firstOrNull { ScoreOption.ThreeFuck in it.scoreOption }?.id
            }
            val loserCount = remember(uiState.gamers, winnerId, traceTermByGamerId) {
                uiState.gamers.count { gamer ->
                    val isSeller = traceTermByGamerId[gamer.id]?.factors?.isSeller == true ||
                        gamer.sellerOption != null
                    gamer.id != winnerId && !isSeller
                }
            }
            val hasGoBakPayer = remember(uiState.gamers, winnerId, traceTermByGamerId) {
                uiState.gamers.any { gamer ->
                    val isSeller = traceTermByGamerId[gamer.id]?.factors?.isSeller == true ||
                        gamer.sellerOption != null
                    gamer.id != winnerId && !isSeller && LoserOption.GoBak in gamer.loserOption
                }
            }

            DetailHeroCard(
                playerCount = uiState.gamers.size,
                potSummary = potSummary,
            )

            if (uiState.gamers.isNotEmpty()) {
                ResultSectionHeader(
                    title = stringResource(Res.string.result_detail_section_players),
                    badgeText = if (trace != null) {
                        stringResource(Res.string.result_detail_badge_breakdown_transfer)
                    } else {
                        stringResource(Res.string.result_badge_transfer_guide)
                    },
                )
            }

            uiState.gamers.forEachIndexed { index, gamer ->
                val detailKey = if (gamer.id != 0L) "gamer:${gamer.id}" else "idx:$index:${gamer.name}"
                key(detailKey) {
                    val term = traceTermByGamerId[gamer.id]
                    val isWinner = (trace != null && gamer.id == trace.winnerId) ||
                        (term != null && TraceTag.WINNER in term.traceTags) ||
                        (trace == null && gamer.id == winnerId)
                    val isSeller = !isWinner &&
                        ((term?.factors?.isSeller == true) || gamer.sellerOption != null)
                    val isSoloGoBakCoverage = term?.factors?.let {
                        it.isSoloGoBakMode || it.goBakCoveredAmount != 0 || it.coveredLosers.isNotEmpty()
                    } ?: isGoBakSoloCoverageMode(loserCount)
                    val includeLegacyDoubleGoBak = term?.factors?.let {
                        !it.isSoloGoBakMode && it.goBakCoveredAmount == 0 && it.coveredLosers.isEmpty()
                    } ?: !isGoBakSoloCoverageMode(loserCount)
                    val isGoBakExempt = when {
                        term != null -> {
                            TraceTag.GO_BAK_EXEMPT in term.traceTags || term.factors.goBakExemptedAmount != 0
                        }
                        else -> !isWinner && !isSeller && isSoloGoBakCoverage && hasGoBakPayer &&
                            LoserOption.GoBak !in gamer.loserOption
                    }

                    val sellerSummary = remember(isSeller, term, gamer, traceTexts, formatter) {
                        if (isSeller) formatter.toDetailSellerSummary(term, gamer, traceTexts) else null
                    }
                    val breakdownRows = remember(term, gamer, trace, traceTexts, formatter, uiState.gamers) {
                        term?.let {
                            formatter.toDetailCardBreakdown(it, gamer, trace, traceTexts, uiState.gamers)
                        }.orEmpty()
                    }

                    DetailGamerCard(
                        index = index,
                        gamer = gamer,
                        isWinner = isWinner,
                        isSeller = isSeller,
                        isGoBakExempt = isGoBakExempt,
                        isSoloGoBakCoverage = isSoloGoBakCoverage,
                        includeLegacyDoubleGoBak = includeLegacyDoubleGoBak,
                        sellerSummary = sellerSummary,
                        breakdownRows = breakdownRows,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultSectionHeader(
    title: String,
    badgeText: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Bold,
            color = ScoreTextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        StatusPillBadge(
            text = badgeText,
            backgroundColor = Color.White,
            textColor = ScoreMutedText,
            borderColor = ScoreBorderNeutral,
            fontSize = 11.sp,
            horizontalPadding = 10.dp,
            verticalPadding = 4.dp
        )
    }
}

@Composable
private fun ResultHeroCard(
    icon: DrawableResource,
    title: String,
    subtitle: String,
    playerCount: Int,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ScoreCardBorder),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
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
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = ScoreTextDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = ScoreMutedText,
                textAlign = TextAlign.Center
            )
            if (playerCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                StatusPillBadge(
                    text = stringResource(Res.string.result_player_count_badge_format, playerCount),
                    backgroundColor = ScoreSummaryCardBg,
                    textColor = ScoreTextDark,
                    borderColor = ScoreCardBorder,
                    fontSize = 11.5.sp,
                    horizontalPadding = 12.dp,
                    verticalPadding = 6.dp
                )
            }
        }
    }
}

@Composable
private fun DetailHeroCard(
    playerCount: Int,
    potSummary: RoundTraceFormatter.RoundBasePotSummary?,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ScoreCardBorder),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ScoreSoftPinkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_hero_trophy),
                            contentDescription = null,
                            tint = ScorePrimaryRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(Res.string.result_detail_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScoreTextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (playerCount > 0) {
                    StatusPillBadge(
                        text = stringResource(Res.string.result_player_count_short_format, playerCount),
                        backgroundColor = ScoreSummaryCardBg,
                        textColor = ScoreMutedText,
                        borderColor = ScoreCardBorder,
                        fontSize = 11.5.sp,
                        horizontalPadding = 10.dp,
                        verticalPadding = 5.dp
                    )
                }
            }

            if (potSummary != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = ScoreSoftPinkBg,
                    border = BorderStroke(1.dp, ScoreSoftPinkBorder)
                ) {
                    val useStackedLayout = !potSummary.isThreeFuck &&
                        potSummary.potAmountText.isNotEmpty() &&
                        potSummary.formulaText.length > 26
                    if (useStackedLayout) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusPillBadge(
                                    text = stringResource(Res.string.result_base_pot_badge),
                                    backgroundColor = ScorePrimaryRed,
                                    textColor = Color.White,
                                    fontSize = 10.5.sp,
                                    horizontalPadding = 8.dp,
                                    verticalPadding = 3.dp
                                )
                                Text(
                                    text = potSummary.potAmountText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ScoreTextDark,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                            Text(
                                text = potSummary.formulaText,
                                fontSize = 12.5.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Medium,
                                color = ScoreTextDark,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = if (potSummary.potAmountText.isNotEmpty()) 8.dp else 0.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatusPillBadge(
                                    text = if (potSummary.isThreeFuck) {
                                        stringResource(Res.string.rule_three_fuck)
                                    } else {
                                        stringResource(Res.string.result_base_pot_badge)
                                    },
                                    backgroundColor = ScorePrimaryRed,
                                    textColor = Color.White,
                                    fontSize = 10.5.sp,
                                    horizontalPadding = 8.dp,
                                    verticalPadding = 3.dp
                                )
                                Text(
                                    text = potSummary.formulaText,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ScoreTextDark,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                            if (potSummary.potAmountText.isNotEmpty()) {
                                Text(
                                    text = potSummary.potAmountText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ScoreTextDark,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GamerResultCard(
    index: Int,
    gamer: Gamer,
) {
    val won = stringResource(Res.string.won)
    val account = gamer.account

    val (badgeBg, badgeText) = when {
        account > 0 -> ScoreSoftPinkBg to ScorePrimaryRed
        account < 0 -> ResultSoftBlueBg to Blue
        else -> ScoreDisabledBadgeBg to ScoreSubtleGray
    }
    val statusText = when {
        account > 0 -> stringResource(Res.string.result_badge_net_gain)
        account < 0 -> stringResource(Res.string.result_badge_net_loss)
        else -> stringResource(Res.string.result_badge_even)
    }

    val validCalculates = remember(gamer.calculate) {
        gamer.calculate.filter { it.account != 0 }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ScoreCardBorder),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerIndexBadge(
                        index = index,
                        containerColor = badgeBg,
                        textColor = badgeText,
                        size = 28.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = gamer.name,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (account == 0 && validCalculates.isEmpty()) {
                            ScoreMutedText
                        } else {
                            ScoreTextDark
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusPillBadge(
                        text = statusText,
                        backgroundColor = badgeBg,
                        textColor = badgeText,
                        borderColor = when {
                            account > 0 -> ScoreSoftPinkBorder
                            account < 0 -> ResultSoftBlueBorder
                            else -> ScoreBorderNeutral
                        },
                        fontSize = 10.5.sp,
                        horizontalPadding = 7.dp,
                        verticalPadding = 2.dp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    val sign = if (account > 0) "+" else ""
                    Text(
                        text = "$sign${account.separateComma()}$won",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (account == 0) ScoreSubtleGray else account.getMoneyColor(),
                        maxLines = 1,
                        softWrap = false
                    )
                    if (account == 0 && validCalculates.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.result_no_transfer),
                            fontSize = 10.5.sp,
                            color = ScoreSubtleGray
                        )
                    }
                }
            }

            if (validCalculates.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = ScoreCardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))
                TransferRowsList(validCalculates = validCalculates, won = won)
            }
        }
    }
}

private data class DetailChipSpec(
    val text: String,
    val backgroundColor: Color,
    val textColor: Color,
    val borderColor: Color? = null,
    val contentDescription: String? = null,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailGamerCard(
    index: Int,
    gamer: Gamer,
    isWinner: Boolean,
    isSeller: Boolean,
    isGoBakExempt: Boolean,
    isSoloGoBakCoverage: Boolean,
    includeLegacyDoubleGoBak: Boolean,
    sellerSummary: RoundTraceFormatter.DetailSellerSummary?,
    breakdownRows: List<RoundTraceFormatter.DetailBreakdownRow>,
) {
    val won = stringResource(Res.string.won)
    val account = gamer.account
    val validCalculates = remember(gamer.calculate) {
        gamer.calculate.filter { it.account != 0 }
    }

    val hasUniformSellerTransfersOnly = sellerSummary != null &&
        breakdownRows.isEmpty() &&
        validCalculates.isNotEmpty() &&
        validCalculates.all { it.account > 0 && it.account == validCalculates.first().account }
    val showTransfers = validCalculates.isNotEmpty() && !hasUniformSellerTransfersOnly

    val cardColor = if (isSeller) ResultAmberCardBg else Color.White
    val cardBorder = when {
        isWinner -> BorderStroke(1.5.dp, ScorePrimaryRed)
        isSeller -> BorderStroke(1.dp, ResultAmberBorder)
        else -> BorderStroke(1.dp, ScoreCardBorder)
    }
    val (indexBg, indexText) = when {
        isWinner -> ScorePrimaryRed to Color.White
        isSeller -> ResultAmberBadgeBg to ResultAmberAccent
        account > 0 -> ScoreSoftPinkBg to ScorePrimaryRed
        account < 0 -> ResultSoftBlueBg to Blue
        else -> ScoreDisabledBadgeBg to ScoreSubtleGray
    }
    val amountColor = when {
        account == 0 -> ScoreSubtleGray
        isSeller && account > 0 -> ResultAmberAccent
        else -> account.getMoneyColor()
    }

    val conditionChips = buildDetailChips(
        gamer = gamer,
        isWinner = isWinner,
        isSeller = isSeller,
        isGoBakExempt = isGoBakExempt,
        isSoloGoBakCoverage = isSoloGoBakCoverage,
        includeLegacyDoubleGoBak = includeLegacyDoubleGoBak,
    )
    val showInlineExemptOnRow1 = isGoBakExempt && !isWinner && !isSeller && conditionChips.isEmpty()
    val chipsForRow2 = if (showInlineExemptOnRow1 || !isGoBakExempt) {
        conditionChips
    } else {
        conditionChips + DetailChipSpec(
            text = stringResource(Res.string.trace_go_bak_exempt_tag),
            backgroundColor = ScoreMintBg,
            textColor = ScoreMintText,
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = cardColor,
        border = cardBorder,
        elevation = if (isWinner) 3.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerIndexBadge(
                        index = index,
                        containerColor = indexBg,
                        textColor = indexText,
                        size = 28.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = gamer.name,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isWinner && !isSeller && account == 0 && validCalculates.isEmpty()) {
                            ScoreMutedText
                        } else {
                            ScoreTextDark
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    val winnerRoleText = stringResource(Res.string.rule_winner)
                    when {
                        isWinner -> {
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusPillBadge(
                                text = "🏆 $winnerRoleText",
                                backgroundColor = ScorePrimaryRed,
                                textColor = Color.White,
                                modifier = Modifier.clearAndSetSemantics {
                                    contentDescription = winnerRoleText
                                },
                                fontSize = 10.5.sp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 3.dp
                            )
                        }
                        isSeller -> {
                            val fallbackSellerBadge = if (gamer.score > 0) {
                                stringResource(Res.string.result_seller_badge_cards_format, gamer.score)
                            } else {
                                stringResource(Res.string.rule_seller_role)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusPillBadge(
                                text = sellerSummary?.badgeText ?: fallbackSellerBadge,
                                backgroundColor = ResultAmberBadgeBg,
                                textColor = ResultAmberAccent,
                                borderColor = ResultAmberBorder,
                                fontSize = 10.5.sp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 3.dp
                            )
                        }
                        showInlineExemptOnRow1 -> {
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusPillBadge(
                                text = stringResource(Res.string.trace_go_bak_exempt_tag),
                                backgroundColor = ScoreMintBg,
                                textColor = ScoreMintText,
                                fontSize = 10.5.sp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 3.dp
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    val sign = if (account > 0) "+" else ""
                    Text(
                        text = "$sign${account.separateComma()}$won",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = amountColor,
                        maxLines = 1,
                        softWrap = false
                    )
                    if (account == 0 && validCalculates.isEmpty() && !isWinner && !isSeller) {
                        Text(
                            text = stringResource(Res.string.result_no_transfer),
                            fontSize = 10.5.sp,
                            color = ScoreSubtleGray
                        )
                    }
                }
            }

            if (chipsForRow2.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    chipsForRow2.forEach { chip ->
                        val chipModifier = chip.contentDescription?.let { desc ->
                            Modifier.clearAndSetSemantics { contentDescription = desc }
                        } ?: Modifier
                        StatusPillBadge(
                            text = chip.text,
                            backgroundColor = chip.backgroundColor,
                            textColor = chip.textColor,
                            modifier = chipModifier,
                            borderColor = chip.borderColor,
                            fontSize = 11.sp,
                            horizontalPadding = 8.dp,
                            verticalPadding = 3.dp
                        )
                    }
                }
            }

            if (sellerSummary != null) {
                Spacer(modifier = Modifier.height(10.dp))
                SellerBreakdownStrip(summary = sellerSummary)
            }

            if (breakdownRows.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                DetailBreakdownBox(
                    rows = breakdownRows,
                    isWinner = isWinner,
                    isGoBakExempt = isGoBakExempt,
                )
            }

            if (showTransfers) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = ScoreCardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))
                TransferRowsList(validCalculates = validCalculates, won = won)
            }
        }
    }
}

@Composable
private fun buildDetailChips(
    gamer: Gamer,
    isWinner: Boolean,
    isSeller: Boolean,
    isGoBakExempt: Boolean,
    isSoloGoBakCoverage: Boolean,
    includeLegacyDoubleGoBak: Boolean,
): List<DetailChipSpec> {
    if (isSeller) return emptyList()
    val chips = mutableListOf<DetailChipSpec>()

    if (isWinner) {
        if (gamer.go > 0) {
            chips.add(
                DetailChipSpec(
                    text = stringResource(Res.string.result_chip_go_format, gamer.go),
                    backgroundColor = ScoreSoftPinkBg,
                    textColor = ScorePrimaryRed,
                    borderColor = ScoreSoftPinkBorder,
                )
            )
        }
        if (gamer.shakeCount > 0) {
            val shakeName = stringResource(Res.string.rule_shake)
            val shakeText = if (gamer.shakeCount == 1) {
                stringResource(Res.string.result_chip_special_x2_format, shakeName)
            } else {
                stringResource(
                    Res.string.winner_multiplier_count_format,
                    shakeName,
                    gamer.shakeCount,
                    calculateShakeBombMultiplier(gamer.shakeCount, 0),
                )
            }
            chips.add(
                DetailChipSpec(
                    text = shakeText,
                    backgroundColor = ScoreSoftPinkBg,
                    textColor = ScorePrimaryRed,
                    borderColor = ScoreSoftPinkBorder,
                )
            )
        }
        if (gamer.bombCount > 0) {
            val bombName = stringResource(Res.string.rule_bomb)
            val bombText = if (gamer.bombCount == 1) {
                stringResource(Res.string.result_chip_special_x2_format, bombName)
            } else {
                stringResource(
                    Res.string.winner_multiplier_count_format,
                    bombName,
                    gamer.bombCount,
                    calculateShakeBombMultiplier(0, gamer.bombCount),
                )
            }
            chips.add(
                DetailChipSpec(
                    text = bombText,
                    backgroundColor = ScoreSoftPinkBg,
                    textColor = ScorePrimaryRed,
                    borderColor = ScoreSoftPinkBorder,
                )
            )
        }
        gamer.scoreOption.forEach { opt ->
            chips.add(
                DetailChipSpec(
                    text = stringResource(opt.toRule().stringRes()),
                    backgroundColor = ScoreSoftPinkBg,
                    textColor = ScorePrimaryRed,
                    borderColor = ScoreSoftPinkBorder,
                )
            )
        }
        return chips
    }

    val hasGoBakOption = LoserOption.GoBak in gamer.loserOption
    val bakOptions = effectiveBakOptions(
        loserOptions = gamer.loserOption,
        loserCount = if (includeLegacyDoubleGoBak) 1 else 2,
    ).sortedBy { it.ordinal }

    bakOptions.forEach { opt ->
        chips.add(
            DetailChipSpec(
                text = stringResource(
                    Res.string.result_chip_bak_x2_format,
                    stringResource(opt.toRule().stringRes()).replace(" ", "")
                ),
                backgroundColor = ResultSoftBlueBg,
                textColor = Blue,
                borderColor = ResultSoftBlueBorder,
            )
        )
    }

    if (hasGoBakOption && isSoloGoBakCoverage && !isGoBakExempt) {
        val soloText = stringResource(Res.string.result_chip_gobak_solo)
        chips.add(
            DetailChipSpec(
                text = soloText,
                backgroundColor = ScoreSoftPinkBg,
                textColor = ResultGoBakRed,
                borderColor = ScoreSoftPinkBorder,
                contentDescription = soloText.removePrefix("⚡").trim(),
            )
        )
    }

    gamer.scoreOption.forEach { opt ->
        chips.add(
            DetailChipSpec(
                text = stringResource(opt.toRule().stringRes()),
                backgroundColor = ScoreSummaryCardBg,
                textColor = ScoreMutedText,
                borderColor = ScoreBorderNeutral,
            )
        )
    }

    return chips
}

@Composable
private fun SellerBreakdownStrip(
    summary: RoundTraceFormatter.DetailSellerSummary,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ResultAmberBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = summary.formulaText,
                fontSize = 12.5.sp,
                color = ScoreMutedText,
                modifier = Modifier
                    .weight(0.46f)
                    .padding(end = 8.dp)
            )
            Text(
                text = summary.receiveEachText,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = ResultAmberAccent,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.54f)
            )
        }
    }
}

@Composable
private fun DetailBreakdownBox(
    rows: List<RoundTraceFormatter.DetailBreakdownRow>,
    isWinner: Boolean,
    isGoBakExempt: Boolean,
) {
    val bgColor = when {
        isWinner -> ResultWinnerInsetBg
        isGoBakExempt -> ResultExemptInsetBg
        else -> ScoreSummaryCardBg
    }
    val borderColor = when {
        isWinner -> ResultWinnerInsetBorder
        isGoBakExempt -> ResultExemptInsetBorder
        else -> ScoreCardBorder
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isWinner) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.result_breakdown_header),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScorePrimaryRed,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = stringResource(Res.string.result_breakdown_winner_subtitle),
                        fontSize = 11.sp,
                        color = ScoreMutedText
                    )
                }
            }

            rows.forEach { row ->
                val amountColor = when (row.tone) {
                    RoundTraceFormatter.BreakdownRowTone.WIN,
                    RoundTraceFormatter.BreakdownRowTone.BONUS_POSITIVE -> ScorePrimaryRed
                    RoundTraceFormatter.BreakdownRowTone.GO_BAK_COVER -> ResultGoBakRed
                    RoundTraceFormatter.BreakdownRowTone.EXEMPT -> ScoreMintText
                    RoundTraceFormatter.BreakdownRowTone.LOSS_DEFAULT -> {
                        if (row.amountValue < 0) Blue else ScoreSubtleGray
                    }
                }
                val isCoverRow = row.tone == RoundTraceFormatter.BreakdownRowTone.GO_BAK_COVER
                val labelColor = if (isCoverRow) ResultGoBakRed else ScoreTextDark
                val labelWeight = if (isCoverRow) FontWeight.SemiBold else FontWeight.Medium

                if (isCoverRow) {
                    val cleanLabel = row.label.removePrefix("⚡").trim()
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clearAndSetSemantics {
                                contentDescription = "$cleanLabel ${row.amountText}"
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = ResultGoBakRowBg
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = row.label,
                                fontSize = 12.5.sp,
                                lineHeight = 17.sp,
                                fontWeight = labelWeight,
                                color = labelColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            Text(
                                text = row.amountText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = amountColor,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = row.label,
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp,
                            fontWeight = labelWeight,
                            color = labelColor,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = row.amountText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = amountColor,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransferRowsList(
    validCalculates: List<Target>,
    won: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        validCalculates.forEach { target ->
            val isReceive = target.account > 0
            val actionBadgeText = if (isReceive) {
                stringResource(Res.string.result_transfer_receive_badge)
            } else {
                stringResource(Res.string.result_transfer_send_badge)
            }
            val actionBg = if (isReceive) ScoreSoftPinkBg else ResultSoftBlueBg
            val actionColor = if (isReceive) ScorePrimaryRed else Blue
            val signedAmount = if (isReceive) {
                "+${abs(target.account.toLong()).separateComma()}$won"
            } else {
                "-${abs(target.account.toLong()).separateComma()}$won"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = target.name,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = ScoreTextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    StatusPillBadge(
                        text = actionBadgeText,
                        backgroundColor = actionBg,
                        textColor = actionColor,
                        fontSize = 10.5.sp,
                        horizontalPadding = 6.dp,
                        verticalPadding = 2.dp
                    )
                }
                Text(
                    text = signedAmount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = target.account.getMoneyColor(),
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}
