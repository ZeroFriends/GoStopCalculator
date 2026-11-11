package zero.friends.gostopcalculator.ui.board.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.CalculationFactors
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.model.TraceTag
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.bak_unit
import zero.friends.gostopcalculator.shared.generated.resources.base_label
import zero.friends.gostopcalculator.shared.generated.resources.go
import zero.friends.gostopcalculator.shared.generated.resources.person_unit
import zero.friends.gostopcalculator.shared.generated.resources.point
import zero.friends.gostopcalculator.shared.generated.resources.rule_first_ddadak
import zero.friends.gostopcalculator.shared.generated.resources.rule_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_go_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_light_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_loser
import zero.friends.gostopcalculator.shared.generated.resources.rule_mong_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_pea_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_sell
import zero.friends.gostopcalculator.shared.generated.resources.rule_seller_role
import zero.friends.gostopcalculator.shared.generated.resources.rule_three_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_winner
import zero.friends.gostopcalculator.shared.generated.resources.trace_base_score
import zero.friends.gostopcalculator.shared.generated.resources.won
import zero.friends.gostopcalculator.util.getMoneyColor
import zero.friends.gostopcalculator.util.separateComma
import kotlin.math.abs

@Composable
fun rememberTraceTexts(): RoundTraceFormatter.TraceTexts {
    val baseScoreTitle = stringResource(Res.string.trace_base_score)
    val baseLabel = stringResource(Res.string.base_label)
    val pointUnit = stringResource(Res.string.point)
    val goUnit = stringResource(Res.string.go)
    val bakUnit = stringResource(Res.string.bak_unit)
    val wonUnit = stringResource(Res.string.won)
    val personUnit = stringResource(Res.string.person_unit)
    val fuckName = stringResource(Res.string.rule_fuck)
    val firstDdadakName = stringResource(Res.string.rule_first_ddadak)
    val sellName = stringResource(Res.string.rule_sell)
    val winnerLabel = stringResource(Res.string.rule_winner)
    val loserLabel = stringResource(Res.string.rule_loser)
    val sellerRoleLabel = stringResource(Res.string.rule_seller_role)
    val threeFuckLabel = stringResource(Res.string.rule_three_fuck)
    val peaBakLabel = stringResource(Res.string.rule_pea_bak)
    val lightBakLabel = stringResource(Res.string.rule_light_bak)
    val mongBakLabel = stringResource(Res.string.rule_mong_bak)
    val goBakLabel = stringResource(Res.string.rule_go_bak)

    return remember(
        baseScoreTitle, baseLabel, pointUnit, goUnit, bakUnit, wonUnit, personUnit,
        fuckName, firstDdadakName, sellName, winnerLabel, loserLabel, sellerRoleLabel,
        threeFuckLabel, peaBakLabel, lightBakLabel, mongBakLabel, goBakLabel
    ) {
        RoundTraceFormatter.TraceTexts(
            baseScoreTitle = baseScoreTitle,
            baseLabel = baseLabel,
            pointUnit = pointUnit,
            goUnit = goUnit,
            bakUnit = bakUnit,
            wonUnit = wonUnit,
            personUnit = personUnit,
            fuckName = fuckName,
            firstDdadakName = firstDdadakName,
            sellName = sellName,
            winnerLabel = winnerLabel,
            loserLabel = loserLabel,
            sellerRoleLabel = sellerRoleLabel,
            threeFuckLabel = threeFuckLabel,
            peaBakLabel = peaBakLabel,
            lightBakLabel = lightBakLabel,
            mongBakLabel = mongBakLabel,
            goBakLabel = goBakLabel
        )
    }
}

@Inject
class RoundTraceFormatter {

    data class TraceTexts(
        val baseScoreTitle: String,
        val baseLabel: String,
        val pointUnit: String,
        val goUnit: String,
        val bakUnit: String,
        val wonUnit: String,
        val personUnit: String,
        val fuckName: String,
        val firstDdadakName: String,
        val sellName: String,
        val winnerLabel: String,
        val loserLabel: String,
        val sellerRoleLabel: String,
        val threeFuckLabel: String,
        val peaBakLabel: String,
        val lightBakLabel: String,
        val mongBakLabel: String,
        val goBakLabel: String
    )

    data class TraceLine(
        val title: String,
        val formula: AnnotatedString,
        val amount: AnnotatedString
    )
    
    data class WinnerTraceLine(
        val title: String,
        val amount: AnnotatedString
    )

    fun toWinnerBreakdown(
        winnerTerm: RoundTraceTerm,
        texts: TraceTexts
    ): List<WinnerTraceLine> {
        val lines = mutableListOf<WinnerTraceLine>()
        val factors = winnerTerm.factors
        
        val baseAmount = winnerTerm.amount - (factors.fuckAmount + factors.ddadakAmount + factors.sellAmount)

        if (baseAmount != 0) {
            lines.add(WinnerTraceLine(texts.baseScoreTitle, buildAmount(baseAmount, texts)))
        }
        if (factors.fuckAmount != 0) {
            lines.add(WinnerTraceLine(texts.fuckName, buildAmount(factors.fuckAmount, texts)))
        }
        if (factors.ddadakAmount != 0) {
            lines.add(WinnerTraceLine(texts.firstDdadakName, buildAmount(factors.ddadakAmount, texts)))
        }
        if (factors.sellAmount != 0) {
            lines.add(WinnerTraceLine(texts.sellName, buildAmount(factors.sellAmount, texts)))
        }
        return lines
    }

