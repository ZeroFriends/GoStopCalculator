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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.TraceTag
import zero.friends.gostopcalculator.navigation.LocalNativeTopBarHeight
import zero.friends.gostopcalculator.navigation.LocalUseNativeNavigation
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.calculate_history_text
import zero.friends.gostopcalculator.shared.generated.resources.detail_text
import zero.friends.gostopcalculator.shared.generated.resources.formula
import zero.friends.gostopcalculator.shared.generated.resources.ic_check_small
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_calculate
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_trophy
import zero.friends.gostopcalculator.shared.generated.resources.player_with_condition
import zero.friends.gostopcalculator.shared.generated.resources.result
import zero.friends.gostopcalculator.shared.generated.resources.result_audit_balance_label
import zero.friends.gostopcalculator.shared.generated.resources.result_audit_title
import zero.friends.gostopcalculator.shared.generated.resources.result_audit_total_gain
import zero.friends.gostopcalculator.shared.generated.resources.result_audit_total_loss
import zero.friends.gostopcalculator.shared.generated.resources.result_badge_even
import zero.friends.gostopcalculator.shared.generated.resources.result_badge_net_gain
import zero.friends.gostopcalculator.shared.generated.resources.result_badge_net_loss
import zero.friends.gostopcalculator.shared.generated.resources.result_badge_transfer_guide
import zero.friends.gostopcalculator.shared.generated.resources.result_detail_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.result_no_transfer
import zero.friends.gostopcalculator.shared.generated.resources.result_player_count_badge_format
import zero.friends.gostopcalculator.shared.generated.resources.result_section_players
import zero.friends.gostopcalculator.shared.generated.resources.result_settlement_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.result_transfer_receive_badge
import zero.friends.gostopcalculator.shared.generated.resources.result_transfer_send_badge
import zero.friends.gostopcalculator.shared.generated.resources.result_zero_sum_verified
import zero.friends.gostopcalculator.shared.generated.resources.rule_winner
import zero.friends.gostopcalculator.shared.generated.resources.share
import zero.friends.gostopcalculator.shared.generated.resources.trace_go_bak_exempt_tag
import zero.friends.gostopcalculator.shared.generated.resources.trace_loser_history
import zero.friends.gostopcalculator.shared.generated.resources.trace_selling_history
import zero.friends.gostopcalculator.shared.generated.resources.winner_with_name
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
import zero.friends.gostopcalculator.ui.board.score.common.ScoreRuleCardBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreRuleCardBorder
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
    BackHandler(enabled = true) {
        onBackClick()
    }

    val graphicsLayer = rememberGraphicsLayer()
    val useNativeNavigation = LocalUseNativeNavigation.current
    val topBarPadding = if (useNativeNavigation) LocalNativeTopBarHeight.current else 0.dp

    Scaffold(
        backgroundColor = ScoreCanvasBg,
        topBar = {
            CenterTextTopBar(
                text = uiState.gameName,
                isRed = false,
                isRedBackground = false,
                onBack = onBackClick,
                onAction = { onShareClick(graphicsLayer) },
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

    val titleRes = if (isCalculateMode) {
        Res.string.calculate_history_text
    } else {
        Res.string.detail_text
    }
    val subtitleRes = if (isCalculateMode) {
        Res.string.result_settlement_subtitle
    } else {
        Res.string.result_detail_subtitle
    }
    val heroIcon = if (isCalculateMode) {
        Res.drawable.ic_hero_calculate
    } else {
        Res.drawable.ic_hero_trophy
    }

    val netSum = remember(uiState.gamers) { uiState.gamers.sumOf { it.account.toLong() } }
    val trace = uiState.roundTrace
    val traceTermByGamerId = remember(trace) {
        trace?.terms?.associateBy { it.gamerId }.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResultHeroCard(
            icon = heroIcon,
            title = stringResource(titleRes),
            subtitle = stringResource(subtitleRes),
            playerCount = uiState.gamers.size,
            isZeroSumVerified = uiState.gamers.isNotEmpty() && netSum == 0L,
        )

        if (uiState.gamers.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.result_section_players),
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
                    text = stringResource(Res.string.result_badge_transfer_guide),
                    backgroundColor = Color.White,
                    textColor = ScoreMutedText,
                    borderColor = ScoreBorderNeutral,
                    fontSize = 11.sp,
                    horizontalPadding = 10.dp,
                    verticalPadding = 4.dp
                )
            }
        }

        uiState.gamers.forEachIndexed { index, gamer ->
            val traceTags = traceTermByGamerId[gamer.id]?.traceTags.orEmpty()
            GamerResultCard(
                index = index,
                gamer = gamer,
                showOptions = !isCalculateMode,
                isGoBakExempt = !isCalculateMode && traceTags.contains(TraceTag.GO_BAK_EXEMPT),
            )
        }

        if (!isCalculateMode && trace != null && roundTraceFormatter != null) {
            val traceTexts = rememberTraceTexts()
            val winnerTerm = remember(trace) {
                trace.terms.find { it.gamerId == trace.winnerId }
            }
            val winnerLines = remember(winnerTerm, traceTexts, roundTraceFormatter) {
                winnerTerm?.let { roundTraceFormatter.toWinnerBreakdown(it, traceTexts) }.orEmpty()
            }
            val sellerLines = remember(trace, traceTexts, roundTraceFormatter) {
                val sellerTerms = trace.terms.filter { it.factors.isSeller && it.gamerId != trace.winnerId }
                if (sellerTerms.isNotEmpty()) roundTraceFormatter.toLines(sellerTerms, traceTexts) else emptyList()
            }
            val loserLines = remember(trace, traceTexts, roundTraceFormatter) {
                val loserTerms = trace.terms.filter { it.gamerId != trace.winnerId && !it.factors.isSeller }
                if (loserTerms.isNotEmpty()) roundTraceFormatter.toLines(loserTerms, traceTexts) else emptyList()
            }

            if (winnerTerm != null) {
                val winnerInfo = uiState.gamers.find { it.id == trace.winnerId }
                val winnerName = winnerInfo?.name ?: stringResource(Res.string.rule_winner)
                WinnerTraceCard(winnerName = winnerName, lines = winnerLines)
            }

            if (sellerLines.isNotEmpty()) {
                TraceCard(
                    title = stringResource(Res.string.trace_selling_history),
                    lines = sellerLines
                )
            }

            if (loserLines.isNotEmpty()) {
                TraceCard(
                    title = stringResource(Res.string.trace_loser_history),
                    lines = loserLines
                )
            }
        }

        if (uiState.gamers.isNotEmpty()) {
            ZeroSumAuditCard(gamers = uiState.gamers)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResultHeroCard(
    icon: DrawableResource,
    title: String,
    subtitle: String,
    playerCount: Int,
    isZeroSumVerified: Boolean,
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
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = ScoreSummaryCardBg,
                    border = BorderStroke(1.dp, ScoreCardBorder)
                ) {
                    FlowRow(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.result_player_count_badge_format, playerCount),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ScoreTextDark
                        )
                        if (isZeroSumVerified) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_check_small),
                                    contentDescription = null,
                                    tint = ScoreMintText,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = stringResource(Res.string.result_zero_sum_verified),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ScoreMintText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GamerResultCard(
    index: Int,
    gamer: Gamer,
    showOptions: Boolean,
    isGoBakExempt: Boolean,
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
                        color = if (account == 0 && validCalculates.isEmpty() && !showOptions) {
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
                    val sign = when {
                        account > 0 -> "+"
                        else -> ""
                    }
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

            if (showOptions) {
                val optionResources = remember(gamer) {
                    buildList {
                        gamer.winnerOption?.let { add(it.toRule().stringRes()) }
                        gamer.loserOption.forEach { add(it.toRule().stringRes()) }
                        gamer.scoreOption.forEach { add(it.toRule().stringRes()) }
                        gamer.sellerOption?.let { add(it.toRule().stringRes()) }
                    }
                }
                val options = optionResources.map { stringResource(it) }

                if (options.isNotEmpty() || isGoBakExempt) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        options.forEach { optText ->
                            StatusPillBadge(
                                text = optText,
                                backgroundColor = ScoreSummaryCardBg,
                                textColor = ScoreMutedText,
                                borderColor = ScoreBorderNeutral,
                                fontSize = 11.sp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 3.dp
                            )
                        }
                        if (isGoBakExempt) {
                            StatusPillBadge(
                                text = stringResource(Res.string.trace_go_bak_exempt_tag),
                                backgroundColor = ScoreMintBg,
                                textColor = ScoreMintText,
                                fontSize = 11.sp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 3.dp
                            )
                        }
                    }
                }
            }

            if (validCalculates.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = ScoreCardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

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
        }
    }
}

