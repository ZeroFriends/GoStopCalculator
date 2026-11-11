package zero.friends.domain.usecase.calculate.sub

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
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
}
