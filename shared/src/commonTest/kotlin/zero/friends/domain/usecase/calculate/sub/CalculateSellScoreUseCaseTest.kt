package zero.friends.domain.usecase.calculate.sub

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Rule
import zero.friends.domain.usecase.calculate.CalculateSellScoreUseCase
import zero.friends.domain.util.Const

class CalculateSellScoreUseCaseTest {

    private lateinit var useCase: CalculateSellScoreUseCase
    private lateinit var ruleRepository: MockRuleRepository
    private lateinit var gamerRepository: MockGamerRepository

    @BeforeTest
    fun setup() {
        ruleRepository = MockRuleRepository()
        gamerRepository = MockGamerRepository()
        useCase = CalculateSellScoreUseCase(ruleRepository, gamerRepository)
    }

    @Test
    fun `seller가 존재할 때 모든 게이머로부터 광값을 정산받는 계산 결과를 반환한다`() = runTest {
        val gameId = 1L
        val roundId = 1L

        val seller = Gamer(id = 1, roundId = roundId, score = 2)
        val p2 = Gamer(id = 2, roundId = roundId)
        val p3 = Gamer(id = 3, roundId = roundId)
        val p4 = Gamer(id = 4, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(seller, p2, p3, p4))
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Sell, score = 500)))

        val result = useCase(gameId, roundId, seller)

        assertEquals(-1000, result.accounts[2])
        assertEquals(-1000, result.accounts[3])
        assertEquals(-1000, result.accounts[4])
        assertEquals(3000, result.accounts[1])
    }
}