@Composable
private fun WinnerTraceCard(winnerName: String, lines: List<RoundTraceFormatter.WinnerTraceLine>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ScoreCardBorder),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(Res.string.winner_with_name, winnerName),
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = ScoreTextDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Divider(color = ScoreCardBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(6.dp))
            lines.forEach { line ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = line.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ScoreMutedText,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = line.amount,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TraceCard(title: String, lines: List<RoundTraceFormatter.TraceLine>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ScoreCardBorder),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = ScoreTextDark,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            TraceTableHeader()

            lines.forEachIndexed { index, line ->
                if (index > 0) {
                    Divider(color = ScoreCardBorder, thickness = 1.dp)
                }
                TraceTableRow(line = line)
            }
        }
    }
}

@Composable
private fun TraceTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ScoreSummaryCardBg)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.player_with_condition),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = ScoreMutedText,
            modifier = Modifier
                .weight(1.0f)
                .padding(end = 6.dp)
        )
        Text(
            text = stringResource(Res.string.formula),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = ScoreMutedText,
            modifier = Modifier
                .weight(1.65f)
                .padding(end = 6.dp)
        )
        Text(
            text = stringResource(Res.string.result),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = ScoreMutedText,
            textAlign = TextAlign.End,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.weight(1.35f)
        )
    }
}

