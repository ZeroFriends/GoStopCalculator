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

class CalculateMatgoMassTest {
    private lateinit var useCase: CalculateGameResultUseCase
    private lateinit var ruleRepository: MockRuleRepository
    private lateinit var gamerRepository: MockGamerRepository

    @Before
    fun setup() {
        ruleRepository = MockRuleRepository()
        gamerRepository = MockGamerRepository()
        useCase = CalculateGameResultUseCase(
            ruleRepository,
            gamerRepository,
            UpdateAccountUseCase(gamerRepository),
            CalculateSellScoreUseCase(ruleRepository, gamerRepository),
            CalculateLoserScoreUseCase(ruleRepository, gamerRepository),
            CalculateScoreOptionUseCase(ruleRepository, gamerRepository)
        )
    }

    @Test
    fun `맞고 전체 시나리오`() = runTest {
        generateMatgoScenarios().forEach { scenario ->
            val gameId = scenario.id.toLong()
            val roundId = gameId
            val winner = Gamer(
                id = 1,
                roundId = roundId,
                score = WINNER_SCORE,
                scoreOption = scenario.winnerOptions
            )
            val loser = Gamer(
                id = 2,
                roundId = roundId,
                loserOption = scenario.loserOptions
            )

            gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
            applyDefaultRules(gameId)

            useCase(gameId, roundId, seller = null, winner = winner)

            val winnerAccount = gamerRepository.getGamerAccount(1)
            val loserAccount = gamerRepository.getGamerAccount(2)
            val expect = scenario.expectedAccounts()

            println("Matgo Case ${scenario.id} loser=${scenario.loserOptions} winner=${scenario.winnerOptions} -> winner=$winnerAccount, loser=$loserAccount")
            assertEquals(expect.first, winnerAccount)
            assertEquals(expect.second, loserAccount)
        }
    }

    private fun applyDefaultRules(gameId: Long) {
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = SCORE_PER_POINT),
                Rule(name = Const.Rule.Fuck, score = FUCK_SCORE)
            )
        )
    }

    private fun generateMatgoScenarios(): Sequence<MatgoScenario> = sequence {
        var id = 1
        for (loser in LOSER_COMBINATIONS) {
            for (winnerOption in WINNER_OPTIONS) {
                yield(MatgoScenario(id++, loser, winnerOption))
            }
        }
    }

    private fun MatgoScenario.expectedAccounts(): Pair<Int, Int> {
        val base = baseLoserAmount(loserOptions, isMatgo = true)
        val option = calculateScoreOptionIncome(winnerOptions, opponentCount = 1)
        val total = base + option
        return total to -total
    }

    private fun baseLoserAmount(options: List<LoserOption>, isMatgo: Boolean): Int {
        val nonGo = options.filterNot { it == LoserOption.GoBak }
        var amount = WINNER_SCORE * SCORE_PER_POINT * (1 shl nonGo.size)
        if (isMatgo && options.contains(LoserOption.GoBak)) {
            amount *= 2
        }
        return amount
    }

    private fun calculateScoreOptionIncome(options: List<ScoreOption>, opponentCount: Int): Int {
        val perOpponent = options.sumOf {
            when (it) {
                ScoreOption.FirstFuck -> FUCK_SCORE
                ScoreOption.SecondFuck -> FUCK_SCORE * 2
                ScoreOption.ThreeFuck -> FUCK_SCORE * 4
                ScoreOption.FirstDdadak -> SCORE_PER_POINT * 3
            }
        }
        return perOpponent * opponentCount
    }

    data class MatgoScenario(
        val id: Int,
        val loserOptions: List<LoserOption>,
        val winnerOptions: List<ScoreOption>
    )

    companion object {
        private const val WINNER_SCORE = 10
        private const val SCORE_PER_POINT = 100
        private const val FUCK_SCORE = 500

        private val WINNER_OPTIONS = listOf(
            emptyList(),
            listOf(ScoreOption.FirstFuck),
            listOf(ScoreOption.SecondFuck),
            listOf(ScoreOption.FirstDdadak),
            listOf(ScoreOption.FirstFuck, ScoreOption.FirstDdadak),
            listOf(ScoreOption.SecondFuck, ScoreOption.FirstDdadak),
            listOf(ScoreOption.ThreeFuck)
        )

        private val LOSER_COMBINATIONS: List<List<LoserOption>> = buildList {
            val types = listOf(
                LoserOption.PeaBak,
                LoserOption.LightBak,
                LoserOption.MongBak,
                LoserOption.GoBak
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
