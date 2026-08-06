package zero.friends.domain.usecase.calculate.integration

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.usecase.calculate.CalculateGameResultUseCase
import zero.friends.domain.usecase.calculate.CalculateLoserScoreUseCase
import zero.friends.domain.usecase.calculate.CalculateScoreOptionUseCase
import zero.friends.domain.usecase.calculate.CalculateSellScoreUseCase
import zero.friends.domain.usecase.calculate.UpdateAccountUseCase
import zero.friends.domain.util.Const

class CalculateGameResultUseCaseTest {

    private lateinit var useCase: CalculateGameResultUseCase
    private lateinit var ruleRepository: MockRuleRepository
    private lateinit var gamerRepository: MockGamerRepository

    @BeforeTest
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
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = 100),
                Rule(name = Const.Rule.Fuck, score = 100)
            )
        )

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
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = 100),
                Rule(name = Const.Rule.Fuck, score = 100)
            )
        )

        useCase(gameId, roundId, seller = null, winner = winner)

        // 첫따닥: 100 * 3 = 300 x 2명 = 600원 (각 300원 지불)
        // 패자 점수: normal 700원, goBak 700 * 2(광박) * 2(고박) = 2,800원 -> 독박: 3,500원
        // 총: goBak -3,800원, normal -300원, winner +4,100원
        assertEquals(4_100, gamerRepository.getGamerAccount(1))
        assertEquals(-3_800, gamerRepository.getGamerAccount(2))
        assertEquals(-300, gamerRepository.getGamerAccount(3))
    }
}