@Composable
private fun TraceTableRow(line: RoundTraceFormatter.TraceLine) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = line.title,
            fontSize = 12.5.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Bold,
            color = ScoreTextDark,
            modifier = Modifier
                .weight(1.0f)
                .padding(end = 6.dp)
        )
        Text(
            text = line.formula,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = ScoreMutedText,
            modifier = Modifier
                .weight(1.65f)
                .padding(end = 6.dp)
        )
        Text(
            text = line.amount,
            fontSize = 12.sp,
            textAlign = TextAlign.End,
            maxLines = 2,
            softWrap = true,
            modifier = Modifier.weight(1.35f)
        )
    }
}

@Composable
private fun ZeroSumAuditCard(gamers: List<Gamer>) {
    val won = stringResource(Res.string.won)
    val totalGain = remember(gamers) {
        gamers.filter { it.account > 0 }.sumOf { it.account.toLong() }
    }
    val totalLoss = remember(gamers) {
        gamers.filter { it.account < 0 }.sumOf { abs(it.account.toLong()) }
    }
    val discrepancy = totalGain - totalLoss

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ScoreRuleCardBg,
        border = BorderStroke(1.dp, ScoreRuleCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(Res.string.result_audit_title),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = ScoreTextDark
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.result_audit_total_gain),
                    fontSize = 12.sp,
                    color = ScoreMutedText,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Text(
                    text = "+${totalGain.separateComma()}$won",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScorePrimaryRed
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.result_audit_total_loss),
                    fontSize = 12.sp,
                    color = ScoreMutedText,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Text(
                    text = "-${totalLoss.separateComma()}$won",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue
                )
            }
            Divider(color = ScoreRuleCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.result_audit_balance_label),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ScoreTextDark,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Text(
                    text = if (discrepancy == 0L) {
                        stringResource(Res.string.result_zero_sum_verified)
                    } else {
                        "${discrepancy.separateComma()}$won"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (discrepancy == 0L) ScoreMintText else ScorePrimaryRed
                )
            }
        }
    }
}
