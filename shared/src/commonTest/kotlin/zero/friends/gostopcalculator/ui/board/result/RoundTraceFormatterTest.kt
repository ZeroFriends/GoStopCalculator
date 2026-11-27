package zero.friends.gostopcalculator.ui.board.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import zero.friends.domain.model.CalculationFactors
import zero.friends.domain.model.CoveredLoserDetail
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.TraceTag
import zero.friends.domain.model.WinnerOption

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

    @Test
    fun `detail breakdown matches 4P Stitch17 GoBak solo FirstFuck and Seller scenario`() {
        val p1Gamer = Gamer(
            id = 1L,
            name = "Player 1",
            score = 2,
            go = 3,
            account = 5400,
            winnerOption = WinnerOption.Winner,
            scoreOption = listOf(ScoreOption.FirstFuck),
        )
        val p2Gamer = Gamer(
            id = 2L,
            name = "Player 2",
            account = -5500,
            loserOption = listOf(LoserOption.LightBak, LoserOption.MongBak, LoserOption.GoBak),
        )
        val p3Gamer = Gamer(
            id = 3L,
            name = "Player 3",
            account = -500,
        )
        val p4Gamer = Gamer(
            id = 4L,
            name = "Player 4",
            score = 2,
            account = 600,
            sellerOption = SellerOption.Seller,
        )
        val gamers = listOf(p1Gamer, p2Gamer, p3Gamer, p4Gamer)

        val p1Term = RoundTraceTerm(
            gamerId = 1L,
            label = "Player 1",
            playerName = "Player 1",
            amount = 5400,
            traceTags = listOf(TraceTag.WINNER),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 2,
                goCount = 3,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = 600,
                ddadakAmount = 0,
                sellAmount = -200,
                receiverCount = 2,
            ),
        )
        val p2Term = RoundTraceTerm(
            gamerId = 2L,
            label = "Player 2",
            playerName = "Player 2",
            amount = -5500,
            traceTags = listOf(TraceTag.LOSER, TraceTag.LIGHT_BAK, TraceTag.MONG_BAK, TraceTag.GO_BAK),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 2,
                goCount = 3,
                scorePerPoint = 100,
                bakCount = 2,
                fuckAmount = -300,
                ddadakAmount = 0,
                sellAmount = -200,
                receiverCount = 1,
                selfBaseLoss = -4000,
                goBakCoveredAmount = -1000,
                isSoloGoBakMode = true,
                coveredLosers = listOf(CoveredLoserDetail(playerName = "Player 3", amount = -1000, bakCount = 0)),
            ),
        )
        val p3Term = RoundTraceTerm(
            gamerId = 3L,
            label = "Player 3",
            playerName = "Player 3",
            amount = -500,
            traceTags = listOf(TraceTag.LOSER, TraceTag.GO_BAK_EXEMPT),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 2,
                goCount = 3,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = -300,
                ddadakAmount = 0,
                sellAmount = -200,
                receiverCount = 1,
                goBakExemptedAmount = -1000,
                goBakPayerName = "Player 2",
                isSoloGoBakMode = true,
            ),
        )
        val p4Term = RoundTraceTerm(
            gamerId = 4L,
            label = "Player 4",
            playerName = "Player 4",
            amount = 600,
            traceTags = listOf(TraceTag.SELLER),
            factors = CalculationFactors(
                isSeller = true,
                baseScore = 0,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = 0,
                ddadakAmount = 0,
                sellAmount = 600,
                receiverCount = 3,
            ),
        )
        val trace = RoundTrace(
            roundId = 1L,
            gameId = 1L,
            winnerId = 1L,
            terms = listOf(p1Term, p2Term, p3Term, p4Term),
            totalAmount = 6000,
        )

        val potSummary = formatter.toRoundBasePotSummary(trace, sampleTexts)
        assertNotNull(potSummary)
        assertEquals(false, potSummary.isThreeFuck)
        assertEquals("(Base 2pts + 3Go) × 100won × 2x(3Go)", potSummary.formulaText)
        assertEquals("1,000won", potSummary.potAmountText)

        val winnerRows = formatter.toDetailCardBreakdown(p1Term, p1Gamer, trace, sampleTexts, gamers)
        assertEquals(3, winnerRows.size)
        assertEquals("Round Win (Player 2 Go-bak solo)", winnerRows[0].label)
        assertEquals("+5,000won", winnerRows[0].amountText)
        assertEquals(RoundTraceFormatter.BreakdownRowTone.WIN, winnerRows[0].tone)
        assertEquals("First Ppeok bonus (300won × 2)", winnerRows[1].label)
        assertEquals("+600won", winnerRows[1].amountText)
        assertEquals("Sell Brights paid (Player 4)", winnerRows[2].label)
        assertEquals("-200won", winnerRows[2].amountText)
        assertEquals(p1Term.amount, winnerRows.sumOf { it.amountValue })

        val payerRows = formatter.toDetailCardBreakdown(p2Term, p2Gamer, trace, sampleTexts, gamers)
        assertEquals(3, payerRows.size)
        assertEquals("Own base (1,000won × 4x)", payerRows[0].label)
        assertEquals("-4,000won", payerRows[0].amountText)
        assertEquals("⚡ Player 3 Go-bak cover (1x)", payerRows[1].label)
        assertEquals("-1,000won", payerRows[1].amountText)
        assertEquals(RoundTraceFormatter.BreakdownRowTone.GO_BAK_COVER, payerRows[1].tone)
        assertEquals("First Ppeok (-300won) · Sell Brights (-200won)", payerRows[2].label)
        assertEquals("-500won", payerRows[2].amountText)
        assertEquals(p2Term.amount, payerRows.sumOf { it.amountValue })

        val exemptRows = formatter.toDetailCardBreakdown(p3Term, p3Gamer, trace, sampleTexts, gamers)
        assertEquals(2, exemptRows.size)
        assertEquals("Base score exempted (Player 2 Go-bak)", exemptRows[0].label)
        assertEquals("0won", exemptRows[0].amountText)
        assertEquals(RoundTraceFormatter.BreakdownRowTone.EXEMPT, exemptRows[0].tone)
        assertEquals("First Ppeok (-300won) · Sell Brights (-200won)", exemptRows[1].label)
        assertEquals("-500won", exemptRows[1].amountText)
        assertEquals(p3Term.amount, exemptRows.sumOf { it.amountValue })

        val sellerSummary = formatter.toDetailSellerSummary(p4Term, p4Gamer, sampleTexts)
        assertNotNull(sellerSummary)
        assertEquals("Seller · 2", sellerSummary.badgeText)
        assertEquals("2 cards × 100won × 3", sellerSummary.formulaText)
        assertEquals("+200won each from 3 players", sellerSummary.receiveEachText)
        assertTrue(formatter.toDetailCardBreakdown(p4Term, p4Gamer, trace, sampleTexts, gamers).isEmpty())
    }

    @Test
    fun `detail breakdown handles 2P Matgo GoBak as x2 multiplier without cover row`() {
        val winner = Gamer(id = 1L, name = "Player 1", score = 7, account = 1400, winnerOption = WinnerOption.Winner)
        val loser = Gamer(id = 2L, name = "Player 2", account = -1400, loserOption = listOf(LoserOption.GoBak))

        val winnerTerm = RoundTraceTerm(
            gamerId = 1L,
            label = "Player 1",
            playerName = "Player 1",
            amount = 1400,
            traceTags = listOf(TraceTag.WINNER),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 7,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = 0,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
            ),
        )
        val loserTerm = RoundTraceTerm(
            gamerId = 2L,
            label = "Player 2",
            playerName = "Player 2",
            amount = -1400,
            traceTags = listOf(TraceTag.LOSER, TraceTag.GO_BAK),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 7,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 1,
                fuckAmount = 0,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
                isSoloGoBakMode = false,
            ),
        )
        val trace = RoundTrace(
            roundId = 1L,
            gameId = 1L,
            winnerId = 1L,
            terms = listOf(winnerTerm, loserTerm),
            totalAmount = 1400,
        )

        val potSummary = formatter.toRoundBasePotSummary(trace, sampleTexts)
        assertNotNull(potSummary)
        assertEquals("Base 7pts × 100won", potSummary.formulaText)
        assertEquals("700won", potSummary.potAmountText)

        val loserRows = formatter.toDetailCardBreakdown(loserTerm, loser, trace, sampleTexts, listOf(winner, loser))
        assertEquals(1, loserRows.size)
        assertEquals("Own base (700won × 2x)", loserRows[0].label)
        assertEquals("-1,400won", loserRows[0].amountText)
        assertEquals(RoundTraceFormatter.BreakdownRowTone.LOSS_DEFAULT, loserRows[0].tone)
    }

    @Test
    fun `detail breakdown handles Triple Ppeok instant win cleanly`() {
        val winnerTerm = RoundTraceTerm(
            gamerId = 1L,
            label = "Player 1",
            playerName = "Player 1",
            amount = 800,
            traceTags = listOf(TraceTag.THREE_FUCK, TraceTag.WINNER),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 0,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = 800,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 2,
            ),
        )
        val loserTerm = RoundTraceTerm(
            gamerId = 2L,
            label = "Player 2",
            playerName = "Player 2",
            amount = -400,
            traceTags = listOf(TraceTag.THREE_FUCK, TraceTag.LOSER),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 0,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = -400,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
            ),
        )
        val loserTerm2 = loserTerm.copy(gamerId = 3L, label = "Player 3", playerName = "Player 3")
        val trace = RoundTrace(
            roundId = 1L,
            gameId = 1L,
            winnerId = 1L,
            terms = listOf(winnerTerm, loserTerm, loserTerm2),
            totalAmount = 800,
        )

        val potSummary = formatter.toRoundBasePotSummary(trace, sampleTexts)
        assertNotNull(potSummary)
        assertEquals(true, potSummary.isThreeFuck)
        assertEquals(sampleTexts.threeFuckBanner, potSummary.formulaText)
        assertEquals("", potSummary.potAmountText)

        val winnerRows = formatter.toDetailCardBreakdown(winnerTerm, null, trace, sampleTexts)
        assertEquals(1, winnerRows.size)
        assertEquals("Triple Ppeok bonus (400won × 2)", winnerRows[0].label)
        assertEquals("+800won", winnerRows[0].amountText)
        assertEquals(RoundTraceFormatter.BreakdownRowTone.BONUS_POSITIVE, winnerRows[0].tone)

        val loserRows = formatter.toDetailCardBreakdown(loserTerm, null, trace, sampleTexts)
        assertEquals(1, loserRows.size)
        assertEquals("Triple Ppeok (-400won)", loserRows[0].label)
        assertEquals("-400won", loserRows[0].amountText)
    }

    @Test
    fun `loser holding ThreeFuck does not trigger instant three-fuck banner or hide base loss`() {
        val winnerTerm = RoundTraceTerm(
            gamerId = 1L,
            label = "Player 1",
            amount = 800,
            playerName = "Player 1",
            traceTags = listOf(TraceTag.WINNER),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 4,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = 0,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
            ),
        )
        val loserThreeFuckTerm = RoundTraceTerm(
            gamerId = 2L,
            label = "Player 2",
            amount = 400,
            playerName = "Player 2",
            traceTags = listOf(TraceTag.THREE_FUCK, TraceTag.LOSER),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 4,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = 800,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
            ),
        )
        val otherLoserTerm = RoundTraceTerm(
            gamerId = 3L,
            label = "Player 3",
            amount = -1200,
            playerName = "Player 3",
            traceTags = listOf(TraceTag.LOSER, TraceTag.PEA_BAK),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 4,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 1,
                fuckAmount = -400,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
            ),
        )
        val trace = RoundTrace(
            roundId = 1L,
            gameId = 1L,
            winnerId = 1L,
            terms = listOf(winnerTerm, loserThreeFuckTerm, otherLoserTerm),
            totalAmount = 1200,
        )

        val potSummary = formatter.toRoundBasePotSummary(trace, sampleTexts)
        assertNotNull(potSummary)
        assertEquals(false, potSummary.isThreeFuck)
        assertEquals("400won", potSummary.potAmountText)

        val loserRows = formatter.toDetailCardBreakdown(loserThreeFuckTerm, null, trace, sampleTexts)
        assertEquals(2, loserRows.size)
        assertEquals(-400, loserRows[0].amountValue)
        assertEquals(800, loserRows[1].amountValue)
        assertEquals(loserThreeFuckTerm.amount, loserRows.sumOf { it.amountValue })
    }

    @Test
    fun `explicit GoBak cover and exempt factors render even when isSoloGoBakMode flag is false`() {
        val payerTerm = RoundTraceTerm(
            gamerId = 2L,
            label = "Player 2",
            amount = -1200,
            playerName = "Player 2",
            traceTags = listOf(TraceTag.LOSER, TraceTag.GO_BAK),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 3,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 1,
                fuckAmount = 0,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
                isSoloGoBakMode = false,
                selfBaseLoss = -600,
                goBakCoveredAmount = -600,
                coveredLosers = listOf(
                    CoveredLoserDetail(
                        playerName = "Player 3",
                        amount = -600,
                        bakCount = 1,
                    )
                ),
            ),
        )
        val exemptTerm = RoundTraceTerm(
            gamerId = 3L,
            label = "Player 3",
            amount = 0,
            playerName = "Player 3",
            traceTags = listOf(TraceTag.LOSER, TraceTag.GO_BAK_EXEMPT),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 3,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 1,
                fuckAmount = 0,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
                isSoloGoBakMode = false,
                goBakPayerName = "Player 2",
                goBakExemptedAmount = -600,
            ),
        )

        val trace = RoundTrace(
            roundId = 1L,
            gameId = 1L,
            winnerId = 1L,
            terms = listOf(payerTerm, exemptTerm),
            totalAmount = 1200,
        )

        val lines = formatter.toLines(listOf(payerTerm, exemptTerm), sampleTexts)
        assertEquals(2, lines.size)
        assertTrue(lines[0].formula.text.contains("Player 3"))
        assertTrue(lines[1].formula.text.contains("Player 2"))

        val payerRows = formatter.toDetailCardBreakdown(payerTerm, null, trace, sampleTexts)
        assertEquals(2, payerRows.size)
        assertEquals(RoundTraceFormatter.BreakdownRowTone.GO_BAK_COVER, payerRows[1].tone)
        assertEquals(-600, payerRows[1].amountValue)

        val exemptRows = formatter.toDetailCardBreakdown(exemptTerm, null, trace, sampleTexts)
        assertEquals(1, exemptRows.size)
        assertEquals(RoundTraceFormatter.BreakdownRowTone.EXEMPT, exemptRows[0].tone)
        assertEquals(0, exemptRows[0].amountValue)
    }

    @Test
    fun `toRoundBasePotSummary returns null for zero-base non-three-fuck and clamps overflow`() {
        val zeroBaseWinner = RoundTraceTerm(
            gamerId = 1L,
            label = "Player 1",
            amount = 0,
            playerName = "Player 1",
            traceTags = listOf(TraceTag.WINNER),
            factors = CalculationFactors(
                isSeller = false,
                baseScore = 0,
                goCount = 0,
                scorePerPoint = 100,
                bakCount = 0,
                fuckAmount = 0,
                ddadakAmount = 0,
                sellAmount = 0,
                receiverCount = 1,
            ),
        )
        val zeroTrace = RoundTrace(
            roundId = 1L,
            gameId = 1L,
            winnerId = 1L,
            terms = listOf(zeroBaseWinner),
            totalAmount = 0,
        )
        assertEquals(null, formatter.toRoundBasePotSummary(zeroTrace, sampleTexts))

        val overflowWinner = zeroBaseWinner.copy(
            factors = zeroBaseWinner.factors.copy(
                baseScore = Int.MAX_VALUE,
                goCount = 5,
                scorePerPoint = Int.MAX_VALUE,
            )
        )
        val overflowTrace = RoundTrace(
            roundId = 2L,
            gameId = 1L,
            winnerId = 1L,
            terms = listOf(overflowWinner),
            totalAmount = Int.MAX_VALUE,
        )
        val overflowSummary = formatter.toRoundBasePotSummary(overflowTrace, sampleTexts)
        assertNotNull(overflowSummary)
        assertTrue(overflowSummary.potAmountText.startsWith("2,147,483,647"))
    }
}
