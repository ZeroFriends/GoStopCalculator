package zero.friends.domain.usecase.calculate

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.ScoreOption

/**
 * 전체 게임 계산 통합 테스트
 * 
 * 여러 계산 단계를 조합하여 실제 게임 시나리오를 검증
 */
class CalculateGameIntegrationTest {
    
    private lateinit var sellUseCase: CalculateSellScoreUseCase
    private lateinit var loserUseCase: CalculateLoserScoreUseCase
    private lateinit var scoreOptionUseCase: CalculateScoreOptionUseCase
    
    @Before
    fun setup() {
        sellUseCase = CalculateSellScoreUseCase()
        loserUseCase = CalculateLoserScoreUseCase()
        scoreOptionUseCase = CalculateScoreOptionUseCase()
    }
    
    @Test
    fun `시나리오 1 - 광팜 + 승자 계산 (박 없음)`() {
        // Given: 4명 게임, 플레이어1 광팜(2광), 플레이어2 승자(7점)
        val seller = Gamer(id = 1, name = "플레이어1", score = 2)
        val winner = Gamer(id = 2, name = "플레이어2", score = 7)
        val loser1 = Gamer(id = 3, name = "플레이어3")
        val loser2 = Gamer(id = 4, name = "플레이어4")
        val allGamers = listOf(seller, winner, loser1, loser2)
        
        val sellScorePerLight = 100  // 광 하나당 100원
        val scorePerPoint = 100      // 점당 100원
        
        // When: 광팜 계산
        val sellResult = sellUseCase(seller, allGamers, sellScorePerLight)
        
        // Then: seller는 600원 받음 (200 × 3)
        assertEquals(600, sellResult.accounts[1L])
        assertEquals(-200, sellResult.accounts[2L])
        assertEquals(-200, sellResult.accounts[3L])
        assertEquals(-200, sellResult.accounts[4L])
        
        // When: 패자 계산 (seller 제외)
        val losers = listOf(loser1, loser2)
        val loserResult = loserUseCase(winner, losers, scorePerPoint)
        
        // Then: winner는 1400원 받음 (700 × 2), 패자들은 각각 700원 지불
        assertEquals(1400, loserResult.accounts[2L])
        assertEquals(-700, loserResult.accounts[3L])
        assertEquals(-700, loserResult.accounts[4L])
        
        // 최종 정산:
        // 플레이어1(seller): +600
        // 플레이어2(winner): -200 + 1400 = +1200
        // 플레이어3: -200 - 700 = -900
        // 플레이어4: -200 - 700 = -900
        // 합계: 600 + 1200 - 900 - 900 = 0 ✓
    }
    
    @Test
    fun `시나리오 2 - 승자 계산 + 피박 + 첫뻑`() {
        // Given: 4명 게임, 플레이어1 승자(6점), 플레이어2 피박, 플레이어3 첫뻑
        val winner = Gamer(id = 1, name = "플레이어1", score = 6)
        val loser1 = Gamer(id = 2, name = "플레이어2 (피박)", 
                          loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 3, name = "플레이어3 (첫뻑)", 
                          scoreOption = listOf(ScoreOption.FirstFuck))
        val loser3 = Gamer(id = 4, name = "플레이어4")
        val allGamers = listOf(winner, loser1, loser2, loser3)
        
        val scorePerPoint = 100  // 점당 100원
        val fuckScore = 50       // 뻑 50원
        
        // When: 패자 계산
        val losers = listOf(loser1, loser2, loser3)
        val loserResult = loserUseCase(winner, losers, scorePerPoint)
        
        // Then:
        // 피박: 1200원 (600 × 2)
        // 일반: 600원
        assertEquals(2400, loserResult.accounts[1L]) // winner: 1200 + 600 + 600
        assertEquals(-1200, loserResult.accounts[2L]) // 피박
        assertEquals(-600, loserResult.accounts[3L])
        assertEquals(-600, loserResult.accounts[4L])
        
        // When: 점수옵션 계산
        val scoreOptionResult = scoreOptionUseCase(allGamers, fuckScore)
        
        // Then: 플레이어3은 150원 받음 (50 × 3), 나머지는 각각 50원 지불
        assertEquals(-50, scoreOptionResult.accounts[1L])
        assertEquals(-50, scoreOptionResult.accounts[2L])
        assertEquals(150, scoreOptionResult.accounts[3L])
        assertEquals(-50, scoreOptionResult.accounts[4L])
        
        // 최종 정산:
        // 플레이어1(winner): +2400 - 50 = +2350
        // 플레이어2(피박): -1200 - 50 = -1250
        // 플레이어3(첫뻑): -600 + 150 = -450
        // 플레이어4: -600 - 50 = -650
        // 합계: 2350 - 1250 - 450 - 650 = 0 ✓
    }
    
