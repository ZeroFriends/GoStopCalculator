package zero.friends.domain.usecase.calculate.sub

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import zero.friends.data.util.SerializationConverters
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.TraceTag
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.usecase.calculate.BuildRoundTraceUseCase
import zero.friends.domain.usecase.calculate.CalculateLoserScoreUseCase
import zero.friends.domain.util.Const

class BuildRoundTraceUseCaseTest {

    private lateinit var useCase: BuildRoundTraceUseCase
    private lateinit var ruleRepository: MockRuleRepository
    private lateinit var gamerRepository: MockGamerRepository

    @BeforeTest
    fun setup() {
        ruleRepository = MockRuleRepository()
        gamerRepository = MockGamerRepository()
        val calculateLoserScoreUseCase = CalculateLoserScoreUseCase(ruleRepository, gamerRepository)
        useCase = BuildRoundTraceUseCase(ruleRepository, gamerRepository, calculateLoserScoreUseCase)
    }

    @Test
    fun `loser holding ThreeFuck retains LOSER and bak trace tags`() = runTest {
        val gameId = 1L
        val roundId = 1L
        val winner = Gamer(
            id = 1,
            roundId = roundId,
            name = "Winner",
            score = 3,
            winnerOption = WinnerOption.Winner
        )
        val loser = Gamer(
            id = 2,
            roundId = roundId,
            name = "Loser",
            scoreOption = listOf(ScoreOption.ThreeFuck),
            loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak)
        )

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = 100),
                Rule(name = Const.Rule.Fuck, score = 100)
            )
        )

        val trace = useCase(gameId, roundId)
        assertNotNull(trace)
        val loserTerm = trace.terms.first { it.gamerId == loser.id }
        assertEquals("Loser", loserTerm.label)
        assertTrue(loserTerm.traceTags.contains(TraceTag.THREE_FUCK))
        assertTrue(loserTerm.traceTags.contains(TraceTag.LOSER))
        assertTrue(loserTerm.traceTags.contains(TraceTag.PEA_BAK))
        assertTrue(loserTerm.traceTags.contains(TraceTag.LIGHT_BAK))
    }

    @Test
    fun `zero net contributing loser is retained in trace terms`() = runTest {
        val gameId = 1L
        val roundId = 1L
        // Base payment from Loser1 to Winner: 4 pts * 100 won = -400
        // FirstFuck received by Loser1 from Winner & Loser2 (2 opponents * 200 won) = +400 -> net 0
        val winner = Gamer(
            id = 1,
            roundId = roundId,
            name = "P1",
            score = 4,
            winnerOption = WinnerOption.Winner
        )
        val loserZeroNet = Gamer(
            id = 2,
            roundId = roundId,
            name = "P2",
            scoreOption = listOf(ScoreOption.FirstFuck)
        )
        val loser2 = Gamer(
            id = 3,
            roundId = roundId,
            name = "P3"
        )

        gamerRepository.setRoundGamers(roundId, listOf(winner, loserZeroNet, loser2))
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = 100),
                Rule(name = Const.Rule.Fuck, score = 200)
            )
        )

        val trace = useCase(gameId, roundId)
        assertNotNull(trace)
        val term = trace.terms.firstOrNull { it.gamerId == loserZeroNet.id }
        assertNotNull(term)
        assertEquals(0, term.amount)
        assertEquals(400, term.factors.fuckAmount)
    }

    @Test
    fun `3인 이상 고박 독박 시 0원 면제 패자도 트레이스에 보존되고 대납 내역이 정확히 분리된다`() = runTest {
        val gameId = 1L
        val roundId = 10L
        val seller = Gamer(id = 1, roundId = roundId, name = "P1", score = 2, sellerOption = SellerOption.Seller)
        val winner = Gamer(id = 2, roundId = roundId, name = "P2", score = 2, go = 3, winnerOption = WinnerOption.Winner)
        val goBakPayer = Gamer(
            id = 3,
            roundId = roundId,
            name = "P3",
            loserOption = listOf(LoserOption.MongBak, LoserOption.LightBak, LoserOption.GoBak)
        )
        val exemptedLoser = Gamer(id = 4, roundId = roundId, name = "P4")

        gamerRepository.setRoundGamers(roundId, listOf(seller, winner, goBakPayer, exemptedLoser))
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = 100),
                Rule(name = Const.Rule.Sell, score = 0)
            )
        )

        val trace = useCase(gameId, roundId)
        assertNotNull(trace)
        assertEquals(5000, trace.totalAmount)

        val payerTerm = trace.terms.first { it.gamerId == 3L }
        assertEquals(-5000, payerTerm.amount)
        assertEquals(2, payerTerm.factors.bakCount)
        assertEquals(-4000, payerTerm.factors.selfBaseLoss)
        assertEquals(-1000, payerTerm.factors.goBakCoveredAmount)
        assertEquals("P4", payerTerm.factors.coveredLosers.single().playerName)

        val exemptedTerm = trace.terms.firstOrNull { it.gamerId == 4L }
        assertNotNull(exemptedTerm)
        assertEquals(0, exemptedTerm.amount)
        assertEquals(-1000, exemptedTerm.factors.goBakExemptedAmount)
        assertEquals("P3", exemptedTerm.factors.goBakPayerName)
        assertTrue(exemptedTerm.traceTags.contains(TraceTag.GO_BAK_EXEMPT))
    }

    @Test
    fun `구버전 직렬화 JSON 스냅샷도 신규 필드가 추가된 RoundTraceTerm으로 정상 역직렬화된다`() {
        val converters = SerializationConverters()
        val legacyJson = """
            [
              {
                "gamerId": 2,
                "label": "플레이어 2",
                "amount": -2000,
                "factors": {
                  "isSeller": false,
                  "baseScore": 5,
                  "goCount": 0,
                  "scorePerPoint": 100,
                  "bakCount": 2,
                  "fuckAmount": 0,
                  "ddadakAmount": 0,
                  "sellAmount": 0,
                  "receiverCount": 1
                },
                "playerName": "플레이어 2",
                "traceTags": ["LOSER", "PEA_BAK", "LIGHT_BAK"]
              }
            ]
        """.trimIndent()

        val decoded = converters.stringToRoundTraceTerms(legacyJson)
        assertEquals(1, decoded.size)
        assertEquals(-2000, decoded.single().amount)
        assertEquals(0, decoded.single().factors.goBakCoveredAmount)
        assertEquals(false, decoded.single().factors.isSoloGoBakMode)
    }

    @Test
    fun `v1_4_x 구규칙으로 DB에 저장된 3인 고박 라운드 상세 조회 시 DB 정산액과 일치하는 트레이스로 자동 폴백한다`() = runTest {
        val gameId = 1L
        val roundId = 11L
        // v1.4.x 구규칙: 4점(400원), loser1 피박(800원), goBakPayer 고박(400*2=800원) => DB에 +1600 / 0 / -1600 저장
        val winner = Gamer(
            id = 1,
            roundId = roundId,
            name = "승자",
            score = 4,
            account = 1600,
            winnerOption = WinnerOption.Winner,
        )
        val exemptedLoser = Gamer(
            id = 2,
            roundId = roundId,
            name = "피박자",
            account = 0,
            loserOption = listOf(LoserOption.PeaBak),
        )
        val goBakPayer = Gamer(
            id = 3,
            roundId = roundId,
            name = "고박자",
            account = -1600,
            loserOption = listOf(LoserOption.GoBak),
        )

        gamerRepository.setRoundGamers(roundId, listOf(winner, exemptedLoser, goBakPayer))
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val trace = useCase(gameId, roundId)
        assertNotNull(trace)
        assertEquals(1600, trace.totalAmount)
        val payerTerm = trace.terms.first { it.gamerId == goBakPayer.id }
        assertEquals(-1600, payerTerm.amount)
        assertEquals(-800, payerTerm.factors.selfBaseLoss)
        assertEquals(-800, payerTerm.factors.goBakCoveredAmount)
        assertEquals(1, payerTerm.factors.bakCount)
    }
}
