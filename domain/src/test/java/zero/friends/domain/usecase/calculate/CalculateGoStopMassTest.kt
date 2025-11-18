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
import kotlin.math.pow

class CalculateGoStopMassTest {
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
    fun `고스톱 전체 시나리오`() = runTest {
        generateGoStopScenarios().forEach { scenario ->
            val gameId = scenario.id.toLong()
            val roundId = gameId
            val winner = Gamer(
                id = 1,
                roundId = roundId,
                score = WINNER_SCORE,
                scoreOption = scenario.winnerOptions
            )
            val losers = scenario.losersBakOptions.mapIndexed { idx, options ->
                Gamer(
                    id = (idx + 2).toLong(),
                    roundId = roundId,
                    loserOption = options
                )
            }

            gamerRepository.setRoundGamers(roundId, listOf(winner) + losers)
            applyDefaultRules(gameId)

            useCase(gameId, roundId, seller = null, winner = winner)

            val winnerAccount = gamerRepository.getGamerAccount(1)
            val loserAccounts = losers.associate { gamer -> gamer.id to gamerRepository.getGamerAccount(gamer.id) }
            val (expectedWinner, expectedLosers) = scenario.expectedAccounts()

            println("GoStop Case ${scenario.id} losers=${scenario.losersBakOptions} winner=${scenario.winnerOptions} -> winner=$winnerAccount, losers=$loserAccounts")
            assertEquals(expectedWinner, winnerAccount)
            losers.forEach { gamer ->
                assertEquals(expectedLosers[gamer.id] ?: 0, loserAccounts[gamer.id])
            }
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

    private fun generateGoStopScenarios(): Sequence<GoStopScenario> = sequence {
        var id = 1
        val loserPairs = mutableListOf<List<List<LoserOption>>>()
        LOSER_COMBINATIONS.forEach { first ->
            LOSER_COMBINATIONS.forEach { second ->
                if (listOf(first, second).count { it.contains(LoserOption.GoBak) } <= 1) {
                    loserPairs.add(listOf(first, second))
                }
            }
        }
        for (pair in loserPairs) {
            for (winnerOption in WINNER_OPTIONS) {
                yield(GoStopScenario(id++, pair, winnerOption))
            }
        }
    }

    private fun GoStopScenario.expectedAccounts(): Pair<Int, Map<Long, Int>> {
        val perLoserBase = losersBakOptions.map { baseLoserAmount(it, isMatgo = false) }.toMutableList()
        val goIndex = losersBakOptions.indexOfFirst { it.contains(LoserOption.GoBak) }
        val payments = mutableMapOf<Long, Int>()
        val winnerBaseIncome: Int

        if (goIndex >= 0) {
            val others = perLoserBase.withIndex().filter { it.index != goIndex }
            val othersSum = others.sumOf { it.value }
            val goOwn = baseLoserAmount(
                losersBakOptions[goIndex].filterNot { it == LoserOption.GoBak },
                isMatgo = false
            )
            winnerBaseIncome = othersSum + goOwn
            losersBakOptions.indices.forEach { idx ->
                payments[idx.toLong() + 2] = if (idx == goIndex) -winnerBaseIncome else 0
            }
        } else {
            winnerBaseIncome = perLoserBase.sum()
            perLoserBase.forEachIndexed { idx, amount ->
                payments[idx.toLong() + 2] = -amount
            }
        }

        val totalOptionIncome = calculateScoreOptionIncome(winnerOptions, losersBakOptions.size)
        val winnerTotal = winnerBaseIncome + totalOptionIncome.first
        losersBakOptions.indices.forEach { idx ->
            payments[idx.toLong() + 2] = (payments[idx.toLong() + 2] ?: 0) - totalOptionIncome.second
        }
        return winnerTotal to payments
    }

    private fun baseLoserAmount(options: List<LoserOption>, isMatgo: Boolean): Int {
        val nonGo = options.filterNot { it == LoserOption.GoBak }
        val result = WINNER_SCORE * SCORE_PER_POINT * (2.0.pow(nonGo.size).toInt())
        return if (isMatgo && options.contains(LoserOption.GoBak)) result * 2 else result
    }

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

    data class GoStopScenario(
        val id: Int,
        val losersBakOptions: List<List<LoserOption>>,
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

        private val LOSER_COMBINATIONS = buildList {
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
