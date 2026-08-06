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
import zero.friends.domain.usecase.calculate.calculateGoScore
import zero.friends.domain.util.Const

class CalculateMatgoMassTest {

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
    fun `맞고 전체 시나리오`() = runTest {
        generateScenarios().forEach { scenario ->
            val gameId = scenario.id.toLong()
            val roundId = gameId

            val winner = Gamer(
                id = 1,
                roundId = roundId,
                score = WINNER_SCORE,
                go = scenario.goCount,
                scoreOption = scenario.winnerOptions
            )
            val loser = Gamer(
                id = 2,
                roundId = roundId,
                loserOption = scenario.loserBakOptions
            )

            gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
            applyDefaultRule(gameId)

            useCase(gameId, roundId, seller = null, winner = winner)

            val winnerAccount = gamerRepository.getGamerAccount(1)
            val loserAccount = gamerRepository.getGamerAccount(2)
            val expect = scenario.expectedAccount()

            assertEquals(expect, winnerAccount)
            assertEquals(-expect, loserAccount)
        }
    }

    private fun applyDefaultRule(gameId: Long) {
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = SCORE_PER_POINT),
                Rule(name = Const.Rule.Fuck, score = FUCK_SCORE)
            )
        )
    }

    private fun generateScenarios(): Sequence<MatgoScenario> = sequence {
        var id = 1
        val goCounts = listOf(0, 1, 3, 5)
        for (bakOptions in BASE_LOSER_COMBINATIONS) {
            for (winnerOpt in WINNER_OPTIONS) {
                for (go in goCounts) {
                    yield(MatgoScenario(id++, go, bakOptions, winnerOpt))
                }
            }
        }
    }

    private fun MatgoScenario.expectedAccount(): Int {
        val winnerScore = calculateGoScore(WINNER_SCORE, goCount)
        val loserAmount = winnerScore * SCORE_PER_POINT * (1 shl loserBakOptions.size)
        val optionIncome = winnerOptions.sumOf {
            when (it) {
                ScoreOption.FirstFuck -> FUCK_SCORE
                ScoreOption.SecondFuck -> FUCK_SCORE * 2
                ScoreOption.ThreeFuck -> FUCK_SCORE * 4
                ScoreOption.FirstDdadak -> SCORE_PER_POINT * 3
            }
        }
        return loserAmount + optionIncome
    }

    data class MatgoScenario(
        val id: Int,
        val goCount: Int,
        val loserBakOptions: List<LoserOption>,
        val winnerOptions: List<ScoreOption>,
    )

    companion object {
        private const val WINNER_SCORE = 7
        private const val SCORE_PER_POINT = 100
        private const val FUCK_SCORE = 500

        private val WINNER_OPTIONS = listOf(
            emptyList(),
            listOf(ScoreOption.FirstFuck),
            listOf(ScoreOption.SecondFuck),
            listOf(ScoreOption.FirstDdadak),
            listOf(ScoreOption.FirstFuck, ScoreOption.FirstDdadak)
        )

        private val BASE_LOSER_COMBINATIONS = buildList {
            val types = listOf(
                LoserOption.PeaBak,
                LoserOption.LightBak,
                LoserOption.MongBak
            )
            val size = types.size
            for (mask in 0 until (1 shl size)) {
                val combo = mutableListOf<LoserOption>()
                for (idx in 0 until size) {
                    if (mask and (1 shl idx) != 0) {
                        combo.add(types[idx])
                    }
                }
                add(combo)
            }
        }
    }
}
