package zero.friends.gostopcalculator.ui.board.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import zero.friends.domain.model.CalculationFactors
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
}
