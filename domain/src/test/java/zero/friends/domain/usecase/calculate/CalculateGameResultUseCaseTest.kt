package zero.friends.domain.usecase.calculate

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.util.Const

/**
 * 대표 시나리오 통합 테스트
 *
 * - 맞고(2인)
 * - 고스톱(3인)
 * - 3인 + seller (광팔기)
 */
class CalculateGameResultUseCaseTest {

    private lateinit var useCase: CalculateGameResultUseCase
    private lateinit var ruleRepository: MockRuleRepository
    private lateinit var gamerRepository: MockGamerRepository

    @Before
    fun setup() {
        ruleRepository = MockRuleRepository()
        gamerRepository = MockGamerRepository()

        useCase = CalculateGameResultUseCase(
            ruleRepository = ruleRepository,
            gamerRepository = gamerRepository,
            updateAccountUseCase = UpdateAccountUseCase(gamerRepository),
            calculateSellScoreUseCase = CalculateSellScoreUseCase(ruleRepository, gamerRepository),
            calculateLoserScoreUseCase = CalculateLoserScoreUseCase(ruleRepository, gamerRepository),
            calculateScoreOptionUseCase = CalculateScoreOptionUseCase(ruleRepository, gamerRepository)
        )
    }

    @Test
    fun `맞고 - 승자 연뻑과 피박`() = runTest {
        val gameId = 1L
        val roundId = 1L

        val winner = Gamer(
            id = 1,
            roundId = roundId,
            score = 6,
            scoreOption = listOf(ScoreOption.SecondFuck)
        )
        val loser = Gamer(
            id = 2,
            roundId = roundId,
            loserOption = listOf(LoserOption.PeaBak)
        )

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        ruleRepository.setRules(gameId, listOf(
            Rule(name = Const.Rule.Score, score = 100),
            Rule(name = Const.Rule.Fuck, score = 100)
        ))

        useCase(gameId, roundId, seller = null, winner = winner)

        // 패자 계산: 6×100×2 = 1,200 / 연뻑: 200
        assertEquals(1_400, gamerRepository.getGamerAccount(1))
        assertEquals(-1_400, gamerRepository.getGamerAccount(2))
    }

    @Test
    fun `고스톱 - 고박과 첫따닥`() = runTest {
        val gameId = 1L
        val roundId = 1L

        val winner = Gamer(
            id = 1,
            roundId = roundId,
            score = 7,
            scoreOption = listOf(ScoreOption.FirstDdadak)
        )
        val goBakLoser = Gamer(
            id = 2,
            roundId = roundId,
            loserOption = listOf(LoserOption.GoBak, LoserOption.LightBak)
        )
        val normalLoser = Gamer(
            id = 3,
            roundId = roundId
        )

        gamerRepository.setRoundGamers(roundId, listOf(winner, goBakLoser, normalLoser))
        ruleRepository.setRules(gameId, listOf(
            Rule(name = Const.Rule.Score, score = 100),
            Rule(name = Const.Rule.Fuck, score = 100)
        ))

        useCase(gameId, roundId, seller = null, winner = winner)

        // 패자: (7×100×2) + (7×100) = 2,100을 고박자가 모두 부담
        // 첫따닥: 점당 100 × 3 × 2명 = 600
        assertEquals(2_700, gamerRepository.getGamerAccount(1))
        assertEquals(-2_400, gamerRepository.getGamerAccount(2))
        assertEquals(-300, gamerRepository.getGamerAccount(3))
    }

    @Test
    fun `광팔기 - seller 제외 후 승자 정산`() = runTest {
        val gameId = 1L
        val roundId = 1L

        val seller = Gamer(id = 1, roundId = roundId, score = 2)
        val winner = Gamer(id = 2, roundId = roundId, score = 5)
        val loser1 = Gamer(id = 3, roundId = roundId)
        val loser2 = Gamer(id = 4, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))

        gamerRepository.setRoundGamers(roundId, listOf(seller, winner, loser1, loser2))
        ruleRepository.setRules(gameId, listOf(
            Rule(name = Const.Rule.Score, score = 100),
            Rule(name = Const.Rule.Fuck, score = 100),
            Rule(name = Const.Rule.Sell, score = 150)
        ))

        useCase(gameId, roundId, seller = seller, winner = winner)

        // seller: 2광 × 150 × 3명 = +900
        assertEquals(900, gamerRepository.getGamerAccount(1))

        // 승자: 패자 두 명 상대로 1,500, 광팜 비용 300 지불
        assertEquals(1_200, gamerRepository.getGamerAccount(2))
        // 패자1: 기본 500 + 광팜 300 = -800
        assertEquals(-800, gamerRepository.getGamerAccount(3))
        // 패자2: 피박 1,000 + 광팜 300 = -1,300
        assertEquals(-1_300, gamerRepository.getGamerAccount(4))
    }
}
