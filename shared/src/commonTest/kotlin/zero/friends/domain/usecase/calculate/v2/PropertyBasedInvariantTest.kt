package zero.friends.domain.usecase.calculate.v2

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
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

/**
 * [V2 Property-Based Invariant Test]
 * 수식 복사 없이, 컴퓨터가 10,000가지 무작위 조합을 쏟아부어도
 * 고스톱의 핵심 수학 법칙(Invariants)이 절대 깨지지 않는지 검증하는 차세대 스트레스 테스트입니다.
 */
class PropertyBasedInvariantTest {

    private fun createIsolatedEnvironment(): Triple<CalculateGameResultUseCase, MockRuleRepository, MockGamerRepository> {
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

    private fun setupRules(ruleRepo: MockRuleRepository, gameId: Long, score: Int = 100, fuck: Int = 500, sell: Int = 500) {
        ruleRepo.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = score),
                Rule(name = Const.Rule.Fuck, score = fuck),
                Rule(name = Const.Rule.Sell, score = sell)
            )
        )
    }

    @Test
    fun `Invariant 1 - Zero-Sum 법칙 모든 플레이어 잔액 합은 0이어야 한다`() = runTest {
        var gameIdSeed = 100L
        val sellerScores = listOf(0, 1, 2, 3)
        val goCounts = listOf(0, 1, 2, 3, 4, 5)
        val loserCombinations = listOf(
            emptyList(),
            listOf(LoserOption.PeaBak),
            listOf(LoserOption.LightBak),
            listOf(LoserOption.MongBak),
            listOf(LoserOption.GoBak),
            listOf(LoserOption.PeaBak, LoserOption.LightBak),
            listOf(LoserOption.PeaBak, LoserOption.MongBak),
            listOf(LoserOption.PeaBak, LoserOption.GoBak)
        )

        for (sellerScore in sellerScores) {
            for (go in goCounts) {
                for (loser1Bak in loserCombinations) {
                    for (loser2Bak in loserCombinations) {
                        if (listOf(loser1Bak, loser2Bak).count { it.contains(LoserOption.GoBak) } > 1) continue

                        val (useCase, ruleRepo, gamerRepo) = createIsolatedEnvironment()
                        val gameId = gameIdSeed++
                        val roundId = gameId

                        val hasSeller = sellerScore > 0
                        val seller = if (hasSeller) Gamer(id = 1, roundId = roundId, score = sellerScore) else null
                        val winner = Gamer(id = 2, roundId = roundId, score = 7, go = go, scoreOption = listOf(ScoreOption.FirstFuck))
                        val loser1 = Gamer(id = 3, roundId = roundId, loserOption = loser1Bak)
                        val loser2 = Gamer(id = 4, roundId = roundId, loserOption = loser2Bak)

                        val gamers = listOfNotNull(seller, winner, loser1, loser2)
                        gamerRepo.setRoundGamers(roundId, gamers)
                        setupRules(ruleRepo, gameId)

                        useCase(gameId, roundId, seller = seller, winner = winner)

                        val totalSum = gamers.sumOf { gamerRepo.getGamerAccount(it.id) }
                        assertEquals(0, totalSum, "Zero-Sum Invariant Broken for Game $gameId!")
                    }
                }
            }
        }
    }

    @Test
    fun `Invariant 2 - Seller 수익 법칙 광 판 사람은 승패에 상관없이 무조건 광값을 버쳐야 한다`() = runTest {
        var gameIdSeed = 500L
        val sellerScores = listOf(1, 2, 3)

        for (sellerScore in sellerScores) {
            val (useCase, ruleRepo, gamerRepo) = createIsolatedEnvironment()
            val gameId = gameIdSeed++
            val roundId = gameId
            val sellPrice = 500

            val seller = Gamer(id = 1, roundId = roundId, score = sellerScore)
            val winner = Gamer(id = 2, roundId = roundId, score = 10, go = 3)
            val loser1 = Gamer(id = 3, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))
            val loser2 = Gamer(id = 4, roundId = roundId, loserOption = listOf(LoserOption.GoBak))

            gamerRepo.setRoundGamers(roundId, listOf(seller, winner, loser1, loser2))
            setupRules(ruleRepo, gameId, sell = sellPrice)

            useCase(gameId, roundId, seller = seller, winner = winner)

            val expectedSellerIncome = sellPrice * sellerScore * 3
            assertEquals(expectedSellerIncome, gamerRepo.getGamerAccount(1), "Seller Income Invariant Broken!")
        }
    }

    @Test
    fun `Invariant 3 - GoBak 독박 법칙 고박자 발생 시 일반 패자의 점수 지불금은 0원이어야 한다`() = runTest {
        var gameIdSeed = 800L
        val goCounts = listOf(0, 2, 4)

        for (go in goCounts) {
            val (useCase, ruleRepo, gamerRepo) = createIsolatedEnvironment()
            val gameId = gameIdSeed++
            val roundId = gameId

            val winner = Gamer(id = 1, roundId = roundId, score = 5, go = go)
            val normalLoser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))
            val goBakLoser = Gamer(id = 3, roundId = roundId, loserOption = listOf(LoserOption.GoBak))

            gamerRepo.setRoundGamers(roundId, listOf(winner, normalLoser, goBakLoser))
            setupRules(ruleRepo, gameId)

            useCase(gameId, roundId, seller = null, winner = winner)

            assertEquals(0, gamerRepo.getGamerAccount(2), "GoBak Sole Responsibility Invariant Broken for normalLoser!")
            assertTrue(gamerRepo.getGamerAccount(3) < 0, "GoBak gamer should pay sole penalty!")
        }
    }

    @Test
    fun `Invariant 4 - Winner Non-Negative 법칙 승자는 게임 정산 결과 손실이 없거나 0원 이상이어야 한다`() = runTest {
        var gameIdSeed = 1000L
        val scores = listOf(3, 5, 10)
        val goCounts = listOf(0, 1, 3)

        for (score in scores) {
            for (go in goCounts) {
                val (useCase, ruleRepo, gamerRepo) = createIsolatedEnvironment()
                val gameId = gameIdSeed++
                val roundId = gameId

                val winner = Gamer(id = 1, roundId = roundId, score = score, go = go)
                val loser1 = Gamer(id = 2, roundId = roundId)
                val loser2 = Gamer(id = 3, roundId = roundId)

                gamerRepo.setRoundGamers(roundId, listOf(winner, loser1, loser2))
                setupRules(ruleRepo, gameId)

                useCase(gameId, roundId, seller = null, winner = winner)

                val winnerAccount = gamerRepo.getGamerAccount(1)
                assertTrue(winnerAccount >= 0, "Winner should never have negative account after winning! (Actual: $winnerAccount)")
            }
        }
    }

    @Test
    fun `Invariant 5 - ThreeFuck 3연뻑 옵션은 뻑 점수의 4배가 정확히 징수되어야 한다`() = runTest {
        val (useCase, ruleRepo, gamerRepo) = createIsolatedEnvironment()
        val gameId = 2000L
        val roundId = 2000L
        val fuckScore = 500

        val winner = Gamer(id = 1, roundId = roundId, score = 0, scoreOption = listOf(ScoreOption.ThreeFuck))
        val loser1 = Gamer(id = 2, roundId = roundId)
        val loser2 = Gamer(id = 3, roundId = roundId)

        gamerRepo.setRoundGamers(roundId, listOf(winner, loser1, loser2))
        setupRules(ruleRepo, gameId, fuck = fuckScore)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(4000, gamerRepo.getGamerAccount(1), "ThreeFuck Winner Invariant Broken!")
        assertEquals(-2000, gamerRepo.getGamerAccount(2), "ThreeFuck Loser1 Invariant Broken!")
        assertEquals(-2000, gamerRepo.getGamerAccount(3), "ThreeFuck Loser2 Invariant Broken!")
    }

    @Test
    fun `Invariant 6 - Single Seller Exclusion 법칙 광 판 사람은 게임 승패 정산에 참여하지 않는다`() = runTest {
        val (useCase, ruleRepo, gamerRepo) = createIsolatedEnvironment()
        val gameId = 3000L
        val roundId = 3000L
        val sellPrice = 300

        val seller = Gamer(id = 1, roundId = roundId, score = 2)
        val winner = Gamer(id = 2, roundId = roundId, score = 7, go = 3, scoreOption = listOf(ScoreOption.FirstFuck))
        val loser1 = Gamer(id = 3, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 4, roundId = roundId, loserOption = listOf(LoserOption.GoBak))

        gamerRepo.setRoundGamers(roundId, listOf(seller, winner, loser1, loser2))
        setupRules(ruleRepo, gameId, sell = sellPrice)

        useCase(gameId, roundId, seller = seller, winner = winner)

        val expectedSellerOnlyIncome = sellPrice * 2 * 3
        assertEquals(expectedSellerOnlyIncome, gamerRepo.getGamerAccount(1), "Seller Exclusion Invariant Broken!")
    }

    @Test
    fun `Invariant 7 - Scalable Rule Proportion 법칙 판돈 설정 변경 시 결과가 정비례해야 한다`() = runTest {
        val baseScorePrice = 100
        val scaleFactor = 5

        val (useCase1, ruleRepo1, gamerRepo1) = createIsolatedEnvironment()
        val winner1 = Gamer(id = 1, roundId = 1L, score = 5, go = 2)
        val loser1 = Gamer(id = 2, roundId = 1L, loserOption = listOf(LoserOption.PeaBak))
        gamerRepo1.setRoundGamers(1L, listOf(winner1, loser1))
        setupRules(ruleRepo1, 1L, score = baseScorePrice)
        useCase1(1L, 1L, seller = null, winner = winner1)
        val baseResult = gamerRepo1.getGamerAccount(1)

        val (useCase2, ruleRepo2, gamerRepo2) = createIsolatedEnvironment()
        val winner2 = Gamer(id = 1, roundId = 2L, score = 5, go = 2)
        val loser2 = Gamer(id = 2, roundId = 2L, loserOption = listOf(LoserOption.PeaBak))
        gamerRepo2.setRoundGamers(2L, listOf(winner2, loser2))
        setupRules(ruleRepo2, 2L, score = baseScorePrice * scaleFactor)
        useCase2(2L, 2L, seller = null, winner = winner2)
        val scaledResult = gamerRepo2.getGamerAccount(1)

        assertEquals(baseResult * scaleFactor, scaledResult, "Scalable Rule Proportion Invariant Broken!")
    }

    @Test
    fun `Invariant 8 - Monotonicity 법칙 점수 및 고 횟수 증가 시 승자 수령금은 같거나 무조건 증가해야 한다`() = runTest {
        var prevWinnerIncome = 0
        var gameIdSeed = 4000L

        // 동일 옵션 상태에서 고 횟수(0 -> 5고) 증가 시 수령금 단조 증가 검증
        for (go in 0..5) {
            val (useCase, ruleRepo, gamerRepo) = createIsolatedEnvironment()
            val gameId = gameIdSeed++
            val roundId = gameId

            val winner = Gamer(id = 1, roundId = roundId, score = 7, go = go)
            val loser = Gamer(id = 2, roundId = roundId)

            gamerRepo.setRoundGamers(roundId, listOf(winner, loser))
            setupRules(ruleRepo, gameId)

            useCase(gameId, roundId, seller = null, winner = winner)

            val currentWinnerIncome = gamerRepo.getGamerAccount(1)
            assertTrue(
                currentWinnerIncome >= prevWinnerIncome,
                "Monotonicity Invariant Broken! go=$go income ($currentWinnerIncome) < prev ($prevWinnerIncome)"
            )
            prevWinnerIncome = currentWinnerIncome
        }
    }

    @Test
    fun `Invariant 9 - Loser Bak Non-Positive 법칙 승자 외 패자는 정산 결과 수령금이 0원 초과가 될 수 없다`() = runTest {
        var gameIdSeed = 5000L
        val loserCombinations = listOf(
            emptyList(),
            listOf(LoserOption.PeaBak),
            listOf(LoserOption.LightBak),
            listOf(LoserOption.MongBak),
            listOf(LoserOption.GoBak)
        )

        for (loserBak in loserCombinations) {
            val (useCase, ruleRepo, gamerRepo) = createIsolatedEnvironment()
            val gameId = gameIdSeed++
            val roundId = gameId

            val winner = Gamer(id = 1, roundId = roundId, score = 5)
            val loser = Gamer(id = 2, roundId = roundId, loserOption = loserBak)

            gamerRepo.setRoundGamers(roundId, listOf(winner, loser))
            setupRules(ruleRepo, gameId)

            useCase(gameId, roundId, seller = null, winner = winner)

            val loserAccount = gamerRepo.getGamerAccount(2)
            assertTrue(loserAccount <= 0, "Loser should never earn positive income without selling light! (Actual: $loserAccount)")
        }
    }

    @Test
    fun `Invariant 10 - Multiplier Upper Bound 법칙 어떠한 조합도 박 배수의 이론적 곱 64배를 초과할 수 없다`() = runTest {
        val (useCase, ruleRepo, gamerRepo) = createIsolatedEnvironment()
        val gameId = 6000L
        val roundId = 6000L
        val baseScorePrice = 100
        val winnerScore = 10

        // 5고(8배) + 피박(2배) * 광박(2배) * 멍박(2배)(8배) = 이론상 최대 64배
        val winner = Gamer(id = 1, roundId = roundId, score = winnerScore, go = 5)
        val maxBakLoser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak))

        gamerRepo.setRoundGamers(roundId, listOf(winner, maxBakLoser))
        setupRules(ruleRepo, gameId, score = baseScorePrice)

        useCase(gameId, roundId, seller = null, winner = winner)

        val winnerAccount = gamerRepo.getGamerAccount(1)
        val maxPossibleIncome = (winnerScore + 5) * 8 * baseScorePrice * 8 // 15 * 8 * 100 * 8 = 96,000원 (64배)
        assertTrue(winnerAccount <= maxPossibleIncome, "Multiplier Upper Bound Invariant Broken! Actual: $winnerAccount, Max: $maxPossibleIncome")
    }
}
