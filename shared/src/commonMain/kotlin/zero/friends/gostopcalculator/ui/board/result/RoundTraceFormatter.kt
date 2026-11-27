package zero.friends.gostopcalculator.ui.board.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.CalculationFactors
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.TraceTag
import zero.friends.domain.usecase.calculate.calculateMaxPerLoser
import zero.friends.domain.usecase.calculate.calculateShakeBombMultiplier
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.bak_unit
import zero.friends.gostopcalculator.shared.generated.resources.base_label
import zero.friends.gostopcalculator.shared.generated.resources.go
import zero.friends.gostopcalculator.shared.generated.resources.person_unit
import zero.friends.gostopcalculator.shared.generated.resources.point
import zero.friends.gostopcalculator.shared.generated.resources.result_base_pot_no_go_format
import zero.friends.gostopcalculator.shared.generated.resources.result_base_pot_special_mult_format
import zero.friends.gostopcalculator.shared.generated.resources.result_base_pot_with_go_format
import zero.friends.gostopcalculator.shared.generated.resources.result_base_pot_with_go_mult_format
import zero.friends.gostopcalculator.shared.generated.resources.result_breakdown_bonus_per_player_format
import zero.friends.gostopcalculator.shared.generated.resources.result_breakdown_gobak_cover_format
import zero.friends.gostopcalculator.shared.generated.resources.result_breakdown_gobak_exempt_format
import zero.friends.gostopcalculator.shared.generated.resources.result_breakdown_own_base_format
import zero.friends.gostopcalculator.shared.generated.resources.result_breakdown_own_base_mult_format
import zero.friends.gostopcalculator.shared.generated.resources.result_breakdown_round_win
import zero.friends.gostopcalculator.shared.generated.resources.result_breakdown_round_win_gobak_format
import zero.friends.gostopcalculator.shared.generated.resources.result_breakdown_sell_pay_format
import zero.friends.gostopcalculator.shared.generated.resources.result_seller_badge_cards_format
import zero.friends.gostopcalculator.shared.generated.resources.result_seller_formula_cards_format
import zero.friends.gostopcalculator.shared.generated.resources.result_seller_receive_each_format
import zero.friends.gostopcalculator.shared.generated.resources.result_three_fuck_banner
import zero.friends.gostopcalculator.shared.generated.resources.rule_bomb
import zero.friends.gostopcalculator.shared.generated.resources.rule_first_ddadak
import zero.friends.gostopcalculator.shared.generated.resources.rule_first_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_go_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_light_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_loser
import zero.friends.gostopcalculator.shared.generated.resources.rule_mong_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_pea_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_second_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_sell
import zero.friends.gostopcalculator.shared.generated.resources.rule_seller_role
import zero.friends.gostopcalculator.shared.generated.resources.rule_shake
import zero.friends.gostopcalculator.shared.generated.resources.rule_three_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_winner
import zero.friends.gostopcalculator.shared.generated.resources.trace_base_score
import zero.friends.gostopcalculator.shared.generated.resources.trace_go_bak_cover_format
import zero.friends.gostopcalculator.shared.generated.resources.trace_go_bak_exempt_formula_format
import zero.friends.gostopcalculator.shared.generated.resources.trace_go_bak_exempt_tag
import zero.friends.gostopcalculator.shared.generated.resources.trace_self_base_loss_format
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
    val goBakExemptLabel = stringResource(Res.string.trace_go_bak_exempt_tag)
    val selfBaseLossFormat = stringResource(Res.string.trace_self_base_loss_format)
    val goBakCoverFormat = stringResource(Res.string.trace_go_bak_cover_format)
    val goBakExemptFormulaFormat = stringResource(Res.string.trace_go_bak_exempt_formula_format)
    val firstFuckName = stringResource(Res.string.rule_first_fuck)
    val secondFuckName = stringResource(Res.string.rule_second_fuck)
    val threeFuckBanner = stringResource(Res.string.result_three_fuck_banner)
    val basePotNoGoFormat = stringResource(Res.string.result_base_pot_no_go_format)
    val basePotWithGoFormat = stringResource(Res.string.result_base_pot_with_go_format)
    val basePotWithGoMultFormat = stringResource(Res.string.result_base_pot_with_go_mult_format)
    val breakdownRoundWin = stringResource(Res.string.result_breakdown_round_win)
    val breakdownRoundWinGoBakFormat = stringResource(Res.string.result_breakdown_round_win_gobak_format)
    val breakdownOwnBaseFormat = stringResource(Res.string.result_breakdown_own_base_format)
    val breakdownOwnBaseMultFormat = stringResource(Res.string.result_breakdown_own_base_mult_format)
    val breakdownGoBakCoverFormat = stringResource(Res.string.result_breakdown_gobak_cover_format)
    val breakdownGoBakExemptFormat = stringResource(Res.string.result_breakdown_gobak_exempt_format)
    val breakdownBonusPerPlayerFormat = stringResource(Res.string.result_breakdown_bonus_per_player_format)
    val breakdownSellPayFormat = stringResource(Res.string.result_breakdown_sell_pay_format)
    val sellerBadgeCardsFormat = stringResource(Res.string.result_seller_badge_cards_format)
    val sellerFormulaCardsFormat = stringResource(Res.string.result_seller_formula_cards_format)
    val sellerReceiveEachFormat = stringResource(Res.string.result_seller_receive_each_format)
    val shakeName = stringResource(Res.string.rule_shake)
    val bombName = stringResource(Res.string.rule_bomb)
    val basePotSpecialMultFormat = stringResource(Res.string.result_base_pot_special_mult_format)

    return remember(
        baseScoreTitle, baseLabel, pointUnit, goUnit, bakUnit, wonUnit, personUnit,
        fuckName, firstDdadakName, sellName, winnerLabel, loserLabel, sellerRoleLabel,
        threeFuckLabel, peaBakLabel, lightBakLabel, mongBakLabel, goBakLabel,
        goBakExemptLabel, selfBaseLossFormat, goBakCoverFormat, goBakExemptFormulaFormat,
        firstFuckName, secondFuckName, threeFuckBanner, basePotNoGoFormat, basePotWithGoFormat,
        basePotWithGoMultFormat, breakdownRoundWin, breakdownRoundWinGoBakFormat,
        breakdownOwnBaseFormat, breakdownOwnBaseMultFormat, breakdownGoBakCoverFormat,
        breakdownGoBakExemptFormat, breakdownBonusPerPlayerFormat, breakdownSellPayFormat,
        sellerBadgeCardsFormat, sellerFormulaCardsFormat, sellerReceiveEachFormat,
        shakeName, bombName, basePotSpecialMultFormat
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
            goBakLabel = goBakLabel,
            goBakExemptLabel = goBakExemptLabel,
            selfBaseLossFormat = selfBaseLossFormat,
            goBakCoverFormat = goBakCoverFormat,
            goBakExemptFormulaFormat = goBakExemptFormulaFormat,
            firstFuckName = firstFuckName,
            secondFuckName = secondFuckName,
            threeFuckBanner = threeFuckBanner,
            basePotNoGoFormat = basePotNoGoFormat,
            basePotWithGoFormat = basePotWithGoFormat,
            basePotWithGoMultFormat = basePotWithGoMultFormat,
            breakdownRoundWin = breakdownRoundWin,
            breakdownRoundWinGoBakFormat = breakdownRoundWinGoBakFormat,
            breakdownOwnBaseFormat = breakdownOwnBaseFormat,
            breakdownOwnBaseMultFormat = breakdownOwnBaseMultFormat,
            breakdownGoBakCoverFormat = breakdownGoBakCoverFormat,
            breakdownGoBakExemptFormat = breakdownGoBakExemptFormat,
            breakdownBonusPerPlayerFormat = breakdownBonusPerPlayerFormat,
            breakdownSellPayFormat = breakdownSellPayFormat,
            sellerBadgeCardsFormat = sellerBadgeCardsFormat,
            sellerFormulaCardsFormat = sellerFormulaCardsFormat,
            sellerReceiveEachFormat = sellerReceiveEachFormat,
            shakeName = shakeName,
            bombName = bombName,
            basePotSpecialMultFormat = basePotSpecialMultFormat,
        )
    }
}

