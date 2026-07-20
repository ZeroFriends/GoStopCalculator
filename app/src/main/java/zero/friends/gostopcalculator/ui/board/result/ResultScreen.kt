package zero.friends.gostopcalculator.ui.board.result

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Target
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.theme.GoStopTheme
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.ContentsCard
import zero.friends.gostopcalculator.util.getMoneyColor
import kotlin.math.roundToInt

fun CharSequence.toAnnotatedString(): AnnotatedString {
    if (this !is Spanned) return AnnotatedString(this.toString())
    return buildAnnotatedString {
        append(this@toAnnotatedString.toString())
        getSpans(0, length, Any::class.java).forEach { span ->
            val start = getSpanStart(span)
            val end = getSpanEnd(span)
            when (span) {
                is ForegroundColorSpan -> {
                    addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
                }
                is StyleSpan -> {
                    when (span.style) {
                        Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                        Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                        Typeface.BOLD_ITALIC -> addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                            start,
                            end
                        )
                    }
                }
            }
        }
    }
}

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
    val graphicsLayer = rememberGraphicsLayer()

    Scaffold(
        topBar = {
            CenterTextTopBar(
                text = uiState.gameName,
                isRed = false,
                onBack = onBackClick,
                onAction = { onShareClick(graphicsLayer) },
                actionText = stringResource(id = R.string.share)
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
                    .background(colorResource(id = R.color.white))
            ) {
                ResultContent(
                    uiState = uiState,
                    roundTraceFormatter = roundTraceFormatter
                )
            }
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
        val subTitleRes = when (uiState.screenType) {
            ResultActivity.ScreenType.CALCULATE -> R.string.calculate_history_text
            ResultActivity.ScreenType.DETAIL -> R.string.detail_text
        }
        Text(
            text = stringResource(id = subTitleRes),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.nero),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        uiState.gamers.forEachIndexed { index, gamer ->
            GamerResultCard(index = index, gamer = gamer)
            Spacer(modifier = Modifier.height(12.dp))
        }

        val trace = uiState.roundTrace
        if (uiState.screenType == ResultActivity.ScreenType.DETAIL && trace != null && roundTraceFormatter != null) {
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
private fun GamerResultCard(index: Int, gamer: Gamer) {
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
                        color = colorResource(id = R.color.nero),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = gamer.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.nero)
                    )
                }

                val account = gamer.account
                val sign = if (account >= 0) "+" else ""
                Text(
                    text = String.format("%s%,d원", sign, account),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = account.getMoneyColor())
                )
            }

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
                    color = colorResource(id = R.color.dusty_gray),
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }

            if (gamer.calculate.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = colorResource(id = R.color.gallery))
                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.padding(horizontal = 6.dp)) {
                    gamer.calculate.forEach { target ->
                        val action = if (target.account > 0) "받아야합니다." else "줘야합니다."
                        val amountText = String.format("%,d원", target.account)
                        val color = colorResource(id = target.account.getMoneyColor())

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
                            color = colorResource(id = R.color.nero),
                            modifier = Modifier.padding(vertical = 4.dp)
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
                color = colorResource(id = R.color.nero),
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
                        color = colorResource(id = R.color.nero)
                    )
                    Text(
                        text = line.amount.toAnnotatedString(),
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
                color = colorResource(id = R.color.nero),
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
            .background(colorResource(id = R.color.manual_background))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.player_with_condition),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.nero),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(id = R.string.formula),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.nero),
            modifier = Modifier.weight(2f)
        )
        Text(
            text = stringResource(id = R.string.result),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.nero),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun TraceTableRow(line: RoundTraceFormatter.TraceLine) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.white))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = line.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.nero),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = line.formula.toAnnotatedString(),
            fontSize = 12.sp,
            color = colorResource(id = R.color.dusty_gray),
            modifier = Modifier
                .weight(2f)
                .padding(end = 6.dp)
        )
        Text(
            text = line.amount.toAnnotatedString(),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultScreenCalculatePreview() {
    GoStopTheme {
        val sampleGamers = listOf(
            Gamer(
                name = "홍길동",
                account = 15000,
                calculate = listOf(
                    Target(name = "김철수", account = 10000),
                    Target(name = "이영희", account = 5000)
                )
            ),
            Gamer(
                name = "김철수",
                account = -10000,
                calculate = listOf(
                    Target(name = "홍길동", account = -10000)
                )
            ),
            Gamer(
                name = "이영희",
                account = -5000,
                calculate = listOf(
                    Target(name = "홍길동", account = -5000)
                )
            )
        )

        ResultScreen(
            uiState = ResultUiState(
                screenType = ResultActivity.ScreenType.CALCULATE,
                gameName = "즐거운 고스톱",
                gamers = sampleGamers
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultScreenDetailPreview() {
    GoStopTheme {
        val sampleGamers = listOf(
            Gamer(
                name = "홍길동",
                account = 20000,
                calculate = listOf(
                    Target(name = "김철수", account = 20000)
                )
            ),
            Gamer(
                name = "김철수",
                account = -20000,
                calculate = listOf(
                    Target(name = "홍길동", account = -20000)
                )
            )
        )

        ResultScreen(
            uiState = ResultUiState(
                screenType = ResultActivity.ScreenType.DETAIL,
                gameName = "상세 정산 내역",
                gamers = sampleGamers
            )
        )
    }
}
