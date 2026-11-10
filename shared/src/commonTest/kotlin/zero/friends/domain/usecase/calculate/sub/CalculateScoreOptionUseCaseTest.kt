package zero.friends.domain.usecase.calculate.sub

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.usecase.calculate.CalculateScoreOptionUseCase
import zero.friends.domain.util.Const

class CalculateScoreOptionUseCaseTest {

    private lateinit var useCase: CalculateScoreOptionUseCase
    private lateinit var ruleRepository: MockRuleRepository
    private lateinit var gamerRepository: MockGamerRepository

    @BeforeTest
    fun setup() {
        ruleRepository = MockRuleRepository()
        gamerRepository = MockGamerRepository()
        useCase = CalculateScoreOptionUseCase(ruleRepository, gamerRepository)
    }

    @Test
    fun `승자의 뻑 및 따닥 옵션 금액 정산 결과를 반환한다`() = runTest {
        val gameId = 1L
        val roundId = 1L

        val winner = Gamer(
            id = 1,
            roundId = roundId,
            scoreOption = listOf(ScoreOption.FirstFuck, ScoreOption.FirstDdadak)
        )
        val p2 = Gamer(id = 2, roundId = roundId)
        val p3 = Gamer(id = 3, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(winner, p2, p3))
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = 100),
                Rule(name = Const.Rule.Fuck, score = 500)
            )
        )

        val result = useCase(gameId, roundId, seller = null)

        assertEquals(-800, result.accounts[2])
        assertEquals(-800, result.accounts[3])
        assertEquals(1600, result.accounts[1])
    }
}
