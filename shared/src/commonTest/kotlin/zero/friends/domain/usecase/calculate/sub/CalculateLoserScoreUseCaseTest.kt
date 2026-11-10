package zero.friends.domain.usecase.calculate.sub

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Rule
import zero.friends.domain.usecase.calculate.CalculateLoserScoreUseCase
import zero.friends.domain.util.Const

class CalculateLoserScoreUseCaseTest {

    private lateinit var useCase: CalculateLoserScoreUseCase
    private lateinit var ruleRepository: MockRuleRepository
    private lateinit var gamerRepository: MockGamerRepository

    @BeforeTest
    fun setup() {
        ruleRepository = MockRuleRepository()
        gamerRepository = MockGamerRepository()
        useCase = CalculateLoserScoreUseCase(ruleRepository, gamerRepository)
    }

    @Test
    fun `기본 패자 피박 2배 계산`() = runTest {
        val gameId = 1L
        val roundId = 1L
        val winner = Gamer(id = 1, roundId = roundId, score = 5)
        val loser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val result = useCase(gameId, roundId, winner)

        // 5점 * 100원 * 2(피박) = 1,000원 지불/수령
        assertEquals(1000, result.accounts[1])
        assertEquals(-1000, result.accounts[2])
    }

    @Test
    fun `고박 발생 시 고박자 1인이 전체 패자 금액 독박 부담`() = runTest {
        val gameId = 1L
        val roundId = 1L
        val winner = Gamer(id = 1, roundId = roundId, score = 3)
        val normalLoser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))
        val goBakLoser = Gamer(id = 3, roundId = roundId, loserOption = listOf(LoserOption.GoBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, normalLoser, goBakLoser))
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val result = useCase(gameId, roundId, winner)

        assertEquals(null, result.accounts[2]) // 일반 패자는 accounts 맵에 안 들어가거나 지불금 없음
        assertEquals(-1200, result.accounts[3])
        assertEquals(1200, result.accounts[1])
    }
}
