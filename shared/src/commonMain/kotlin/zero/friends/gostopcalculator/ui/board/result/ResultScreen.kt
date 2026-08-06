package zero.friends.gostopcalculator.ui.board.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.calculate_history_text
import zero.friends.gostopcalculator.shared.generated.resources.detail_text
import zero.friends.gostopcalculator.shared.generated.resources.formula
import zero.friends.gostopcalculator.shared.generated.resources.player_with_condition
import zero.friends.gostopcalculator.shared.generated.resources.result
import zero.friends.gostopcalculator.shared.generated.resources.share
import zero.friends.gostopcalculator.theme.DustyGray
import zero.friends.gostopcalculator.theme.Gallery
import zero.friends.gostopcalculator.theme.ManualBackground
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.ContentsCard
import zero.friends.gostopcalculator.util.getMoneyColor
import zero.friends.gostopcalculator.util.separateComma

fun Modifier.drawGraphicsLayer(graphicsLayer: GraphicsLayer): Modifier =
    this.drawWithContent {
        graphicsLayer.record {
            this@drawWithContent.drawContent()
        }
        drawContent()
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

    Scaffold(
        topBar = {
            CenterTextTopBar(
                text = uiState.gameName,
                isRed = true,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawGraphicsLayer(graphicsLayer)
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val isCalculateMode = when (uiState.screenType) {
            ScreenType.CALCULATE, ScreenType.Calculate -> true
            else -> false
        }

        val subTitleRes = if (isCalculateMode) {
            Res.string.calculate_history_text
        } else {
            Res.string.detail_text
        }

        Text(
            text = stringResource(subTitleRes),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Nero,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        uiState.gamers.forEachIndexed { index, gamer ->
            GamerResultCard(index = index, gamer = gamer, showOptions = !isCalculateMode)
            Spacer(modifier = Modifier.height(12.dp))
        }

        val trace = uiState.roundTrace
        if (!isCalculateMode && trace != null && roundTraceFormatter != null) {
            val winnerTerm = trace.terms.find { it.gamerId == trace.winnerId }
            val sellerTerms = trace.terms.filter { it.factors.isSeller && it.gamerId != trace.winnerId }
            val loserTerms = trace.terms.filter { it.gamerId != trace.winnerId && !it.factors.isSeller }

            if (winnerTerm != null) {
                val winnerInfo = uiState.gamers.find { it.id == trace.winnerId }
                val winnerName = winnerInfo?.name ?: "승자"
                val winnerLines = roundTraceFormatter.toWinnerBreakdown(winnerTerm)
                WinnerTraceCard(winnerName = winnerName, lines = winnerLines)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (sellerTerms.isNotEmpty()) {
                val sellerLines = roundTraceFormatter.toLines(sellerTerms)
                TraceCard(title = "광팔기 내역", lines = sellerLines)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (loserTerms.isNotEmpty()) {
                val loserLines = roundTraceFormatter.toLines(loserTerms)
                TraceCard(title = "패자 내역", lines = loserLines)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun GamerResultCard(index: Int, gamer: Gamer, showOptions: Boolean) {
    ContentsCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (index + 1).toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Nero,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = gamer.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Nero
                    )
                }

                val account = gamer.account
                val sign = if (account >= 0) "+" else ""
                Text(
                    text = "$sign${account.separateComma()}원",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = account.getMoneyColor()
                )
            }

            if (showOptions) {
                val options = remember(gamer) {
                    buildList {
                        gamer.winnerOption?.korean?.let { add(it) }
                        gamer.loserOption.forEach { add(it.korean) }
                        gamer.scoreOption.forEach { add(it.korean) }
                        gamer.sellerOption?.korean?.let { add(it) }
                    }
                }

                if (options.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = options.joinToString(" / "),
                        fontSize = 12.sp,
                        color = DustyGray,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }

            val validCalculates = remember(gamer.calculate) {
                gamer.calculate.filter { it.account != 0 }
            }

            if (validCalculates.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Gallery)
                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.padding(horizontal = 6.dp)) {
                    validCalculates.forEach { target ->
                        val action = if (target.account > 0) "받아야합니다." else "줘야합니다."
                        val amountText = "${kotlin.math.abs(target.account).separateComma()}원"
                        val color = target.account.getMoneyColor()

                        val annotatedString = buildAnnotatedString {
                            append("${target.name}에게 ")
                            pushStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold))
                            append(amountText)
                            pop()
                            append(" 을 $action")
                        }

                        Text(
                            text = annotatedString,
                            fontSize = 14.sp,
                            color = Nero,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WinnerTraceCard(winnerName: String, lines: List<RoundTraceFormatter.WinnerTraceLine>) {
    ContentsCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "승자 ($winnerName)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Nero,
                modifier = Modifier.padding(bottom = 8.dp)
            )
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
                        fontWeight = FontWeight.Bold,
                        color = Nero
                    )
                    Text(
                        text = line.amount,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TraceCard(title: String, lines: List<RoundTraceFormatter.TraceLine>) {
    ContentsCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Nero,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TraceTableHeader()

            lines.forEach { line ->
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
            .background(ManualBackground)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.player_with_condition),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Nero,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(Res.string.formula),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Nero,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = stringResource(Res.string.result),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Nero,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun TraceTableRow(line: RoundTraceFormatter.TraceLine) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = line.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Nero,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = line.formula,
            fontSize = 12.sp,
            color = DustyGray,
            modifier = Modifier
                .weight(2f)
                .padding(end = 6.dp)
        )
        Text(
            text = line.amount,
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
    }
}
