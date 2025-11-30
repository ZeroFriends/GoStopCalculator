package zero.friends.gostopcalculator.ui.board.result

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import zero.friends.domain.model.CalculationFactors
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.util.Const
import zero.friends.gostopcalculator.util.getMoneyColor
import java.text.DecimalFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

class RoundTraceFormatter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    data class TraceLine(
        val title: String,
        val formula: CharSequence,
        val amount: CharSequence
    )
    
    data class WinnerTraceLine(
        val title: String,
        val amount: CharSequence
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

    private fun buildFormula(factors: CalculationFactors, totalAmount: Int): CharSequence {
        if (factors.isSeller) {
            return buildSellerFormula(factors, totalAmount)
        }
        return buildDefaultFormula(factors, totalAmount)
    }

    private fun buildSellerFormula(factors: CalculationFactors, totalAmount: Int): CharSequence {
        val builder = SpannableStringBuilder()
        
        val loserAmount = totalAmount - factors.sellAmount
        val sellAmountPerBuyer = factors.sellAmount / factors.receiverCount.coerceAtLeast(1)

        if (loserAmount != 0) {
            val tempFactors = factors.copy(sellAmount = 0)
            builder.append(buildDefaultFormula(tempFactors, loserAmount))
            builder.append("\n")
        }

        builder.append("${formatNumber(sellAmountPerBuyer)}원 x ${factors.receiverCount}명 (${Const.Rule.Sell.displayName})")

        if (loserAmount != 0) {
            builder.append("\n  ➡️ ")
            builder.bold { append("= ${formatNumber(totalAmount)}원") }
        }
        
        return builder
    }
    
    private fun buildDefaultFormula(factors: CalculationFactors, totalAmount: Int): CharSequence {
        val builder = SpannableStringBuilder()
        val divider = factors.receiverCount.coerceAtLeast(1)
        var partCount = 0

        // 1. 기본 점수 파트
        val baseScorePart = mutableListOf<String>()
        val totalScore = factors.baseScore + factors.goCount
        if (totalScore > 0) {
            baseScorePart.add("(기본 ${factors.baseScore}점 + ${factors.goCount}고)")
            if (factors.scorePerPoint > 0) {
                baseScorePart.add("x ${formatNumber(factors.scorePerPoint)}원")
            }
        }

        if (baseScorePart.isNotEmpty()) {
            builder.append(baseScorePart.joinToString(" "))
            partCount++
        }

        // "고" 배율 추가
        if (factors.goCount >= 3) {
            if (builder.isNotEmpty()) builder.append("\n")
            builder.append("x 2${(factors.goCount - 2).toSuperscript()}(${factors.goCount}고)")
            partCount++
        }
        // "박" 배율 추가
        if (factors.bakCount > 0) {
            if (builder.isNotEmpty()) builder.append("\n")
            builder.append("x 2${factors.bakCount.toSuperscript()}(${factors.bakCount}박)")
            partCount++
        }

        // 2. 추가 점수 파트 (뻑, 따닥, 광팔기)
        if (factors.fuckAmount != 0) {
            if (builder.isNotEmpty()) builder.append("\n")
            builder.append("${formatSigned(factors.fuckAmount / divider)}원 (${Const.Rule.Fuck.displayName})")
            partCount++
        }
        if (factors.ddadakAmount != 0) {
            if (builder.isNotEmpty()) builder.append("\n")
            builder.append("${formatSigned(factors.ddadakAmount / divider)}원 (${Const.Rule.FirstDdadak.displayName})")
            partCount++
        }
        if (factors.sellAmount != 0) {
            if (builder.isNotEmpty()) builder.append("\n")
            builder.append("${formatSigned(factors.sellAmount / divider)}원 (${Const.Rule.Sell.displayName})")
            partCount++
        }

        // 3. 중간 계산 결과
        val intermediateAmount = totalAmount / divider
        if (partCount > 1) {
            builder.append("\n  ➡️ ")
            builder.bold { append("= ${formatNumber(intermediateAmount)}원") }
        }

        // 4. 최종 계산 (인원 수)
        if (factors.receiverCount > 1) {
            if (builder.isNotEmpty()) builder.append("\n")
            builder.append("  ➡️ ${formatNumber(intermediateAmount)}원 x ${factors.receiverCount}명 ")
            builder.bold { append("= ${formatNumber(totalAmount)}원") }
        }

        return builder
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

    fun formatAmount(value: Int): CharSequence = buildAmount(value)

    private fun buildAmount(
        value: Int
    ): CharSequence {
        val builder = SpannableStringBuilder()
        val sign = if (value >= 0) "+" else "-"
        val amount = String.format(Locale.KOREA, "%,d", abs(value))
        val start = builder.length
        builder.append(sign).append(amount).append("원")
        val end = builder.length
        val color = ContextCompat.getColor(context, value.getMoneyColor())
        builder.setSpan(
            ForegroundColorSpan(color),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return builder
    }

    private fun formatNumber(value: Number): String {
        val formatter = DecimalFormat("#,##0.##")
        return formatter.format(value)
    }

    private fun formatSigned(value: Int): String {
        val sign = if (value >= 0) "+" else "-"
        return "$sign ${formatNumber(abs(value))}"
    }

    private fun SpannableStringBuilder.bold(block: SpannableStringBuilder.() -> Unit) {
        val start = length
        block()
        setSpan(StyleSpan(Typeface.BOLD), start, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}