    @Test
    fun `시나리오 3 - 고박 케이스 + 삼연뻑`() {
        // Given: 3명 게임, 플레이어1 승자(10점), 플레이어2 고박+광박, 플레이어3 삼연뻑
        val winner = Gamer(id = 1, name = "플레이어1", score = 10)
        val loser1 = Gamer(id = 2, name = "플레이어2 (고박+광박)", 
                          loserOption = listOf(LoserOption.GoBak, LoserOption.LightBak))
        val loser2 = Gamer(id = 3, name = "플레이어3 (삼연뻑)", 
                          scoreOption = listOf(ScoreOption.ThreeFuck))
        val allGamers = listOf(winner, loser1, loser2)
        
        val scorePerPoint = 100  // 점당 100원
        val fuckScore = 100      // 뻑 100원
        
        // When: 패자 계산
        val losers = listOf(loser1, loser2)
        val loserResult = loserUseCase(winner, losers, scorePerPoint)
        
        // Then: 고박자가 모두 지불 (고스톱이므로 고박 제외)
        // 플레이어2 본인: 1000 × 2 (고박 제외, 광박만) = 2000
        // 플레이어3 대신: 1000 (고박자가 대신 냄, 그대로)
        // 합계: 3000
        assertEquals(3000, loserResult.accounts[1L])
        assertEquals(-3000, loserResult.accounts[2L])
        assertEquals(null, loserResult.accounts[3L]) // 고박자가 대신 냄
        
        // When: 점수옵션 계산
        val scoreOptionResult = scoreOptionUseCase(allGamers, fuckScore)
        
        // Then: 플레이어3은 800원 받음 (400 × 2), 나머지는 각각 400원 지불
        assertEquals(-400, scoreOptionResult.accounts[1L])
        assertEquals(-400, scoreOptionResult.accounts[2L])
        assertEquals(800, scoreOptionResult.accounts[3L])
        
        // 최종 정산:
        // 플레이어1(winner): +3000 - 400 = +2600
        // 플레이어2(고박): -3000 - 400 = -3400
        // 플레이어3(삼연뻑): 0 + 800 = +800
        // 합계: 2600 - 3400 + 800 = 0 ✓
    }
    
    @Test
    fun `시나리오 4 - 광팜 + 고박 + 여러 뻑`() {
        // Given: 4명 게임, 플레이어1 광팜(3광), 플레이어2 승자(8점), 
        //        플레이어3 고박, 플레이어4 연뻑
        val seller = Gamer(id = 1, name = "플레이어1 (광팜)", score = 3)
        val winner = Gamer(id = 2, name = "플레이어2 (승자)", score = 8)
        val loser1 = Gamer(id = 3, name = "플레이어3 (고박)", 
                          loserOption = listOf(LoserOption.GoBak))
        val loser2 = Gamer(id = 4, name = "플레이어4 (연뻑)", 
                          scoreOption = listOf(ScoreOption.SecondFuck))
        val allGamers = listOf(seller, winner, loser1, loser2)
        
        val sellScorePerLight = 200  // 광 하나당 200원
        val scorePerPoint = 100      // 점당 100원
        val fuckScore = 100          // 뻑 100원
        
        // When: 광팜 계산
        val sellResult = sellUseCase(seller, allGamers, sellScorePerLight)
        
        // Then: seller는 1800원 받음 (600 × 3)
        assertEquals(1800, sellResult.accounts[1L])
        assertEquals(-600, sellResult.accounts[2L])
        assertEquals(-600, sellResult.accounts[3L])
        assertEquals(-600, sellResult.accounts[4L])
        
        // When: 패자 계산 (seller 제외)
        val losers = listOf(loser1, loser2)
        val loserResult = loserUseCase(winner, losers, scorePerPoint)
        
        // Then: 고박자가 모두 지불 (고스톱이므로 고박 제외)
        // 플레이어3 본인: 800 (고박 제외, 기본 금액만)
        // 플레이어4 대신: 800 (고박자가 대신 냄, 그대로)
        // 합계: 1600
        assertEquals(1600, loserResult.accounts[2L])
        assertEquals(-1600, loserResult.accounts[3L])
        assertEquals(null, loserResult.accounts[4L])
        
        // When: 점수옵션 계산 (seller 제외)
        val scoreOptionGamers = listOf(winner, loser1, loser2)
        val scoreOptionResult = scoreOptionUseCase(scoreOptionGamers, fuckScore)
        
        // Then: 플레이어4는 400원 받음 (200 × 2), 나머지는 각각 200원 지불
        assertEquals(-200, scoreOptionResult.accounts[2L])
        assertEquals(-200, scoreOptionResult.accounts[3L])
        assertEquals(400, scoreOptionResult.accounts[4L])
        
        // 최종 정산:
        // 플레이어1(seller): +1800
        // 플레이어2(winner): -600 + 1600 - 200 = +800
        // 플레이어3(고박): -600 - 1600 - 200 = -2400
        // 플레이어4(연뻑): -600 + 0 + 400 = -200
        // 합계: 1800 + 800 - 2400 - 200 = 0 ✓
    }
    