@Immutable
@Inject
class RoundTraceFormatter {

    override fun equals(other: Any?): Boolean = other is RoundTraceFormatter

    override fun hashCode(): Int = this::class.hashCode()

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
        val goBakLabel: String,
        val goBakExemptLabel: String = "Go-bak Exempt",
        val selfBaseLossFormat: String = "Own base: %1\$s",
        val goBakCoverFormat: String = "%1\$s Go-bak cover",
        val goBakExemptFormulaFormat: String = "Exempted (%1\$s Go-bak)",
        val firstFuckName: String = "First Ppeok",
        val secondFuckName: String = "Double Ppeok",
        val threeFuckBanner: String = "Triple Ppeok Instant Win (All Players Pay)",
        val basePotNoGoFormat: String = "Base %1\$dpts × %2\$s",
        val basePotWithGoFormat: String = "(Base %1\$dpts + %2\$dGo) × %3\$s",
        val basePotWithGoMultFormat: String = "(Base %1\$dpts + %2\$dGo) × %3\$s × %4\$dx(%2\$dGo)",
        val breakdownRoundWin: String = "Round Win",
        val breakdownRoundWinGoBakFormat: String = "Round Win (%1\$s Go-bak solo)",
        val breakdownOwnBaseFormat: String = "Own base (%1\$s)",
        val breakdownOwnBaseMultFormat: String = "Own base (%1\$s × %2\$dx)",
        val breakdownGoBakCoverFormat: String = "⚡ %1\$s Go-bak cover (%2\$dx)",
        val breakdownGoBakExemptFormat: String = "Base score exempted (%1\$s Go-bak)",
        val breakdownBonusPerPlayerFormat: String = "%1\$s bonus (%2\$s × %3\$d)",
        val breakdownSellPayFormat: String = "Sell Brights paid (%1\$s)",
        val sellerBadgeCardsFormat: String = "Seller · %1\$d",
        val sellerFormulaCardsFormat: String = "%1\$d cards × %2\$s × %3\$d",
        val sellerReceiveEachFormat: String = "+%1\$s each from %2\$d players",
        val shakeName: String = "Shake",
        val bombName: String = "Bomb",
        val basePotSpecialMultFormat: String = "%1\$s × %2\$dx(%3\$s)",
    )

    data class RoundBasePotSummary(
        val isThreeFuck: Boolean,
        val formulaText: String,
        val potAmountText: String,
    )

    enum class BreakdownRowTone {
        WIN,
        GO_BAK_COVER,
        EXEMPT,
        BONUS_POSITIVE,
        LOSS_DEFAULT,
    }

    data class DetailBreakdownRow(
        val label: String,
        val amountText: String,
        val amountValue: Int,
        val tone: BreakdownRowTone,
    )

    data class DetailSellerSummary(
        val badgeText: String,
        val formulaText: String,
        val receiveEachText: String,
    )

    data class TraceLine(
        val title: String,
        val formula: AnnotatedString,
        val amount: AnnotatedString
    )

    fun toLines(
        terms: List<RoundTraceTerm>,
        texts: TraceTexts
    ): List<TraceLine> {
        return terms
            .filter { term ->
                term.amount != 0 ||
                    term.factors.fuckAmount != 0 ||
                    term.factors.ddadakAmount != 0 ||
                    term.factors.sellAmount != 0 ||
                    term.factors.goBakExemptedAmount != 0
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
                TraceTag.GO_BAK_EXEMPT -> texts.goBakExemptLabel
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
        if (factors.goBakExemptedAmount != 0) {
            return buildGoBakExemptFormula(factors, totalAmount, texts)
        }
        if (factors.goBakCoveredAmount != 0 || factors.coveredLosers.isNotEmpty()) {
            return buildGoBakSoloPayerFormula(factors, totalAmount, texts)
        }
        return buildDefaultFormula(factors, totalAmount, texts)
    }

    private fun buildGoBakSoloPayerFormula(
        factors: CalculationFactors,
        totalAmount: Int,
        texts: TraceTexts,
    ): AnnotatedString {
        val divider = factors.receiverCount.coerceAtLeast(1)
        return buildAnnotatedString {
            appendBaseMultiplierFormula(factors, texts)
            if (length > 0) append("\n")
            val selfLossText = "${formatNumber(factors.selfBaseLoss)}${texts.wonUnit}"
            append("  ➡️ ${texts.selfBaseLossFormat.replace("%1\$s", selfLossText)}")

            factors.coveredLosers.filter { it.amount != 0 }.forEach { covered ->
                append("\n")
                val coverLabel = texts.goBakCoverFormat.replace("%1\$s", covered.playerName)
                append("${formatSigned(covered.amount)}${texts.wonUnit} ($coverLabel)")
            }

            appendAuxiliaryFactors(factors, divider, texts)

            append("\n  ➡️ ")
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append("= ${formatNumber(totalAmount)}${texts.wonUnit}")
            pop()
        }
    }

    private fun buildGoBakExemptFormula(
        factors: CalculationFactors,
        totalAmount: Int,
        texts: TraceTexts,
    ): AnnotatedString {
        val divider = factors.receiverCount.coerceAtLeast(1)
        val hasAux = factors.fuckAmount != 0 || factors.ddadakAmount != 0 || factors.sellAmount != 0
        return buildAnnotatedString {
            appendBaseMultiplierFormula(factors, texts)
            if (length > 0) append("\n")
            val exemptNote = texts.goBakExemptFormulaFormat.replace("%1\$s", factors.goBakPayerName)
            append("  ➡️ 0${texts.wonUnit} ($exemptNote)")

            if (hasAux) {
                appendAuxiliaryFactors(factors, divider, texts)
                append("\n  ➡️ ")
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append("= ${formatNumber(totalAmount)}${texts.wonUnit}")
                pop()
            }
        }
    }

    private fun AnnotatedString.Builder.appendBaseMultiplierFormula(
        factors: CalculationFactors,
        texts: TraceTexts,
    ): Int {
        var partCount = 0
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
        if (totalScore > 0) {
            if (factors.goCount >= 3) {
                if (length > 0) append("\n")
                append("x 2${(factors.goCount - 2).toSuperscript()}(${factors.goCount}${texts.goUnit})")
                partCount++
            }
            val specialTotal = factors.shakeCount.coerceAtLeast(0) + factors.bombCount.coerceAtLeast(0)
            if (specialTotal > 0) {
                val specialLabel = buildSpecialLabel(factors.shakeCount, factors.bombCount, texts)
                if (length > 0) append("\n")
                append("x 2${specialTotal.toSuperscript()}($specialLabel)")
                partCount++
            }
            if (factors.bakCount > 0) {
                if (length > 0) append("\n")
                append("x 2${factors.bakCount.toSuperscript()}(${factors.bakCount}${texts.bakUnit})")
                partCount++
            }
        }
        return partCount
    }

    private fun AnnotatedString.Builder.appendAuxiliaryFactors(
        factors: CalculationFactors,
        divider: Int,
        texts: TraceTexts,
    ): Int {
        var auxCount = 0
        if (factors.fuckAmount != 0) {
            if (length > 0) append("\n")
            append("${formatSigned(factors.fuckAmount / divider)}${texts.wonUnit} (${texts.fuckName})")
            auxCount++
        }
        if (factors.ddadakAmount != 0) {
            if (length > 0) append("\n")
            append("${formatSigned(factors.ddadakAmount / divider)}${texts.wonUnit} (${texts.firstDdadakName})")
            auxCount++
        }
        if (factors.sellAmount != 0) {
            if (length > 0) append("\n")
            append("${formatSigned(factors.sellAmount / divider)}${texts.wonUnit} (${texts.sellName})")
            auxCount++
        }
        return auxCount
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

        return buildAnnotatedString {
            var partCount = appendBaseMultiplierFormula(factors, texts)
            partCount += appendAuxiliaryFactors(factors, divider, texts)

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

    fun toRoundBasePotSummary(
        trace: RoundTrace,
        texts: TraceTexts,
    ): RoundBasePotSummary? {
        val winnerTerm = trace.terms.firstOrNull { it.gamerId == trace.winnerId }
        val isInstantThreeFuck = winnerTerm != null &&
            TraceTag.THREE_FUCK in winnerTerm.traceTags &&
            trace.terms.all { term ->
                term.factors.isSeller ||
                    (term.amount.toLong() - term.factors.fuckAmount - term.factors.ddadakAmount - term.factors.sellAmount) == 0L
            }
        if (isInstantThreeFuck) {
            return RoundBasePotSummary(
                isThreeFuck = true,
                formulaText = texts.threeFuckBanner,
                potAmountText = "",
            )
        }

        val refFactors = trace.terms
            .firstOrNull { !it.factors.isSeller && it.factors.baseScore > 0 }?.factors
            ?: trace.terms.firstOrNull { !it.factors.isSeller }?.factors
            ?: return null

        if (refFactors.baseScore == 0 && refFactors.goCount == 0) {
            return null
        }

        val perPointStr = "${formatNumber(refFactors.scorePerPoint)}${texts.wonUnit}"
        val goCount = refFactors.goCount
        val loserCount = resolveLoserCount(trace)
        val basePot = calculateBasePotAmount(refFactors, loserCount)
        val baseFormula = when {
            goCount <= 0 -> texts.basePotNoGoFormat
                .replace("%1\$d", refFactors.baseScore.toString())
                .replace("%2\$s", perPointStr)
            goCount < 3 -> texts.basePotWithGoFormat
                .replace("%2\$d", goCount.toString())
                .replace("%3\$s", perPointStr)
                .replace("%1\$d", refFactors.baseScore.toString())
            else -> {
                val goMult = 1L shl (goCount - 2).coerceAtMost(30)
                texts.basePotWithGoMultFormat
                    .replace("%4\$d", goMult.toString())
                    .replace("%2\$d", goCount.toString())
                    .replace("%3\$s", perPointStr)
                    .replace("%1\$d", refFactors.baseScore.toString())
            }
        }
        val specialMult = calculateShakeBombMultiplier(refFactors.shakeCount, refFactors.bombCount)
        val formulaText = if (specialMult > 1) {
            val specialLabel = buildSpecialLabel(refFactors.shakeCount, refFactors.bombCount, texts)
            texts.basePotSpecialMultFormat
                .replace("%3\$s", specialLabel)
                .replace("%2\$d", specialMult.toString())
                .replace("%1\$s", baseFormula)
        } else {
            baseFormula
        }
        return RoundBasePotSummary(
            isThreeFuck = false,
            formulaText = formulaText,
            potAmountText = "${formatNumber(basePot)}${texts.wonUnit}",
        )
    }

    private fun buildSpecialLabel(
        shakeCount: Int,
        bombCount: Int,
        texts: TraceTexts,
    ): String {
        return buildList {
            if (shakeCount > 0) add(texts.shakeName)
            if (bombCount > 0) add(texts.bombName)
        }.joinToString("·")
    }

    fun toDetailSellerSummary(
        term: RoundTraceTerm?,
        gamer: Gamer?,
        texts: TraceTexts,
    ): DetailSellerSummary? {
        if (term == null || !term.factors.isSeller) return null
        val cards = gamer?.score ?: 0
        val badgeText = if (cards > 0) {
            texts.sellerBadgeCardsFormat.replace("%1\$d", cards.toString())
        } else {
            texts.sellerRoleLabel
        }
        val buyerCount = term.factors.receiverCount.coerceAtLeast(1)
        val sellAmountPerBuyer = term.factors.sellAmount.toLong() / buyerCount
        val absPerBuyer = abs(sellAmountPerBuyer)
        val unitPrice = if (cards > 0) absPerBuyer / cards else 0L
        val formulaText = if (cards > 0 && unitPrice > 0L) {
            texts.sellerFormulaCardsFormat
                .replace("%3\$d", buyerCount.toString())
                .replace("%2\$s", "${formatNumber(unitPrice)}${texts.wonUnit}")
                .replace("%1\$d", cards.toString())
        } else {
            "${formatNumber(absPerBuyer)}${texts.wonUnit} × $buyerCount${texts.personUnit}"
        }
        val receiveEachText = texts.sellerReceiveEachFormat
            .replace("%2\$d", buyerCount.toString())
            .replace("%1\$s", "${formatNumber(absPerBuyer)}${texts.wonUnit}")
        return DetailSellerSummary(
            badgeText = badgeText,
            formulaText = formulaText,
            receiveEachText = receiveEachText,
        )
    }

    fun toDetailCardBreakdown(
        term: RoundTraceTerm,
        gamer: Gamer?,
        trace: RoundTrace?,
        texts: TraceTexts,
        gamers: List<Gamer> = emptyList(),
    ): List<DetailBreakdownRow> {
        if (term.factors.isSeller) {
            val nonSellNet = (term.amount.toLong() - term.factors.sellAmount.toLong()).toInt()
            if (nonSellNet == 0) return emptyList()
            val normalizedTerm = term.copy(
                amount = nonSellNet,
                factors = term.factors.copy(isSeller = false, sellAmount = 0),
            )
            return toDetailCardBreakdown(normalizedTerm, gamer, trace, texts, gamers)
        }

        val isWinner = (trace != null && term.gamerId == trace.winnerId) ||
            TraceTag.WINNER in term.traceTags
        val fuckLabel = if (term.factors.fuckAmount != 0) {
            resolveFuckLabel(gamer, term, trace, gamers, texts)
        } else {
            texts.fuckName
        }

        return if (isWinner) {
            buildWinnerBreakdownRows(term, trace, fuckLabel, texts)
        } else {
            buildLoserBreakdownRows(term, trace, fuckLabel, texts)
        }
    }

    private fun buildWinnerBreakdownRows(
        term: RoundTraceTerm,
        trace: RoundTrace?,
        fuckLabel: String,
        texts: TraceTexts,
    ): List<DetailBreakdownRow> {
        val rows = mutableListOf<DetailBreakdownRow>()
        val factors = term.factors
        val baseAmount = (term.amount.toLong() - factors.fuckAmount - factors.ddadakAmount - factors.sellAmount).toInt()

        if (baseAmount != 0) {
            val goBakPayers = trace?.terms
                ?.filter { it.factors.coveredLosers.isNotEmpty() || it.factors.goBakCoveredAmount != 0 }
                .orEmpty()
            val label = if (goBakPayers.isNotEmpty()) {
                val payerNames = goBakPayers.joinToString(", ") { it.playerName.ifBlank { it.label } }
                texts.breakdownRoundWinGoBakFormat.replace("%1\$s", payerNames)
            } else {
                texts.breakdownRoundWin
            }
            rows.add(
                DetailBreakdownRow(
                    label = label,
                    amountText = formatSignedPlain(baseAmount, texts),
                    amountValue = baseAmount,
                    tone = BreakdownRowTone.WIN,
                )
            )
        }

        when {
            factors.fuckAmount > 0 -> {
                val payerCount = payerCountFromTrace(trace, term.gamerId) { it.factors.fuckAmount < 0 }
                rows.add(buildBonusRow(fuckLabel, factors.fuckAmount, payerCount, factors.receiverCount, texts))
            }
            factors.fuckAmount < 0 -> rows.add(
                DetailBreakdownRow(
                    label = fuckLabel,
                    amountText = formatSignedPlain(factors.fuckAmount, texts),
                    amountValue = factors.fuckAmount,
                    tone = BreakdownRowTone.LOSS_DEFAULT,
                )
            )
        }

        when {
            factors.ddadakAmount > 0 -> {
                val payerCount = payerCountFromTrace(trace, term.gamerId) { it.factors.ddadakAmount < 0 }
                rows.add(
                    buildBonusRow(texts.firstDdadakName, factors.ddadakAmount, payerCount, factors.receiverCount, texts)
                )
            }
            factors.ddadakAmount < 0 -> rows.add(
                DetailBreakdownRow(
                    label = texts.firstDdadakName,
                    amountText = formatSignedPlain(factors.ddadakAmount, texts),
                    amountValue = factors.ddadakAmount,
                    tone = BreakdownRowTone.LOSS_DEFAULT,
                )
            )
        }

        if (factors.sellAmount != 0) {
            val sellerNames = trace?.terms
                ?.filter { it.factors.isSeller }
                ?.map { it.playerName.ifBlank { it.label } }
                ?.filter { it.isNotBlank() }
                .orEmpty()
            val sellLabel = if (factors.sellAmount < 0 && sellerNames.isNotEmpty()) {
                texts.breakdownSellPayFormat.replace("%1\$s", sellerNames.joinToString(", "))
            } else {
                texts.sellName
            }
            rows.add(
                DetailBreakdownRow(
                    label = sellLabel,
                    amountText = formatSignedPlain(factors.sellAmount, texts),
                    amountValue = factors.sellAmount,
                    tone = if (factors.sellAmount > 0) BreakdownRowTone.BONUS_POSITIVE else BreakdownRowTone.LOSS_DEFAULT,
                )
            )
        }

        return rows
    }

    private fun buildLoserBreakdownRows(
        term: RoundTraceTerm,
        trace: RoundTrace?,
        fuckLabel: String,
        texts: TraceTexts,
    ): List<DetailBreakdownRow> {
        val rows = mutableListOf<DetailBreakdownRow>()
        val factors = term.factors
        val isGoBakExempt = TraceTag.GO_BAK_EXEMPT in term.traceTags || factors.goBakExemptedAmount != 0
        val isSoloGoBakPayer = factors.coveredLosers.isNotEmpty() || factors.goBakCoveredAmount != 0
        val loserCount = resolveLoserCount(trace)

        when {
            isGoBakExempt -> {
                val payerName = factors.goBakPayerName.ifBlank { texts.goBakLabel }
                rows.add(
                    DetailBreakdownRow(
                        label = texts.breakdownGoBakExemptFormat.replace("%1\$s", payerName),
                        amountText = "0${texts.wonUnit}",
                        amountValue = 0,
                        tone = BreakdownRowTone.EXEMPT,
                    )
                )
            }
            isSoloGoBakPayer -> {
                if (factors.selfBaseLoss != 0) {
                    rows.add(buildOwnBaseRow(factors.selfBaseLoss, factors, loserCount, texts))
                }
                if (factors.coveredLosers.isNotEmpty()) {
                    factors.coveredLosers.forEach { covered ->
                        val covMult = 1L shl covered.bakCount.coerceAtLeast(0).coerceAtMost(30)
                        rows.add(
                            DetailBreakdownRow(
                                label = texts.breakdownGoBakCoverFormat
                                    .replace("%2\$d", covMult.toString())
                                    .replace("%1\$s", covered.playerName),
                                amountText = formatSignedPlain(covered.amount, texts),
                                amountValue = covered.amount,
                                tone = BreakdownRowTone.GO_BAK_COVER,
                            )
                        )
                    }
                } else if (factors.goBakCoveredAmount != 0) {
                    rows.add(
                        DetailBreakdownRow(
                            label = texts.goBakLabel,
                            amountText = formatSignedPlain(factors.goBakCoveredAmount, texts),
                            amountValue = factors.goBakCoveredAmount,
                            tone = BreakdownRowTone.GO_BAK_COVER,
                        )
                    )
                }
            }
            else -> {
                val auxSum = factors.fuckAmount.toLong() + factors.ddadakAmount.toLong() + factors.sellAmount.toLong()
                val baseLoss = (term.amount.toLong() - auxSum).toInt()
                if (baseLoss != 0) {
                    rows.add(buildOwnBaseRow(baseLoss, factors, loserCount, texts))
                }
            }
        }

        if (factors.fuckAmount > 0) {
            val payerCount = payerCountFromTrace(trace, term.gamerId) { it.factors.fuckAmount < 0 }
            rows.add(buildBonusRow(fuckLabel, factors.fuckAmount, payerCount, factors.receiverCount, texts))
        }
        if (factors.ddadakAmount > 0) {
            val payerCount = payerCountFromTrace(trace, term.gamerId) { it.factors.ddadakAmount < 0 }
            rows.add(buildBonusRow(texts.firstDdadakName, factors.ddadakAmount, payerCount, factors.receiverCount, texts))
        }
        if (factors.sellAmount > 0) {
            val payerCount = payerCountFromTrace(trace, term.gamerId) { it.factors.sellAmount < 0 }
            rows.add(buildBonusRow(texts.sellName, factors.sellAmount, payerCount, factors.receiverCount, texts))
        }

        val feeParts = mutableListOf<String>()
        var feeTotal = 0L
        if (factors.fuckAmount < 0) {
            feeParts.add("$fuckLabel (${formatSignedPlain(factors.fuckAmount, texts)})")
            feeTotal += factors.fuckAmount
        }
        if (factors.ddadakAmount < 0) {
            feeParts.add("${texts.firstDdadakName} (${formatSignedPlain(factors.ddadakAmount, texts)})")
            feeTotal += factors.ddadakAmount
        }
        if (factors.sellAmount < 0) {
            feeParts.add("${texts.sellName} (${formatSignedPlain(factors.sellAmount, texts)})")
            feeTotal += factors.sellAmount
        }
        if (feeParts.isNotEmpty()) {
            val feeTotalInt = feeTotal.toInt()
            rows.add(
                DetailBreakdownRow(
                    label = feeParts.joinToString(" · "),
                    amountText = formatSignedPlain(feeTotalInt, texts),
                    amountValue = feeTotalInt,
                    tone = BreakdownRowTone.LOSS_DEFAULT,
                )
            )
        }

        return rows
    }

    private fun buildOwnBaseRow(
        amount: Int,
        factors: CalculationFactors,
        loserCount: Int,
        texts: TraceTexts,
    ): DetailBreakdownRow {
        val mult = 1L shl factors.bakCount.coerceAtLeast(0).coerceAtMost(30)
        val potPerLoser = calculateBasePotAmount(factors, loserCount)
        val potValue = if (potPerLoser > 0L) potPerLoser else abs(amount.toLong()) / mult.coerceAtLeast(1L)
        val potStr = "${formatNumber(potValue)}${texts.wonUnit}"
        val label = if (mult > 1L) {
            texts.breakdownOwnBaseMultFormat
                .replace("%2\$d", mult.toString())
                .replace("%1\$s", potStr)
        } else {
            texts.breakdownOwnBaseFormat.replace("%1\$s", potStr)
        }
        return DetailBreakdownRow(
            label = label,
            amountText = formatSignedPlain(amount, texts),
            amountValue = amount,
            tone = BreakdownRowTone.LOSS_DEFAULT,
        )
    }

    private fun buildBonusRow(
        bonusName: String,
        totalAmount: Int,
        tracedPayerCount: Int,
        fallbackReceiverCount: Int,
        texts: TraceTexts,
    ): DetailBreakdownRow {
        val payerCount = when {
            tracedPayerCount > 0 -> tracedPayerCount
            fallbackReceiverCount > 1 -> fallbackReceiverCount
            else -> 1
        }
        val perPayer = totalAmount.toLong() / payerCount
        val label = texts.breakdownBonusPerPlayerFormat
            .replace("%3\$d", payerCount.toString())
            .replace("%2\$s", "${formatNumber(perPayer)}${texts.wonUnit}")
            .replace("%1\$s", bonusName)
        return DetailBreakdownRow(
            label = label,
            amountText = formatSignedPlain(totalAmount, texts),
            amountValue = totalAmount,
            tone = BreakdownRowTone.BONUS_POSITIVE,
        )
    }

    private inline fun payerCountFromTrace(
        trace: RoundTrace?,
        receiverGamerId: Long,
        predicate: (RoundTraceTerm) -> Boolean,
    ): Int {
        if (trace == null) return 0
        return trace.terms.count { it.gamerId != receiverGamerId && !it.factors.isSeller && predicate(it) }
    }

    private fun resolveFuckLabel(
        gamer: Gamer?,
        term: RoundTraceTerm,
        trace: RoundTrace?,
        gamers: List<Gamer>,
        texts: TraceTexts,
    ): String {
        val ownOptions = gamer?.scoreOption.orEmpty()
        if (term.factors.fuckAmount > 0) {
            return when {
                TraceTag.THREE_FUCK in term.traceTags || ScoreOption.ThreeFuck in ownOptions -> texts.threeFuckLabel
                ScoreOption.SecondFuck in ownOptions -> texts.secondFuckName
                ScoreOption.FirstFuck in ownOptions -> texts.firstFuckName
                else -> texts.fuckName
            }
        }
        val fuckEarners = trace?.terms
            ?.filter { it.factors.fuckAmount > 0 }
            .orEmpty()
        val fuckEarnerIds = fuckEarners.mapTo(mutableSetOf()) { it.gamerId }
        val relevantGamers = if (fuckEarnerIds.isNotEmpty()) {
            gamers.filter { it.id in fuckEarnerIds }
        } else {
            gamers
        }
        val earnerOptions = relevantGamers
            .asSequence()
            .flatMap { it.scoreOption.asSequence() }
            .filter {
                it == ScoreOption.FirstFuck || it == ScoreOption.SecondFuck || it == ScoreOption.ThreeFuck
            }
            .distinct()
            .toList()
        if (earnerOptions.isEmpty() && fuckEarners.isNotEmpty() && fuckEarners.all { TraceTag.THREE_FUCK in it.traceTags }) {
            return texts.threeFuckLabel
        }
        return when {
            earnerOptions.size == 1 && earnerOptions.first() == ScoreOption.ThreeFuck -> texts.threeFuckLabel
            earnerOptions.size == 1 && earnerOptions.first() == ScoreOption.SecondFuck -> texts.secondFuckName
            earnerOptions.size == 1 && earnerOptions.first() == ScoreOption.FirstFuck -> texts.firstFuckName
            else -> texts.fuckName
        }
    }

    private fun resolveLoserCount(trace: RoundTrace?): Int {
        if (trace == null) return 1
        return trace.terms.count { !it.factors.isSeller && it.gamerId != trace.winnerId }
            .coerceAtLeast(1)
    }

    private fun calculateBasePotAmount(
        factors: CalculationFactors,
        loserCount: Int = 1,
    ): Long {
        val rawBase = (factors.baseScore.toLong() + factors.goCount).coerceAtLeast(0L)
        if (rawBase == 0L || factors.scorePerPoint <= 0) return 0L
        val maxPerLoser = calculateMaxPerLoser(loserCount)
        val goMult = if (factors.goCount >= 3) 1L shl (factors.goCount - 2).coerceAtMost(30) else 1L
        val specialMult = calculateShakeBombMultiplier(factors.shakeCount, factors.bombCount).toLong()
        val scorePts = factors.scorePerPoint.toLong()
        val withUnit = if (rawBase > maxPerLoser / scorePts) {
            maxPerLoser
        } else {
            rawBase * scorePts
        }
        val withGo = if (withUnit > maxPerLoser / goMult) {
            maxPerLoser
        } else {
            withUnit * goMult
        }
        return if (withGo > maxPerLoser / specialMult) {
            maxPerLoser
        } else {
            withGo * specialMult
        }
    }

    private fun formatSignedPlain(value: Int, texts: TraceTexts): String {
        val sign = when {
            value > 0 -> "+"
            value < 0 -> "-"
            else -> ""
        }
        val amount = abs(value.toLong()).separateComma()
        return "$sign$amount${texts.wonUnit}"
    }

    fun formatAmount(value: Int, texts: TraceTexts): AnnotatedString = buildAmount(value, texts)

    private fun buildAmount(value: Int, texts: TraceTexts): AnnotatedString {
        val sign = when {
            value > 0 -> "+"
            value < 0 -> "-"
            else -> ""
        }
        val amount = abs(value.toLong()).separateComma()
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
        return "$sign ${formatNumber(abs(value.toLong()))}"
    }
}
