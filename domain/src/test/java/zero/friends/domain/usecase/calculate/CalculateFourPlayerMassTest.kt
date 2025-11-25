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
import kotlin.time.Duration.Companion.seconds

/**
 * 4인 게임(광팔기 포함) 통합 테스트
 * - seller + winner + 패자 2명만 존재 (루저가 3명일 수 없음)
 */
class CalculateFourPlayerMassTest {
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
    fun `4인 고스톱 전체 시나리오`() = runTest(timeout = MASS_TEST_TIMEOUT) {
        generateScenarios().forEach { scenario ->
            val gameId = scenario.id.toLong()
            val roundId = gameId

            val seller = Gamer(id = 1, roundId = roundId, score = scenario.sellerScore)
            val winner = Gamer(
                id = 2,
                roundId = roundId,
                score = WINNER_SCORE,
                go = scenario.goCount,
                scoreOption = scenario.winnerOptions
            )
            val losers = scenario.loserBakOptions.mapIndexed { idx, bak ->
                Gamer(id = (idx + 3).toLong(), roundId = roundId, loserOption = bak)
            }

            gamerRepository.setRoundGamers(roundId, listOf(seller, winner) + losers)
            applyDefaultRule(gameId)

            useCase(gameId, roundId, seller = seller, winner = winner)

            val sellerAccount = gamerRepository.getGamerAccount(1)
            val winnerAccount = gamerRepository.getGamerAccount(2)
            val loserAccounts = losers.associate { gamer -> gamer.id to gamerRepository.getGamerAccount(gamer.id) }
            val expect = scenario.expectedAccounts()

            println(
                "Four Case ${scenario.id} seller=${scenario.sellerScore}장 go=${scenario.goCount} losers=${scenario.loserBakOptions} winnerOpts=${scenario.winnerOptions} -> seller=$sellerAccount, winner=$winnerAccount, losers=$loserAccounts"
            )
            assertEquals(expect.seller, sellerAccount)
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
                Rule(name = Const.Rule.Fuck, score = FUCK_SCORE),
                Rule(name = Const.Rule.Sell, score = SELL_SCORE)
            )
        )
    }

    private fun generateScenarios(): Sequence<FourScenario> = sequence {
        var id = 1
        val loserPairs = BASE_LOSER_COMBINATIONS
        val sellerScores = listOf(1, 2, 3)
        val goCounts = listOf(0, 2, 4)
        for (score in sellerScores) {
            for (first in loserPairs) {
                for (second in loserPairs) {
                    if (listOf(first, second).count { it.contains(LoserOption.GoBak) } <= 1) {
                        for (winnerOpt in WINNER_OPTIONS) {
                            for (go in goCounts) {
                                yield(FourScenario(id++, score, go, listOf(first, second), winnerOpt))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun FourScenario.expectedAccounts(): ExpectedResult {
        val sellerIncome = SELL_SCORE * sellerScore * (loserBakOptions.size + 1)
        val loserPayments = mutableMapOf<Long, Int>()
        val winnerScore = calculateGoScore(WINNER_SCORE, goCount)
        val baseAmounts = loserBakOptions.map { baseLoserAmount(it, winnerScore) }.toMutableList()
        val goIndex = loserBakOptions.indexOfFirst { it.contains(LoserOption.GoBak) }

        val winnerBaseIncome = if (goIndex >= 0) {
            val basePerLoser = baseLoserAmount(emptyList(), winnerScore)
            val baseTotal = basePerLoser * loserBakOptions.size
            val goBakExtra = baseLoserAmount(
                loserBakOptions[goIndex].filterNot { it == LoserOption.GoBak },
                winnerScore
            ) - basePerLoser
            val remainExtras = loserBakOptions.withIndex()
                .filter { it.index != goIndex }
                .sumOf { indexed ->
                    baseLoserAmount(
                        indexed.value.filterNot { it == LoserOption.GoBak },
                        winnerScore
                    ) - basePerLoser
                }

            val total = baseTotal + goBakExtra + (remainExtras * 2)
            loserBakOptions.indices.forEach { idx ->
                loserPayments[idx.toLong() + 3] = if (idx == goIndex) -total else 0
            }
            total
        } else {
            baseAmounts.forEachIndexed { idx, amount ->
                loserPayments[idx.toLong() + 3] = -amount
            }
            baseAmounts.sum()
        }

        val (totalOption, perOpponent) = calculateScoreOptionIncome(winnerOptions, opponentCount = loserBakOptions.size)
        val winnerTotal = winnerBaseIncome + totalOption
        loserBakOptions.indices.forEach { idx ->
            loserPayments[idx.toLong() + 3] = (loserPayments[idx.toLong() + 3] ?: 0) - perOpponent
        }

        // seller에게 지불한 금액을 승자/패자 계정에서 차감
        val winnerAfterSell = winnerTotal - SELL_SCORE * sellerScore
        val adjustedLosers = loserPayments.mapValues { it.value - SELL_SCORE * sellerScore }

        return ExpectedResult(
            seller = sellerIncome,
            winner = winnerAfterSell,
            losers = adjustedLosers
        )
    }

    private fun baseLoserAmount(options: List<LoserOption>, winnerScore: Int): Int {
        val nonGo = options.filterNot { it == LoserOption.GoBak }
        return winnerScore * SCORE_PER_POINT * (1 shl nonGo.size)
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

    data class FourScenario(
        val id: Int,
        val sellerScore: Int,
        val goCount: Int,
        val loserBakOptions: List<List<LoserOption>>,
        val winnerOptions: List<ScoreOption>,
    )

    data class ExpectedResult(
        val seller: Int,
        val winner: Int,
        val losers: Map<Long, Int>
    )

    companion object {
        private const val WINNER_SCORE = 10
        private const val SCORE_PER_POINT = 100
        private const val FUCK_SCORE = 500
        private const val SELL_SCORE = 500
        private val MASS_TEST_TIMEOUT = 300.seconds

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