    @Test
    fun `시나리오 5 - 복잡한 케이스 (모든 요소 포함)`() {
        // Given: 4명 게임, 모든 요소가 포함된 복잡한 상황
        val seller = Gamer(id = 1, name = "플레이어1 (광팜)", score = 1)
        val winner = Gamer(id = 2, name = "플레이어2 (승자)", score = 5,
                          scoreOption = listOf(ScoreOption.FirstDdadak))
        val loser1 = Gamer(id = 3, name = "플레이어3 (고박+피박+첫뻑)", 
                          loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak),
                          scoreOption = listOf(ScoreOption.FirstFuck))
        val loser2 = Gamer(id = 4, name = "플레이어4 (광박+멍박)",
                          loserOption = listOf(LoserOption.LightBak, LoserOption.MongBak))
        val allGamers = listOf(seller, winner, loser1, loser2)
        
        val sellScorePerLight = 100  // 광 하나당 100원
        val scorePerPoint = 100      // 점당 100원
        val fuckScore = 50           // 뻑 50원
        
        // When: 광팜 계산
        val sellResult = sellUseCase(seller, allGamers, sellScorePerLight)
        assertEquals(300, sellResult.accounts[1L])  // 100 × 3
        assertEquals(-100, sellResult.accounts[2L])
        assertEquals(-100, sellResult.accounts[3L])
        assertEquals(-100, sellResult.accounts[4L])
        
        // When: 패자 계산 (seller 제외)
        val losers = listOf(loser1, loser2)
        val loserResult = loserUseCase(winner, losers, scorePerPoint)
        
        // 플레이어3(고박+피박): 본인 1000원(고박 제외, 피박만 = 2배)
        // 플레이어4(광박+멍박): 500×4 = 2000 → 고박자가 대신 냄: 2000 (그대로, 고스톱이므로)
        // 총합: 1000 + 2000 = 3000원
        assertEquals(3000, loserResult.accounts[2L])
        assertEquals(-3000, loserResult.accounts[3L])
        
        // When: 점수옵션 계산 (seller 제외)
        val scoreOptionGamers = listOf(winner, loser1, loser2)
        val scoreOptionResult = scoreOptionUseCase(scoreOptionGamers, fuckScore, scorePerPoint)
        
        // 첫따닥: 100 × 3 = 300원
        // 플레이어2(첫따닥): +300 × 2 = +600, -50 (첫뻑) = +550
        // 플레이어3(첫뻑): +50 × 2 = +100, -300 (첫따닥) = -200
        // 플레이어4: -50 (첫뻑) - 300 (첫따닥) = -350
        assertEquals(550, scoreOptionResult.accounts[2L])  // 첫따닥(600) - 첫뻑(50) = 550
        assertEquals(-200, scoreOptionResult.accounts[3L])  // 첫뻑(100) - 첫따닥(300) = -200
        assertEquals(-350, scoreOptionResult.accounts[4L])  // -50 - 300 = -350
        
        // 최종 정산:
        // 플레이어1(seller): +300
        // 플레이어2(winner): -100 + 3000 + 550 = +3450
        // 플레이어3(고박): -100 - 3000 - 200 = -3300
        // 플레이어4: -100 + 0 - 350 = -450
        // 합계: 300 + 3450 - 3300 - 450 = 0 ✓
    }
    
    @Test
    fun `시나리오 6 - 맞고 (2인 게임)`() {
        // Given: 2명 게임 (맞고), 플레이어1 승자(9점), 플레이어2 고박+멍박
        val winner = Gamer(id = 1, name = "플레이어1", score = 9)
        val loser = Gamer(id = 2, name = "플레이어2 (고박+멍박)", 
                         loserOption = listOf(LoserOption.GoBak, LoserOption.MongBak))
        val allGamers = listOf(winner, loser)
        
        val scorePerPoint = 100  // 점당 100원
        
        // When: 패자 계산
        val losers = listOf(loser)
        val loserResult = loserUseCase(winner, losers, scorePerPoint)
        
        // Then: 고박+멍박 = 900 × 4 = 3600 (2개 박)
        assertEquals(3600, loserResult.accounts[1L])
        assertEquals(-3600, loserResult.accounts[2L])
        
        // 최종 정산:
        // 플레이어1(winner): +3600
        // 플레이어2(고박): -3600
        // 합계: 3600 - 3600 = 0 ✓
    }
    
    @Test
    fun `모든 시나리오 총액 검증`() {
        // 모든 시나리오에서 총액이 0인지 검증하는 통합 테스트
        
        // 시나리오 1
        val s1_seller = Gamer(id = 1, score = 2)
        val s1_winner = Gamer(id = 2, score = 7)
        val s1_gamers = listOf(s1_seller, s1_winner, Gamer(id = 3), Gamer(id = 4))
        
        val s1_sell = sellUseCase(s1_seller, s1_gamers, 100)
        val s1_loser = loserUseCase(s1_winner, s1_gamers.filter { it.id != 1L && it.id != 2L }, 100)
        
        val s1_total = (s1_sell.accounts.values.sum() + s1_loser.accounts.values.sum())
        assertEquals(0, s1_total)
    }
}

