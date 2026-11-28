package zero.friends.domain.usecase.calculate.v2

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

/**
 * [V2 Ground Truth Oracle Test]
 * 테스트 내부 수식 재구현(Tautology)을 완전히 제거하고,
 * 사람이 계산기 및 고스톱 룰북으로 직접 검수한 15가지 절대 정답표(Fixture)와 1대1 대조 검증합니다.
 */
class GroundTruthOracleTest {

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

    private fun setupRules(gameId: Long, score: Int = 100, fuck: Int = 500, sell: Int = 500) {
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = score),
                Rule(name = Const.Rule.Fuck, score = fuck),
                Rule(name = Const.Rule.Sell, score = sell)
            )
        )
    }

    // --- [맞고 2인 시나리오 정답표 검증] ---

    @Test
    fun `Oracle Case 01 - 맞고 기본 7점 승리 피박 없음`() = runTest {
        val gameId = 1L; val roundId = 1L
        val winner = Gamer(id = 1, roundId = roundId, score = 7)
        val loser = Gamer(id = 2, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        setupRules(gameId)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(700, gamerRepository.getGamerAccount(1))
        assertEquals(-700, gamerRepository.getGamerAccount(2))
    }

    @Test
    fun `Oracle Case 02 - 맞고 10점 3고 승리 및 피박`() = runTest {
        val gameId = 1L; val roundId = 2L
        val winner = Gamer(id = 1, roundId = roundId, score = 10, go = 3)
        val loser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        setupRules(gameId)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(5200, gamerRepository.getGamerAccount(1))
        assertEquals(-5200, gamerRepository.getGamerAccount(2))
    }

    @Test
    fun `Oracle Case 03 - 맞고 피박 광박 멍박 8배 중첩`() = runTest {
        val gameId = 1L; val roundId = 3L
        val winner = Gamer(id = 1, roundId = roundId, score = 5)
        val loser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        setupRules(gameId)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(4000, gamerRepository.getGamerAccount(1))
        assertEquals(-4000, gamerRepository.getGamerAccount(2))
    }

    // --- [고스톱 3인 시나리오 정답표 검증] ---

    @Test
    fun `Oracle Case 04 - 고스톱 3인 일반 정산 피박 1명`() = runTest {
        val gameId = 1L; val roundId = 4L
        val winner = Gamer(id = 1, roundId = roundId, score = 5)
        val loser1 = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 3, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser1, loser2))
        setupRules(gameId)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(1500, gamerRepository.getGamerAccount(1))
        assertEquals(-1000, gamerRepository.getGamerAccount(2))
        assertEquals(-500, gamerRepository.getGamerAccount(3))
    }

    @Test
    fun `Oracle Case 05 - 고스톱 3인 고박 발생 고박자 독박 부담`() = runTest {
        val gameId = 1L; val roundId = 5L
        val winner = Gamer(id = 1, roundId = roundId, score = 4)
        val loser1 = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))
        val goBakLoser = Gamer(id = 3, roundId = roundId, loserOption = listOf(LoserOption.GoBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser1, goBakLoser))
        setupRules(gameId)

        useCase(gameId, roundId, seller = null, winner = winner)

        // 나무위키 3인 고스톱 표준: loser1(피박 800원) + goBakLoser(본인 400원) = 1,200원 독박 대납
        assertEquals(1200, gamerRepository.getGamerAccount(1))
        assertEquals(0, gamerRepository.getGamerAccount(2))
        assertEquals(-1200, gamerRepository.getGamerAccount(3))
    }

    @Test
    fun `Oracle Case 06 - 고스톱 3인 첫뻑 첫따닥 승자 옵션`() = runTest {
        val gameId = 1L; val roundId = 6L
        val winner = Gamer(id = 1, roundId = roundId, score = 3, scoreOption = listOf(ScoreOption.FirstFuck, ScoreOption.FirstDdadak))
        val loser1 = Gamer(id = 2, roundId = roundId)
        val loser2 = Gamer(id = 3, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser1, loser2))
        setupRules(gameId)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(2200, gamerRepository.getGamerAccount(1))
        assertEquals(-1100, gamerRepository.getGamerAccount(2))
        assertEquals(-1100, gamerRepository.getGamerAccount(3))
    }

    // --- [4인 고스톱 광팔기 시나리오 정답표 검증] ---

    @Test
    fun `Oracle Case 07 - 4인 경기 광 2장 판매 정산`() = runTest {
        val gameId = 1L; val roundId = 7L
        val seller = Gamer(id = 1, roundId = roundId, score = 2)
        val winner = Gamer(id = 2, roundId = roundId, score = 5)
        val loser1 = Gamer(id = 3, roundId = roundId)
        val loser2 = Gamer(id = 4, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(seller, winner, loser1, loser2))
        setupRules(gameId)

        useCase(gameId, roundId, seller = seller, winner = winner)

        assertEquals(3000, gamerRepository.getGamerAccount(1))
        assertEquals(0, gamerRepository.getGamerAccount(2))
        assertEquals(-1500, gamerRepository.getGamerAccount(3))
        assertEquals(-1500, gamerRepository.getGamerAccount(4))
    }

    @Test
    fun `Oracle Case 08 - 4인 경기 광 1장 판매 고박 독박`() = runTest {
        val gameId = 1L; val roundId = 8L
        val seller = Gamer(id = 1, roundId = roundId, score = 1)
        val winner = Gamer(id = 2, roundId = roundId, score = 3)
        val normalLoser = Gamer(id = 3, roundId = roundId)
        val goBakLoser = Gamer(id = 4, roundId = roundId, loserOption = listOf(LoserOption.GoBak))

        gamerRepository.setRoundGamers(roundId, listOf(seller, winner, normalLoser, goBakLoser))
        setupRules(gameId)

        useCase(gameId, roundId, seller = seller, winner = winner)

        // seller: +500 * 3 = +1,500원
        // 판 점수: normalLoser(300원) + goBakLoser(300원) = 600원을 goBakLoser가 전액 대납
        // winner: +600(판돈) - 500(광값) = +100원 / normalLoser: 0 - 500 = -500원 / goBakLoser: -600 - 500 = -1,100원
        assertEquals(1500, gamerRepository.getGamerAccount(1))
        assertEquals(100, gamerRepository.getGamerAccount(2))
        assertEquals(-500, gamerRepository.getGamerAccount(3))
        assertEquals(-1100, gamerRepository.getGamerAccount(4))
    }

    @Test
    fun `Oracle Case 09 - 맞고 5고 피박 멍박 최고 배수 정산`() = runTest {
        val gameId = 1L; val roundId = 9L
        // 10점 5고 = (10 + 5) * 8 = 120점 -> 120 * 100원 = 12,000원 -> 피박(2배) * 멍박(2배) = 4배 = 48,000원
        val winner = Gamer(id = 1, roundId = roundId, score = 10, go = 5)
        val loser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak, LoserOption.MongBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        setupRules(gameId)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(48000, gamerRepository.getGamerAccount(1))
        assertEquals(-48000, gamerRepository.getGamerAccount(2))
    }

    @Test
    fun `Oracle Case 10 - 고스톱 3인 첫뻑 2회 SecondFuck 중첩 승리`() = runTest {
        val gameId = 1L; val roundId = 10L
        // 승자 3점 (300원) + SecondFuck(1인당 1,000원) = 1인당 1,300원 수령 * 2명 = 2,600원
        val winner = Gamer(id = 1, roundId = roundId, score = 3, scoreOption = listOf(ScoreOption.SecondFuck))
        val loser1 = Gamer(id = 2, roundId = roundId)
        val loser2 = Gamer(id = 3, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser1, loser2))
        setupRules(gameId, fuck = 500)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(2600, gamerRepository.getGamerAccount(1))
        assertEquals(-1300, gamerRepository.getGamerAccount(2))
        assertEquals(-1300, gamerRepository.getGamerAccount(3))
    }

    @Test
    fun `Oracle Case 11 - 고스톱 3인 광박 고박 독박 정산`() = runTest {
        val gameId = 1L; val roundId = 11L
        // 승자 5점 (500원)
        // loser1(광박): 500 * 2 = 1,000원
        // goBakLoser(광박+고박): 본인 광박 500 * 2 = 1,000원 + loser1 대납 1,000원 = 2,000원 독박 지불
        val winner = Gamer(id = 1, roundId = roundId, score = 5)
        val loser1 = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.LightBak))
        val goBakLoser = Gamer(id = 3, roundId = roundId, loserOption = listOf(LoserOption.LightBak, LoserOption.GoBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser1, goBakLoser))
        setupRules(gameId)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(2000, gamerRepository.getGamerAccount(1))
        assertEquals(0, gamerRepository.getGamerAccount(2))
        assertEquals(-2000, gamerRepository.getGamerAccount(3))
    }

    @Test
    fun `Oracle Case 12 - 4인 경기 광 3장 최고 판매 피박 승리`() = runTest {
        val gameId = 1L; val roundId = 12L
        // seller(id=1, 광 3장 = 1,500원 * 3명 = +4,500원)
        // winner(id=2, 4점 * 100원 = 400원 -> loser1(피박): 800원, loser2(일반): 400원 = 1,200원 수령) - 광값 1,500원 = -300원
        // loser1(id=3, 피박 800원 지불 + 광값 1,500원 지불 = -2,300원)
        // loser2(id=4, 일반 400원 지불 + 광값 1,500원 지불 = -1,900원)
        val seller = Gamer(id = 1, roundId = roundId, score = 3)
        val winner = Gamer(id = 2, roundId = roundId, score = 4)
        val loser1 = Gamer(id = 3, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 4, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(seller, winner, loser1, loser2))
        setupRules(gameId)

        useCase(gameId, roundId, seller = seller, winner = winner)

        assertEquals(4500, gamerRepository.getGamerAccount(1))
        assertEquals(-300, gamerRepository.getGamerAccount(2))
        assertEquals(-2300, gamerRepository.getGamerAccount(3))
        assertEquals(-1900, gamerRepository.getGamerAccount(4))
    }

    @Test
    fun `Oracle Case 13 - 맞고 1고 가산 1점 룰 및 피박`() = runTest {
        val gameId = 1L; val roundId = 13L
        // 1고 수식: (7점 + 1점) = 8점 -> 8점 * 100원 = 800원 -> 피박(2배) = 1,600원
        val winner = Gamer(id = 1, roundId = roundId, score = 7, go = 1)
        val loser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        setupRules(gameId)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(1600, gamerRepository.getGamerAccount(1))
        assertEquals(-1600, gamerRepository.getGamerAccount(2))
    }

    @Test
    fun `Oracle Case 14 - 커스텀 소액 판돈 50원 및 고액 뻑 1000원 정산`() = runTest {
        val gameId = 1L; val roundId = 14L
        // 승자 10점 (10 * 50원 = 500원) + 첫뻑(1,000원) = 1인당 1,500원씩 2명 = +3,000원
        val winner = Gamer(id = 1, roundId = roundId, score = 10, scoreOption = listOf(ScoreOption.FirstFuck))
        val loser1 = Gamer(id = 2, roundId = roundId)
        val loser2 = Gamer(id = 3, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser1, loser2))
        setupRules(gameId, score = 50, fuck = 1000)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(3000, gamerRepository.getGamerAccount(1))
        assertEquals(-1500, gamerRepository.getGamerAccount(2))
        assertEquals(-1500, gamerRepository.getGamerAccount(3))
    }

    @Test
    fun `Oracle Case 15 - 맞고 2인 2점 3고 멍박 광박 고박 8배 정산`() = runTest {
        val gameId = 1L; val roundId = 15L
        // 2인 맞고: (2점 + 3고) * 2(3고 배수) * 100원 = 1,000원 -> 멍박(2) * 광박(2) * 고박(2) = 8,000원
        val winner = Gamer(id = 1, roundId = roundId, score = 2, go = 3)
        val loser = Gamer(
            id = 2,
            roundId = roundId,
            loserOption = listOf(LoserOption.MongBak, LoserOption.LightBak, LoserOption.GoBak)
        )

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        setupRules(gameId, score = 100, sell = 0)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(8000, gamerRepository.getGamerAccount(1))
        assertEquals(-8000, gamerRepository.getGamerAccount(2))
    }

    @Test
    fun `Oracle Case 16 - 4인 중 1인 광팔기 0원 3인 플레이 2점 3고 멍박 광박 고박 독박 정산`() = runTest {
        val gameId = 1L; val roundId = 16L
        // 실기기 제보 시나리오: 점당 100원, 광팔기 0원, 4인 중 1명 광팜(3인 현역)
        // 승자: 2점 3고 = (2+3)*2*100 = 1,000원
        // 고박 패자(멍박+광박+고박): 본인 1,000 * 4(멍박·광박) = 4,000원 + 일반 패자 1,000원 대납 = -5,000원
        // 일반 패자(무박): 고박 대납으로 0원
        val seller = Gamer(id = 1, roundId = roundId, score = 2)
        val winner = Gamer(id = 2, roundId = roundId, score = 2, go = 3)
        val goBakLoser = Gamer(
            id = 3,
            roundId = roundId,
            loserOption = listOf(LoserOption.MongBak, LoserOption.LightBak, LoserOption.GoBak)
        )
        val exemptedLoser = Gamer(id = 4, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(seller, winner, goBakLoser, exemptedLoser))
        setupRules(gameId, score = 100, sell = 0)

        useCase(gameId, roundId, seller = seller, winner = winner)

        assertEquals(0, gamerRepository.getGamerAccount(1))
        assertEquals(5000, gamerRepository.getGamerAccount(2))
        assertEquals(-5000, gamerRepository.getGamerAccount(3))
        assertEquals(0, gamerRepository.getGamerAccount(4))
    }

    @Test
    fun `Oracle Case 17 - 맞고 2인 5점 흔들기 1회 폭탄 1회 피박 8배 정산`() = runTest {
        val gameId = 1L; val roundId = 17L
        // 2인 맞고: 5점 × 100원 × 흔들기1(2) × 폭탄1(2) = 2,000원, 피박(2) 적용 -> 4,000원
        val winner = Gamer(id = 1, roundId = roundId, score = 5, shakeCount = 1, bombCount = 1)
        val loser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        setupRules(gameId, score = 100, sell = 0)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(4000, gamerRepository.getGamerAccount(1))
        assertEquals(-4000, gamerRepository.getGamerAccount(2))
    }

    @Test
    fun `Oracle Case 18 - 고스톱 3인 2점 3고 흔들기1 폭탄1 고박 독박 정산`() = runTest {
        val gameId = 1L; val roundId = 18L
        // 기준 판돈 = (2+3)*2 * 100 * 4(흔들기·폭탄) = 4,000원
        // 패자1(무박): 원래 4,000원 -> 고박 대납으로 0원
        // 고박자(광박): 본인 4,000*2 = 8,000원 + 대납 4,000원 = -12,000원
        val winner = Gamer(id = 1, roundId = roundId, score = 2, go = 3, shakeCount = 1, bombCount = 1)
        val exemptedLoser = Gamer(id = 2, roundId = roundId)
        val goBakLoser = Gamer(
            id = 3,
            roundId = roundId,
            loserOption = listOf(LoserOption.LightBak, LoserOption.GoBak),
        )

        gamerRepository.setRoundGamers(roundId, listOf(winner, exemptedLoser, goBakLoser))
        setupRules(gameId, score = 100, sell = 0)

        useCase(gameId, roundId, seller = null, winner = winner)

        assertEquals(12000, gamerRepository.getGamerAccount(1))
        assertEquals(0, gamerRepository.getGamerAccount(2))
        assertEquals(-12000, gamerRepository.getGamerAccount(3))
    }

    @Test
    fun `Oracle Case 19 - 4인 1광팔이 첫뻑과 흔들기2 폭탄1 동시 정산 시 보조 정산은 배수 미적용`() = runTest {
        val gameId = 1L; val roundId = 19L
        // 점당 100, 뻑 300, 광팔기 200 (2장)
        // 승자(2점, 흔들기 2회 + 폭탄 1회 = ×8): 기준 판돈 2 * 100 * 8 = 1,600원
        // 패자 2명: 각 -1,600원 -> 승자 +3,200원
        // 첫 뻑(승자 보유): 300원 × 2명 = +600원 (흔들기·폭탄 ×8 미적용)
        // 광팔이(플레이어 4, 2장): 400원 × 3명 = +1,200원 (승자·패자2 각 -400원)
        val seller = Gamer(id = 4, roundId = roundId, score = 2)
        val winner = Gamer(
            id = 1,
            roundId = roundId,
            score = 2,
            shakeCount = 2,
            bombCount = 1,
            scoreOption = listOf(ScoreOption.FirstFuck),
        )
        val loser1 = Gamer(id = 2, roundId = roundId)
        val loser2 = Gamer(id = 3, roundId = roundId)

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser1, loser2, seller))
        setupRules(gameId, score = 100, fuck = 300, sell = 200)

        useCase(gameId, roundId, seller = seller, winner = winner)

        assertEquals(3200 + 600 - 400, gamerRepository.getGamerAccount(1))
        assertEquals(-1600 - 300 - 400, gamerRepository.getGamerAccount(2))
        assertEquals(-1600 - 300 - 400, gamerRepository.getGamerAccount(3))
        assertEquals(1200, gamerRepository.getGamerAccount(4))
    }
}
