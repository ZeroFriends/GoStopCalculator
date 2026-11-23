package zero.friends.gostopcalculator.ui.board.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import zero.friends.domain.model.CalculationFactors
import zero.friends.domain.model.CoveredLoserDetail
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.model.TraceTag

class RoundTraceFormatterTest {

    private val formatter = RoundTraceFormatter()

    private val sampleTexts = RoundTraceFormatter.TraceTexts(
        baseScoreTitle = "Base Score",
        baseLabel = "Base",
        pointUnit = "pts",
        goUnit = "Go",
        bakUnit = "bak",
        wonUnit = "won",
        personUnit = "p",
        fuckName = "Ppeok",
        firstDdadakName = "First Ddadak",
        sellName = "Sell Brights",
        winnerLabel = "Winner",
        loserLabel = "Loser",
        sellerRoleLabel = "Seller",
        threeFuckLabel = "Triple Ppeok",
        peaBakLabel = "Pi-bak",
        lightBakLabel = "Gwang-bak",
        mongBakLabel = "Meong-bak",
        goBakLabel = "Go-bak"
    )

    @Test
    fun `toLines preserves 0-net participant when auxiliary factor is non-zero`() {
        val term = RoundTraceTerm(
            gamerId = 2L,
            label = "Player 2",
            amount = 0,
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 4,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = 400,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1
            ),
            playerName = "Player 2",
            traceTags = listOf(TraceTag.THREE_FUCK, TraceTag.LOSER, TraceTag.PEA_BAK)
        )

        val lines = formatter.toLines(listOf(term), sampleTexts)
        assertEquals(1, lines.size)
        assertEquals("Player 2\n(Triple Ppeok, Loser, Pi-bak)", lines.single().title)
        assertEquals("0won", lines.single().amount.text)
    }

    @Test
    fun `toLines formats multi-digit superscript exponents for goCount 12 and 13`() {
        val term12Go = RoundTraceTerm(
            gamerId = 1L,
            label = "Winner",
            amount = 102400,
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 3,
                goCount = 12,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = 0,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1
            ),
            playerName = "Winner",
            traceTags = listOf(TraceTag.WINNER)
        )
        val term13Go = term12Go.copy(
            factors = term12Go.factors.copy(goCount = 13)
        )

        val lines = formatter.toLines(listOf(term12Go, term13Go), sampleTexts)
        assertTrue(lines[0].formula.text.contains("x 2¹⁰(12Go)"))
        assertTrue(lines[1].formula.text.contains("x 2¹¹(13Go)"))
    }

    @Test
    fun `toLines formats 3P GoBak solo payer and exempted loser with exact arithmetic parity`() {
        val soloPayerTerm = RoundTraceTerm(
            gamerId = 2L,
            label = "Player 2",
            amount = -5000,
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 2,
                goCount = 3,
                scorePerPoint = 100,
                bakCount = 2,
                fuckAmount = 0,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
                selfBaseLoss = -4000,
                goBakCoveredAmount = -1000,
                isSoloGoBakMode = true,
                coveredLosers = listOf(CoveredLoserDetail(playerName = "Player 3", amount = -1000, bakCount = 0))
            ),
            playerName = "Player 2",
            traceTags = listOf(TraceTag.LOSER, TraceTag.LIGHT_BAK, TraceTag.MONG_BAK, TraceTag.GO_BAK)
        )

        val exemptedTerm = RoundTraceTerm(
            gamerId = 3L,
            label = "Player 3",
            amount = 0,
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 2,
                goCount = 3,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = 0,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
                goBakExemptedAmount = -1000,
                goBakPayerName = "Player 2",
                isSoloGoBakMode = true
            ),
            playerName = "Player 3",
            traceTags = listOf(TraceTag.LOSER, TraceTag.GO_BAK_EXEMPT)
        )

        val lines = formatter.toLines(listOf(soloPayerTerm, exemptedTerm), sampleTexts)
        assertEquals(2, lines.size)

        val payerFormula = lines[0].formula.text
        assertTrue(payerFormula.contains("x 2²(2bak)"))
        assertTrue(payerFormula.contains("Own base: -4,000won"))
        assertTrue(payerFormula.contains("- 1,000won (Player 3 Go-bak cover)"))
        assertTrue(payerFormula.endsWith("= -5,000won"))
        assertEquals("-5,000won", lines[0].amount.text)

        val exemptedFormula = lines[1].formula.text
        assertTrue(lines[1].title.contains("Go-bak Exempt"))
        assertTrue(exemptedFormula.contains("0won (Exempted (Player 2 Go-bak))"))
        assertEquals("0won", lines[1].amount.text)
    }

    @Test
    fun `formatAmount formats Int MIN_VALUE safely without double negative sign`() {
        val formatted = formatter.formatAmount(Int.MIN_VALUE, sampleTexts).text
        assertEquals("-2,147,483,648won", formatted)
    }
}
