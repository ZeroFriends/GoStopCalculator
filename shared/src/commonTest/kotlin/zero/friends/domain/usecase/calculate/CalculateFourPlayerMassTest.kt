package zero.friends.domain.usecase.calculate

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.util.Const
import kotlin.time.Duration.Companion.seconds

/**
 * 4인 게임(광팔기 포함) 시나리오 분할 및 병렬 검증 테스트
 * - 병렬화 및 분수 분할을 위해 시나리오 카테고리별 개별 @Test 로 분리
 */
class CalculateFourPlayerMassTest {

    private fun createIsolatedUseCase(): Triple<CalculateGameResultUseCase, MockRuleRepository, MockGamerRepository> {
        val ruleRepo = MockRuleRepository()
        val gamerRepo = MockGamerRepository()
        val useCase = CalculateGameResultUseCase(
            ruleRepository = ruleRepo,
            gamerRepository = gamerRepo,
            updateAccountUseCase = UpdateAccountUseCase(gamerRepo),
            calculateSellScoreUseCase = CalculateSellScoreUseCase(ruleRepo, gamerRepo),
            calculateLoserScoreUseCase = CalculateLoserScoreUseCase(ruleRepo, gamerRepo),
            calculateScoreOptionUseCase = CalculateScoreOptionUseCase(ruleRepo, gamerRepo)
        )
        return Triple(useCase, ruleRepo, gamerRepo)
    }

    private fun applyDefaultRule(ruleRepository: MockRuleRepository, gameId: Long) {
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = SCORE_PER_POINT),
                Rule(name = Const.Rule.Fuck, score = FUCK_SCORE),
                Rule(name = Const.Rule.Sell, score = SELL_SCORE)
            )
        )
    }

    private suspend fun runScenarioList(scenarios: List<FourScenario>) {
        scenarios.forEach { scenario ->
            val (useCase, ruleRepository, gamerRepository) = createIsolatedUseCase()
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
            applyDefaultRule(ruleRepository, gameId)

            useCase(gameId, roundId, seller = seller, winner = winner)

            val sellerAccount = gamerRepository.getGamerAccount(1)
            val winnerAccount = gamerRepository.getGamerAccount(2)
            val loserAccounts = losers.associate { gamer -> gamer.id to gamerRepository.getGamerAccount(gamer.id) }
            val expect = scenario.expectedAccounts()

            assertEquals(expect.seller, sellerAccount, "Seller account mismatch in Scenario ${scenario.id}")
            assertEquals(expect.winner, winnerAccount, "Winner account mismatch in Scenario ${scenario.id}")
            losers.forEach { gamer ->
                assertEquals(expect.losers[gamer.id] ?: 0, loserAccounts[gamer.id], "Loser ${gamer.id} account mismatch in Scenario ${scenario.id}")
            }
        }
    }

    @Test
    fun `4인 고스톱 - 기본 광팔기 및 일반 박 옵션 시나리오`() = runTest(timeout = MASS_TEST_TIMEOUT) {
        val scenarios = generateScenarios(
            sellerScores = listOf(1, 2, 3),
            goCounts = listOf(0, 1, 2),
            includeGoBakOnly = false,
            winnerOptionsToUse = listOf(emptyList())
        )
        runScenarioList(scenarios)
    }

    @Test
    fun `4인 고스톱 - 고박 독박 시나리오`() = runTest(timeout = MASS_TEST_TIMEOUT) {
        val scenarios = generateScenarios(
            sellerScores = listOf(1, 2, 3),
            goCounts = listOf(0, 3),
            includeGoBakOnly = true,
            winnerOptionsToUse = listOf(emptyList(), listOf(ScoreOption.FirstFuck))
        )
        runScenarioList(scenarios)
    }

    @Test
    fun `4인 고스톱 - 승자 옵션 복합 시나리오`() = runTest(timeout = MASS_TEST_TIMEOUT) {
        val scenarios = generateScenarios(
            sellerScores = listOf(1, 2),
            goCounts = listOf(0, 2),
            includeGoBakOnly = false,
            winnerOptionsToUse = WINNER_OPTIONS
        )
        runScenarioList(scenarios)
    }

    @Test
    fun `4인 고스톱 - 고득점 배수 시나리오`() = runTest(timeout = MASS_TEST_TIMEOUT) {
        val scenarios = generateScenarios(
            sellerScores = listOf(2),
            goCounts = listOf(3, 4, 5),
            includeGoBakOnly = false,
            winnerOptionsToUse = listOf(emptyList(), listOf(ScoreOption.FirstDdadak))
        )
        runScenarioList(scenarios)
    }

    private fun generateScenarios(
        sellerScores: List<Int>,
        goCounts: List<Int>,
        includeGoBakOnly: Boolean,
        winnerOptionsToUse: List<List<ScoreOption>>
    ): List<FourScenario> {
        val list = mutableListOf<FourScenario>()
        var id = 1
        val loserPairs = BASE_LOSER_COMBINATIONS
        for (score in sellerScores) {
            for (first in loserPairs) {
                for (second in loserPairs) {
                    val hasGoBak = listOf(first, second).any { it.contains(LoserOption.GoBak) }
                    if (includeGoBakOnly && !hasGoBak) continue
                    if (!includeGoBakOnly && hasGoBak) continue

                    if (listOf(first, second).count { it.contains(LoserOption.GoBak) } <= 1) {
                        for (winnerOpt in winnerOptionsToUse) {
                            for (go in goCounts) {
                                list.add(FourScenario(id++, score, go, listOf(first, second), winnerOpt))
                            }
                        }
                    }
                }
            }
        }
        return list
    }

    private fun FourScenario.expectedAccounts(): ExpectedResult {
        val sellerIncome = SELL_SCORE * sellerScore * (loserBakOptions.size + 1)
        val loserPayments = mutableMapOf<Long, Int>()
        val winnerScore = calculateGoScore(WINNER_SCORE, goCount)
        val baseAmounts = loserBakOptions.map { baseLoserAmount(it, winnerScore) }
        val goIndex = loserBakOptions.indexOfFirst { it.contains(LoserOption.GoBak) }

        val winnerBaseIncome = baseAmounts.sum()
        if (goIndex >= 0) {
            loserBakOptions.indices.forEach { idx ->
                loserPayments[idx.toLong() + 3] = if (idx == goIndex) -winnerBaseIncome else 0
            }
        } else {
            baseAmounts.forEachIndexed { idx, amount ->
                loserPayments[idx.toLong() + 3] = -amount
            }
        }

        val (totalOption, perOpponent) = calculateScoreOptionIncome(winnerOptions, opponentCount = loserBakOptions.size)
        val winnerTotal = winnerBaseIncome + totalOption
        loserBakOptions.indices.forEach { idx ->
            loserPayments[idx.toLong() + 3] = (loserPayments[idx.toLong() + 3] ?: 0) - perOpponent
        }

        val winnerAfterSell = winnerTotal - SELL_SCORE * sellerScore
        val adjustedLosers = loserPayments.mapValues { it.value - SELL_SCORE * sellerScore }

        return ExpectedResult(
            seller = sellerIncome,
            winner = winnerAfterSell,
            losers = adjustedLosers
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
        private val MASS_TEST_TIMEOUT = 30.seconds

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
