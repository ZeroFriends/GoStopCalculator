package zero.friends.gostopcalculator.ui.board.result

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import me.tatarka.inject.annotations.Inject
import zero.friends.domain.model.CalculationFactors
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.util.Const
import zero.friends.gostopcalculator.util.getMoneyColor
import zero.friends.gostopcalculator.util.separateComma
import kotlin.math.abs

@Inject
class RoundTraceFormatter {

    data class TraceLine(
        val title: String,
        val formula: AnnotatedString,
        val amount: AnnotatedString
    )
    
    data class WinnerTraceLine(
        val title: String,
        val amount: AnnotatedString
    )

    fun toWinnerBreakdown(winnerTerm: RoundTraceTerm): List<WinnerTraceLine> {
        val lines = mutableListOf<WinnerTraceLine>()
        val factors = winnerTerm.factors
        
        val baseAmount = winnerTerm.amount - (factors.fuckAmount + factors.ddadakAmount + factors.sellAmount)

        if (baseAmount != 0) {
            lines.add(WinnerTraceLine("기본 점수", buildAmount(baseAmount)))
        }
        if (factors.fuckAmount != 0) {
            lines.add(WinnerTraceLine(Const.Rule.Fuck.displayName, buildAmount(factors.fuckAmount)))
        }
        if (factors.ddadakAmount != 0) {
            lines.add(WinnerTraceLine(Const.Rule.FirstDdadak.displayName, buildAmount(factors.ddadakAmount)))
        }
        if (factors.sellAmount != 0) {
            lines.add(WinnerTraceLine(Const.Rule.Sell.displayName, buildAmount(factors.sellAmount)))
        }
        return lines
    }

    fun toLines(terms: List<RoundTraceTerm>): List<TraceLine> {
        return terms
            .filter { it.amount != 0 }
            .map { term ->
                TraceLine(
                    title = term.label,
                    formula = buildFormula(term.factors, term.amount),
                    amount = buildAmount(term.amount)
                )
            }
    }

    private fun buildFormula(factors: CalculationFactors, totalAmount: Int): AnnotatedString {
        if (factors.isSeller) {
            return buildSellerFormula(factors, totalAmount)
        }
        return buildDefaultFormula(factors, totalAmount)
    }

    private fun buildSellerFormula(factors: CalculationFactors, totalAmount: Int): AnnotatedString {
        val loserAmount = totalAmount - factors.sellAmount
        val sellAmountPerBuyer = factors.sellAmount / factors.receiverCount.coerceAtLeast(1)

        return buildAnnotatedString {
            if (loserAmount != 0) {
                val tempFactors = factors.copy(sellAmount = 0)
                append(buildDefaultFormula(tempFactors, loserAmount))
                append("\n")
            }

            append("${formatNumber(sellAmountPerBuyer)}원 x ${factors.receiverCount}명 (${Const.Rule.Sell.displayName})")

            if (loserAmount != 0) {
                append("\n  ➡️ ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("= ${formatNumber(totalAmount)}원")
                pop()
            }
        }
    }
    
    private fun buildDefaultFormula(factors: CalculationFactors, totalAmount: Int): AnnotatedString {
        val divider = factors.receiverCount.coerceAtLeast(1)
        var partCount = 0

        return buildAnnotatedString {
            val baseScorePart = mutableListOf<String>()
            val totalScore = factors.baseScore + factors.goCount
            if (totalScore > 0) {
                baseScorePart.add("(기본 ${factors.baseScore}점 + ${factors.goCount}고)")
                if (factors.scorePerPoint > 0) {
                    baseScorePart.add("x ${formatNumber(factors.scorePerPoint)}원")
                }
            }

            if (baseScorePart.isNotEmpty()) {
                append(baseScorePart.joinToString(" "))
                partCount++
            }

            if (factors.goCount >= 3) {
                if (length > 0) append("\n")
                append("x 2${(factors.goCount - 2).toSuperscript()}(${factors.goCount}고)")
                partCount++
            }
            if (factors.bakCount > 0) {
                if (length > 0) append("\n")
                append("x 2${factors.bakCount.toSuperscript()}(${factors.bakCount}박)")
                partCount++
            }

            if (factors.fuckAmount != 0) {
                if (length > 0) append("\n")
                append("${formatSigned(factors.fuckAmount / divider)}원 (${Const.Rule.Fuck.displayName})")
                partCount++
            }
            if (factors.ddadakAmount != 0) {
                if (length > 0) append("\n")
                append("${formatSigned(factors.ddadakAmount / divider)}원 (${Const.Rule.FirstDdadak.displayName})")
                partCount++
            }
            if (factors.sellAmount != 0) {
                if (length > 0) append("\n")
                append("${formatSigned(factors.sellAmount / divider)}원 (${Const.Rule.Sell.displayName})")
                partCount++
            }

            val intermediateAmount = totalAmount / divider
            if (partCount > 1) {
                append("\n  ➡️ ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("= ${formatNumber(intermediateAmount)}원")
                pop()
            }

            if (factors.receiverCount > 1) {
                if (length > 0) append("\n")
                append("  ➡️ ${formatNumber(intermediateAmount)}원 x ${factors.receiverCount}명 ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("= ${formatNumber(totalAmount)}원")
                pop()
            }
        }
    }

    private fun Int.toSuperscript(): String = toString().map {
        when (it) {
            '0', '1' -> ""
            '2' -> '²'
            '3' -> '³'
            '4' -> '⁴'
            '5' -> '⁵'
            '6' -> '⁶'
            '7' -> '⁷'
            '8' -> '⁸'
            '9' -> '⁹'
            else -> it
        }
    }.joinToString("")

    fun formatAmount(value: Int): AnnotatedString = buildAmount(value)

    private fun buildAmount(value: Int): AnnotatedString {
        val sign = if (value >= 0) "+" else "-"
        val amount = abs(value).separateComma()
        val color = value.getMoneyColor()

        return buildAnnotatedString {
            pushStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold))
            append("$sign${amount}원")
            pop()
        }
    }

    private fun formatNumber(value: Number): String {
        return value.toLong().separateComma()
    }

    private fun formatSigned(value: Int): String {
        val sign = if (value >= 0) "+" else "-"
        return "$sign ${formatNumber(abs(value))}"
    }
}
