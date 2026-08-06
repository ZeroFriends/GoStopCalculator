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

class CalculateGoStopMassTest {
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
    fun `3인 고스톱 전체 시나리오`() = runTest {
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
            val losers = scenario.loserBakOptions.mapIndexed { idx, bak ->
                Gamer(id = (idx + 2).toLong(), roundId = roundId, loserOption = bak)
            }

            gamerRepository.setRoundGamers(roundId, listOf(winner) + losers)
            applyDefaultRule(gameId)

            useCase(gameId, roundId, seller = null, winner = winner)

            val winnerAccount = gamerRepository.getGamerAccount(1)
            val loserAccounts = losers.associate { gamer -> gamer.id to gamerRepository.getGamerAccount(gamer.id) }
            val expect = scenario.expectedAccounts()

            assertEquals(expect.winner, winnerAccount)
            losers.forEach { gamer ->
                assertEquals(expect.losers[gamer.id] ?: 0, loserAccounts[gamer.id])
            }
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

    private fun generateScenarios(): Sequence<ThreeScenario> = sequence {
        var id = 1
        val loserPairs = BASE_LOSER_COMBINATIONS
        val goCounts = listOf(0, 1, 3, 5)
        for (first in loserPairs) {
            for (second in loserPairs) {
                if (listOf(first, second).count { it.contains(LoserOption.GoBak) } <= 1) {
                    for (winnerOpt in WINNER_OPTIONS) {
                        for (go in goCounts) {
                            yield(ThreeScenario(id++, go, listOf(first, second), winnerOpt))
                        }
                    }
                }
            }
        }
    }

    private fun ThreeScenario.expectedAccounts(): ExpectedResult {
        val loserPayments = mutableMapOf<Long, Int>()
        val winnerScore = calculateGoScore(WINNER_SCORE, goCount)
        val baseAmounts = loserBakOptions.map { baseLoserAmount(it, winnerScore) }
        val goIndex = loserBakOptions.indexOfFirst { it.contains(LoserOption.GoBak) }

        val winnerBaseIncome = baseAmounts.sum()
        if (goIndex >= 0) {
            loserBakOptions.indices.forEach { idx ->
                loserPayments[idx.toLong() + 2] = if (idx == goIndex) -winnerBaseIncome else 0
            }
        } else {
            baseAmounts.forEachIndexed { idx, amount ->
                loserPayments[idx.toLong() + 2] = -amount
            }
        }

        val (totalOption, perOpponent) = calculateScoreOptionIncome(winnerOptions, opponentCount = loserBakOptions.size)
        val winnerTotal = winnerBaseIncome + totalOption
        loserBakOptions.indices.forEach { idx ->
            loserPayments[idx.toLong() + 2] = (loserPayments[idx.toLong() + 2] ?: 0) - perOpponent
        }

        return ExpectedResult(
            winner = winnerTotal,
            losers = loserPayments
        )
    }

    private fun baseLoserAmount(options: List<LoserOption>, winnerScore: Int): Int =
        winnerScore * SCORE_PER_POINT * (1 shl options.size)

    private fun calculateScoreOptionIncome(options: List<ScoreOption>, opponentCount: Int): Pair<Int, Int> {
        val perOpponent = options.sumOf {
            when (it) {
                ScoreOption.FirstFuck -> FUCK_SCORE
                ScoreOption.SecondFuck -> FUCK_SCORE * 2
                ScoreOption.ThreeFuck -> FUCK_SCORE * 4
                ScoreOption.FirstDdadak -> SCORE_PER_POINT * 3
            }
        }
        return (perOpponent * opponentCount) to perOpponent
    }

    data class ThreeScenario(
        val id: Int,
        val goCount: Int,
        val loserBakOptions: List<List<LoserOption>>,
        val winnerOptions: List<ScoreOption>,
    )

    data class ExpectedResult(
        val winner: Int,
        val losers: Map<Long, Int>
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