    fun toLines(
        terms: List<RoundTraceTerm>,
        texts: TraceTexts
    ): List<TraceLine> {
        return terms
            .filter { term ->
                term.amount != 0 ||
                    term.factors.fuckAmount != 0 ||
                    term.factors.ddadakAmount != 0 ||
                    term.factors.sellAmount != 0
            }
            .map { term ->
                TraceLine(
                    title = formatTitle(term, texts),
                    formula = buildFormula(term.factors, term.amount, texts),
                    amount = buildAmount(term.amount, texts)
                )
            }
    }

    private fun formatTitle(term: RoundTraceTerm, texts: TraceTexts): String {
        if (term.playerName.isEmpty()) return term.label
        if (term.traceTags.isEmpty()) return term.playerName
        val roleText = term.traceTags.joinToString(separator = ", ") { tag ->
            when (tag) {
                TraceTag.THREE_FUCK -> texts.threeFuckLabel
                TraceTag.WINNER -> texts.winnerLabel
                TraceTag.SELLER -> texts.sellerRoleLabel
                TraceTag.LOSER -> texts.loserLabel
                TraceTag.PEA_BAK -> texts.peaBakLabel
                TraceTag.LIGHT_BAK -> texts.lightBakLabel
                TraceTag.MONG_BAK -> texts.mongBakLabel
                TraceTag.GO_BAK -> texts.goBakLabel
            }
        }
        return "${term.playerName}\n($roleText)"
    }

    private fun buildFormula(
        factors: CalculationFactors,
        totalAmount: Int,
        texts: TraceTexts
    ): AnnotatedString {
        if (factors.isSeller) {
            return buildSellerFormula(factors, totalAmount, texts)
        }
        return buildDefaultFormula(factors, totalAmount, texts)
    }

    private fun buildSellerFormula(
        factors: CalculationFactors,
        totalAmount: Int,
        texts: TraceTexts
    ): AnnotatedString {
        val loserAmount = totalAmount - factors.sellAmount
        val sellAmountPerBuyer = factors.sellAmount / factors.receiverCount.coerceAtLeast(1)

        return buildAnnotatedString {
            if (loserAmount != 0) {
                val tempFactors = factors.copy(sellAmount = 0)
                append(buildDefaultFormula(tempFactors, loserAmount, texts))
                append("\n")
            }

            append("${formatNumber(sellAmountPerBuyer)}${texts.wonUnit} x ${factors.receiverCount}${texts.personUnit} (${texts.sellName})")

            if (loserAmount != 0) {
                append("\n  ➡️ ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("= ${formatNumber(totalAmount)}${texts.wonUnit}")
                pop()
            }
        }
    }
    
    private fun buildDefaultFormula(
        factors: CalculationFactors,
        totalAmount: Int,
        texts: TraceTexts
    ): AnnotatedString {
        val divider = factors.receiverCount.coerceAtLeast(1)
        var partCount = 0

        return buildAnnotatedString {
            val baseScorePart = mutableListOf<String>()
            val totalScore = factors.baseScore + factors.goCount
            if (totalScore > 0) {
                baseScorePart.add("(${texts.baseLabel} ${factors.baseScore}${texts.pointUnit} + ${factors.goCount}${texts.goUnit})")
                if (factors.scorePerPoint > 0) {
                    baseScorePart.add("x ${formatNumber(factors.scorePerPoint)}${texts.wonUnit}")
                }
            }

            if (baseScorePart.isNotEmpty()) {
                append(baseScorePart.joinToString(" "))
                partCount++
            }

            if (factors.goCount >= 3) {
                if (length > 0) append("\n")
                append("x 2${(factors.goCount - 2).toSuperscript()}(${factors.goCount}${texts.goUnit})")
                partCount++
            }
            if (factors.bakCount > 0) {
                if (length > 0) append("\n")
                append("x 2${factors.bakCount.toSuperscript()}(${factors.bakCount}${texts.bakUnit})")
                partCount++
            }

            if (factors.fuckAmount != 0) {
                if (length > 0) append("\n")
                append("${formatSigned(factors.fuckAmount / divider)}${texts.wonUnit} (${texts.fuckName})")
                partCount++
            }
            if (factors.ddadakAmount != 0) {
                if (length > 0) append("\n")
                append("${formatSigned(factors.ddadakAmount / divider)}${texts.wonUnit} (${texts.firstDdadakName})")
                partCount++
            }
            if (factors.sellAmount != 0) {
                if (length > 0) append("\n")
                append("${formatSigned(factors.sellAmount / divider)}${texts.wonUnit} (${texts.sellName})")
                partCount++
            }

            val intermediateAmount = totalAmount / divider
            if (partCount > 1) {
                append("\n  ➡️ ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("= ${formatNumber(intermediateAmount)}${texts.wonUnit}")
                pop()
            }

            if (factors.receiverCount > 1) {
                if (length > 0) append("\n")
                append("  ➡️ ${formatNumber(intermediateAmount)}${texts.wonUnit} x ${factors.receiverCount}${texts.personUnit} ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("= ${formatNumber(totalAmount)}${texts.wonUnit}")
                pop()
            }
        }
    }

    private fun Int.toSuperscript(): String {
        if (this <= 1) return ""
        return toString().map {
            when (it) {
                '0' -> '⁰'
                '1' -> '¹'
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
    }

    fun formatAmount(value: Int, texts: TraceTexts): AnnotatedString = buildAmount(value, texts)

    private fun buildAmount(value: Int, texts: TraceTexts): AnnotatedString {
        val sign = when {
            value > 0 -> "+"
            value < 0 -> "-"
            else -> ""
        }
        val amount = abs(value).separateComma()
        val color = value.getMoneyColor()

        return buildAnnotatedString {
            pushStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold))
            append("$sign${amount}${texts.wonUnit}")
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
