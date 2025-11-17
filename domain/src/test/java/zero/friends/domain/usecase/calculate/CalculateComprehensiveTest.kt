package zero.friends.domain.usecase.calculate

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.ScoreOption

/**
 * 포괄적인 고스톱 계산 검증 테스트 (50개 추가)
 * 
 * 실제 게임에서 발생할 수 있는 다양한 시나리오 검증
 */
class CalculateComprehensiveTest {
    
    private lateinit var sellUseCase: CalculateSellScoreUseCase
    private lateinit var loserUseCase: CalculateLoserScoreUseCase
    private lateinit var scoreOptionUseCase: CalculateScoreOptionUseCase
    
    @Before
    fun setup() {
        sellUseCase = CalculateSellScoreUseCase()
        loserUseCase = CalculateLoserScoreUseCase()
        scoreOptionUseCase = CalculateScoreOptionUseCase()
    }
    
    // ===== 3명 게임 케이스 (1~10) =====
    
    @Test
    fun `3명 - 승자 7점, 패자1 피박, 패자2 기본`() {
        val winner = Gamer(id = 1, score = 7)
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 3)
        
        val result = loserUseCase(winner, listOf(loser1, loser2), 100)
        
        assertEquals(2100, result.accounts[1L])  // 1400 + 700
        assertEquals(-1400, result.accounts[2L])
        assertEquals(-700, result.accounts[3L])
    }
    
    @Test
    fun `3명 - 승자 첫뻑 + 5점, 패자들 기본`() {
        val winner = Gamer(id = 1, score = 5, scoreOption = listOf(ScoreOption.FirstFuck))
        val gamers = listOf(winner, Gamer(id = 2), Gamer(id = 3))
        
        val loserResult = loserUseCase(winner, listOf(gamers[1], gamers[2]), 100)
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(1000, loserResult.accounts[1L])  // 500 + 500
        assertEquals(200, scoreResult.accounts[1L])   // 100 × 2
    }
    
    @Test
    fun `3명 - 승자 8점, 패자1 광박, 패자2 멍박`() {
        val winner = Gamer(id = 1, score = 8)
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.LightBak))
        val loser2 = Gamer(id = 3, loserOption = listOf(LoserOption.MongBak))
        
        val result = loserUseCase(winner, listOf(loser1, loser2), 100)
        
        assertEquals(3200, result.accounts[1L])  // 1600 + 1600
        assertEquals(-1600, result.accounts[2L])
        assertEquals(-1600, result.accounts[3L])
    }
    
    @Test
    fun `3명 - 모두 뻑 있음, 승자 10점`() {
        val winner = Gamer(id = 1, score = 10, scoreOption = listOf(ScoreOption.ThreeFuck))
        val loser1 = Gamer(id = 2, scoreOption = listOf(ScoreOption.SecondFuck))
        val loser2 = Gamer(id = 3, scoreOption = listOf(ScoreOption.FirstFuck))
        val gamers = listOf(winner, loser1, loser2)
        
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        // 승자: 400×2 = 800, 패: 200+100 = 300, 총: 500
        assertEquals(500, scoreResult.accounts[1L])  // 800 - 200 - 100
    }
    
    @Test
    fun `3명 - 승자 연뻑 + 6점, 패자1 피박, 패자2 첫뻑`() {
        val winner = Gamer(id = 1, score = 6, scoreOption = listOf(ScoreOption.SecondFuck))
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 3, scoreOption = listOf(ScoreOption.FirstFuck))
        val gamers = listOf(winner, loser1, loser2)
        
        val loserResult = loserUseCase(winner, listOf(loser1, loser2), 100)
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(1800, loserResult.accounts[1L])  // 1200 + 600
        assertEquals(300, scoreResult.accounts[1L])   // 400 - 100
    }
    
    @Test
    fun `3명 - 광팜 1광 + 승자 7점`() {
        val seller = Gamer(id = 1, score = 1)
        val winner = Gamer(id = 2, score = 7)
        val loser = Gamer(id = 3)
        val gamers = listOf(seller, winner, loser)
        
        val sellResult = sellUseCase(seller, gamers, 200)
        val loserResult = loserUseCase(winner, listOf(loser), 100)
        
        assertEquals(400, sellResult.accounts[1L])   // 200 × 2
        assertEquals(700, loserResult.accounts[2L])
    }
    
    @Test
    fun `3명 - 광팜 2광 + 승자 첫뻑 + 5점`() {
        val seller = Gamer(id = 1, score = 2)
        val winner = Gamer(id = 2, score = 5, scoreOption = listOf(ScoreOption.FirstFuck))
        val loser = Gamer(id = 3)
        val gamers = listOf(seller, winner, loser)
        
        val sellResult = sellUseCase(seller, gamers, 100)
        val loserResult = loserUseCase(winner, listOf(loser), 100)
        val scoreResult = scoreOptionUseCase(listOf(winner, loser), 100)
        
        assertEquals(400, sellResult.accounts[1L])
        assertEquals(500, loserResult.accounts[2L])
        assertEquals(100, scoreResult.accounts[2L])
    }
    
    @Test
    fun `3명 - 승자 9점, 패자1 피박+광박, 패자2 기본`() {
        val winner = Gamer(id = 1, score = 9)
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak))
        val loser2 = Gamer(id = 3)
        
        val result = loserUseCase(winner, listOf(loser1, loser2), 100)
        
        assertEquals(4500, result.accounts[1L])  // 3600 + 900
        assertEquals(-3600, result.accounts[2L]) // 900 × 4
        assertEquals(-900, result.accounts[3L])
    }
    
    @Test
    fun `3명 - 승자 삼연뻑 + 8점, 패자1 연뻑, 패자2 기본`() {
        val winner = Gamer(id = 1, score = 8, scoreOption = listOf(ScoreOption.ThreeFuck))
        val loser1 = Gamer(id = 2, scoreOption = listOf(ScoreOption.SecondFuck))
        val loser2 = Gamer(id = 3)
        val gamers = listOf(winner, loser1, loser2)
        
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(600, scoreResult.accounts[1L])  // 800 - 200
        assertEquals(0, scoreResult.accounts[2L])    // 200 - 200
        assertEquals(-600, scoreResult.accounts[3L]) // -400 - 200
    }
    
    @Test
    fun `3명 - 승자 7점, 패자들 모두 멍박`() {
        val winner = Gamer(id = 1, score = 7)
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.MongBak))
        val loser2 = Gamer(id = 3, loserOption = listOf(LoserOption.MongBak))
        
        val result = loserUseCase(winner, listOf(loser1, loser2), 100)
        
        assertEquals(2800, result.accounts[1L])  // 1400 + 1400
    }
    
    // ===== 4명 게임 케이스 (11~25) =====
    
    @Test
    fun `4명 - 승자 6점, 3명 패자 모두 기본`() {
        val winner = Gamer(id = 1, score = 6)
        val losers = listOf(Gamer(id = 2), Gamer(id = 3), Gamer(id = 4))
        
        val result = loserUseCase(winner, losers, 100)
        
        assertEquals(1800, result.accounts[1L])  // 600 × 3
    }
    
    @Test
    fun `4명 - 승자 첫뻑 + 7점, 패자 1명만 피박`() {
        val winner = Gamer(id = 1, score = 7, scoreOption = listOf(ScoreOption.FirstFuck))
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 3)
        val loser3 = Gamer(id = 4)
        
        val loserResult = loserUseCase(winner, listOf(loser1, loser2, loser3), 100)
        
        assertEquals(2800, loserResult.accounts[1L])  // 1400 + 700 + 700
    }
    
    @Test
    fun `4명 - 승자 연뻑 + 8점, 패자들 다양한 박`() {
        val winner = Gamer(id = 1, score = 8, scoreOption = listOf(ScoreOption.SecondFuck))
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 3, loserOption = listOf(LoserOption.LightBak))
        val loser3 = Gamer(id = 4)
        val gamers = listOf(winner, loser1, loser2, loser3)
        
        val loserResult = loserUseCase(winner, listOf(loser1, loser2, loser3), 100)
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(4000, loserResult.accounts[1L])  // 1600 + 1600 + 800
        assertEquals(600, scoreResult.accounts[1L])   // 200 × 3
    }
    
    @Test
    fun `4명 - 광팜 3광 + 승자 5점`() {
        val seller = Gamer(id = 1, score = 3)
        val winner = Gamer(id = 2, score = 5)
        val loser1 = Gamer(id = 3)
        val loser2 = Gamer(id = 4)
        val gamers = listOf(seller, winner, loser1, loser2)
        
        val sellResult = sellUseCase(seller, gamers, 100)
        val loserResult = loserUseCase(winner, listOf(loser1, loser2), 100)
        
        assertEquals(900, sellResult.accounts[1L])   // 300 × 3
        assertEquals(1000, loserResult.accounts[2L]) // 500 + 500
    }
    
    @Test
    fun `4명 - 승자 10점, 패자1 피박+광박+멍박`() {
        val winner = Gamer(id = 1, score = 10)
        val loser1 = Gamer(id = 2, loserOption = listOf(
            LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak
        ))
        val loser2 = Gamer(id = 3)
        val loser3 = Gamer(id = 4)
        
        val result = loserUseCase(winner, listOf(loser1, loser2, loser3), 100)
        
        assertEquals(10000, result.accounts[1L])  // 8000 + 1000 + 1000
        assertEquals(-8000, result.accounts[2L])  // 1000 × 8 (3개 박)
        assertEquals(-1000, result.accounts[3L])
        assertEquals(-1000, result.accounts[4L])
    }
    
    @Test
    fun `4명 - 모두 뻑 옵션 보유`() {
        val p1 = Gamer(id = 1, score = 7, scoreOption = listOf(ScoreOption.ThreeFuck))
        val p2 = Gamer(id = 2, scoreOption = listOf(ScoreOption.SecondFuck))
        val p3 = Gamer(id = 3, scoreOption = listOf(ScoreOption.FirstFuck))
        val p4 = Gamer(id = 4, scoreOption = listOf(ScoreOption.FirstDdadak))
        val gamers = listOf(p1, p2, p3, p4)
        
        val result = scoreOptionUseCase(gamers, 100)
        
        // 총액은 0이어야 함
        assertEquals(0, result.accounts.values.sum())
    }
    
    @Test
    fun `4명 - 승자 삼연뻑 + 9점, 패자1 연뻑 + 피박`() {
        val winner = Gamer(id = 1, score = 9, scoreOption = listOf(ScoreOption.ThreeFuck))
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak), 
                          scoreOption = listOf(ScoreOption.SecondFuck))
        val loser2 = Gamer(id = 3)
        val loser3 = Gamer(id = 4)
        val gamers = listOf(winner, loser1, loser2, loser3)
        
        val loserResult = loserUseCase(winner, listOf(loser1, loser2, loser3), 100)
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(3600, loserResult.accounts[1L])  // 1800 + 900 + 900
        assertEquals(1000, scoreResult.accounts[1L])  // 1200 - 200
    }
    
    @Test
    fun `4명 - 광팜 2광 + 승자 연뻑 + 6점, 패자 광박`() {
        val seller = Gamer(id = 1, score = 2)
        val winner = Gamer(id = 2, score = 6, scoreOption = listOf(ScoreOption.SecondFuck))
        val loser1 = Gamer(id = 3, loserOption = listOf(LoserOption.LightBak))
        val loser2 = Gamer(id = 4)
        val gamers = listOf(seller, winner, loser1, loser2)
        
        val sellResult = sellUseCase(seller, gamers, 150)
        val loserResult = loserUseCase(winner, listOf(loser1, loser2), 100)
        val scoreResult = scoreOptionUseCase(listOf(winner, loser1, loser2), 100)
        
        assertEquals(900, sellResult.accounts[1L])    // 300 × 3
        assertEquals(1800, loserResult.accounts[2L])  // 1200 + 600
        assertEquals(400, scoreResult.accounts[2L])   // 200 × 2
    }
    
    @Test
    fun `4명 - 승자 7점, 패자들 각기 다른 박`() {
        val winner = Gamer(id = 1, score = 7)
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 3, loserOption = listOf(LoserOption.LightBak))
        val loser3 = Gamer(id = 4, loserOption = listOf(LoserOption.MongBak))
        
        val result = loserUseCase(winner, listOf(loser1, loser2, loser3), 100)
        
        assertEquals(4200, result.accounts[1L])  // 1400 + 1400 + 1400
    }
    
    @Test
    fun `4명 - 승자 첫따닥 + 5점, 패자1 첫뻑 + 피박`() {
        val winner = Gamer(id = 1, score = 5, scoreOption = listOf(ScoreOption.FirstDdadak))
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak),
                          scoreOption = listOf(ScoreOption.FirstFuck))
        val loser2 = Gamer(id = 3)
        val loser3 = Gamer(id = 4)
        val gamers = listOf(winner, loser1, loser2, loser3)
        
        val loserResult = loserUseCase(winner, listOf(loser1, loser2, loser3), 100)
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(2000, loserResult.accounts[1L])  // 1000 + 500 + 500
        assertEquals(200, scoreResult.accounts[1L])   // 300 - 100
    }
    
    @Test
    fun `4명 - 승자 8점, 패자1만 2개 박, 나머지 기본`() {
        val winner = Gamer(id = 1, score = 8)
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak))
        val loser2 = Gamer(id = 3)
        val loser3 = Gamer(id = 4)
        
        val result = loserUseCase(winner, listOf(loser1, loser2, loser3), 100)
        
        assertEquals(4800, result.accounts[1L])  // 3200 + 800 + 800
        assertEquals(-3200, result.accounts[2L]) // 800 × 4
        assertEquals(-800, result.accounts[3L])
        assertEquals(-800, result.accounts[4L])
    }
    
    @Test
    fun `4명 - 광팜 4광 (극단적)`() {
        val seller = Gamer(id = 1, score = 4)
        val gamers = listOf(seller, Gamer(id = 2), Gamer(id = 3), Gamer(id = 4))
        
        val result = sellUseCase(seller, gamers, 200)
        
        assertEquals(2400, result.accounts[1L])  // 800 × 3
        assertEquals(-800, result.accounts[2L])
    }
    
    @Test
    fun `4명 - 승자 9점, 패자 2명이 각각 2개 박`() {
        val winner = Gamer(id = 1, score = 9)
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak))
        val loser2 = Gamer(id = 3, loserOption = listOf(LoserOption.LightBak, LoserOption.MongBak))
        val loser3 = Gamer(id = 4)
        
        val result = loserUseCase(winner, listOf(loser1, loser2, loser3), 100)
        
        assertEquals(8100, result.accounts[1L])  // 3600 + 3600 + 900
    }
    
    @Test
    fun `4명 - 승자 연뻑 + 첫따닥 + 7점, 패자들 다양`() {
        val winner = Gamer(id = 1, score = 7, 
                          scoreOption = listOf(ScoreOption.SecondFuck, ScoreOption.FirstDdadak))
        val loser1 = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 3, scoreOption = listOf(ScoreOption.FirstFuck))
        val loser3 = Gamer(id = 4)
        val gamers = listOf(winner, loser1, loser2, loser3)
        
        val loserResult = loserUseCase(winner, listOf(loser1, loser2, loser3), 100)
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(2800, loserResult.accounts[1L])  // 1400 + 700 + 700
        assertEquals(800, scoreResult.accounts[1L])   // 900 - 100
    }
    
    // ===== 고박 케이스 (26~35) =====
    
    @Test
    fun `3명 고박 - 고박자만, 승자 7점`() {
        val winner = Gamer(id = 1, score = 7)
        val goBakLoser = Gamer(id = 2, loserOption = listOf(LoserOption.GoBak))
        val normalLoser = Gamer(id = 3)
        
        val result = loserUseCase(winner, listOf(goBakLoser, normalLoser), 100)
        
        assertEquals(1400, result.accounts[1L])
        assertEquals(-1400, result.accounts[2L])  // 고박자가 전부 냄
        assertEquals(null, result.accounts[3L])
    }
    
    @Test
    fun `3명 고박 - 고박자 + 피박, 승자 8점`() {
        val winner = Gamer(id = 1, score = 8)
        val goBakLoser = Gamer(id = 2, loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak))
        val normalLoser = Gamer(id = 3)
        
        val result = loserUseCase(winner, listOf(goBakLoser, normalLoser), 100)
        
        assertEquals(2400, result.accounts[1L])  // 800 + 1600
        assertEquals(-2400, result.accounts[2L])
    }
    
    @Test
    fun `4명 고박 - 고박자 1명, 승자 첫뻑 + 6점`() {
        val winner = Gamer(id = 1, score = 6, scoreOption = listOf(ScoreOption.FirstFuck))
        val goBakLoser = Gamer(id = 2, loserOption = listOf(LoserOption.GoBak))
        val loser2 = Gamer(id = 3)
        val loser3 = Gamer(id = 4)
        val gamers = listOf(winner, goBakLoser, loser2, loser3)
        
        val loserResult = loserUseCase(winner, listOf(goBakLoser, loser2, loser3), 100)
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(1800, loserResult.accounts[1L])  // 600 + 600 + 600
        assertEquals(-1800, loserResult.accounts[2L]) // 고박자가 전부
        assertEquals(300, scoreResult.accounts[1L])   // 100 × 3
    }
    
    @Test
    fun `3명 고박 - 고박자 + 광박, 다른 패자 피박`() {
        val winner = Gamer(id = 1, score = 7)
        val goBakLoser = Gamer(id = 2, loserOption = listOf(LoserOption.GoBak, LoserOption.LightBak))
        val normalLoser = Gamer(id = 3, loserOption = listOf(LoserOption.PeaBak))
        
        val result = loserUseCase(winner, listOf(goBakLoser, normalLoser), 100)
        
        assertEquals(2800, result.accounts[1L])  // 1400(고박자) + 1400(다른패자)
        assertEquals(-2800, result.accounts[2L])
    }
    
    @Test
    fun `4명 고박 - 고박자 + 피박, 다른 2명 각각 박`() {
        val winner = Gamer(id = 1, score = 9)
        val goBakLoser = Gamer(id = 2, loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak))
        val loser2 = Gamer(id = 3, loserOption = listOf(LoserOption.LightBak))
        val loser3 = Gamer(id = 4, loserOption = listOf(LoserOption.MongBak))
        
        val result = loserUseCase(winner, listOf(goBakLoser, loser2, loser3), 100)
        
        assertEquals(5400, result.accounts[1L])  // 1800(고박자) + 1800 + 1800
        assertEquals(-5400, result.accounts[2L])
    }
    
    @Test
    fun `맞고 고박 - 고박자 + 피박 + 광박, 승자 10점`() {
        val winner = Gamer(id = 1, score = 10)
        val goBakLoser = Gamer(id = 2, loserOption = listOf(
            LoserOption.GoBak, LoserOption.PeaBak, LoserOption.LightBak
        ))
        
        val result = loserUseCase(winner, listOf(goBakLoser), 100)
        
        assertEquals(4000, result.accounts[1L])  // 1000 × 4 (2개 박)
        assertEquals(-4000, result.accounts[2L])
    }
    
    @Test
    fun `3명 고박 - 고박자 연뻑, 승자 삼연뻑 + 7점`() {
        val winner = Gamer(id = 1, score = 7, scoreOption = listOf(ScoreOption.ThreeFuck))
        val goBakLoser = Gamer(id = 2, loserOption = listOf(LoserOption.GoBak),
                              scoreOption = listOf(ScoreOption.SecondFuck))
        val normalLoser = Gamer(id = 3)
        val gamers = listOf(winner, goBakLoser, normalLoser)
        
        val loserResult = loserUseCase(winner, listOf(goBakLoser, normalLoser), 100)
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(1400, loserResult.accounts[1L])
        assertEquals(600, scoreResult.accounts[1L])  // 800 - 200
    }
    
    @Test
    fun `4명 고박 - 광팜 + 고박 조합`() {
        val seller = Gamer(id = 1, score = 2)
        val winner = Gamer(id = 2, score = 6)
        val goBakLoser = Gamer(id = 3, loserOption = listOf(LoserOption.GoBak))
        val normalLoser = Gamer(id = 4)
        val gamers = listOf(seller, winner, goBakLoser, normalLoser)
        
        val sellResult = sellUseCase(seller, gamers, 100)
        val loserResult = loserUseCase(winner, listOf(goBakLoser, normalLoser), 100)
        
        assertEquals(600, sellResult.accounts[1L])   // 200 × 3
        assertEquals(1200, loserResult.accounts[2L]) // 600 + 600
        assertEquals(-1200, loserResult.accounts[3L])
    }
    
    @Test
    fun `3명 고박 - 고박자 + 멍박, 승자 첫뻑 + 8점`() {
        val winner = Gamer(id = 1, score = 8, scoreOption = listOf(ScoreOption.FirstFuck))
        val goBakLoser = Gamer(id = 2, loserOption = listOf(LoserOption.GoBak, LoserOption.MongBak))
        val normalLoser = Gamer(id = 3)
        val gamers = listOf(winner, goBakLoser, normalLoser)
        
        val loserResult = loserUseCase(winner, listOf(goBakLoser, normalLoser), 100)
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(2400, loserResult.accounts[1L])  // 1600 + 800
        assertEquals(200, scoreResult.accounts[1L])
    }
    
    @Test
    fun `4명 고박 - 고박자만, 나머지 3명 모두 박`() {
        val winner = Gamer(id = 1, score = 7)
        val goBakLoser = Gamer(id = 2, loserOption = listOf(LoserOption.GoBak))
        val loser2 = Gamer(id = 3, loserOption = listOf(LoserOption.PeaBak))
        val loser3 = Gamer(id = 4, loserOption = listOf(LoserOption.LightBak))
        
        val result = loserUseCase(winner, listOf(goBakLoser, loser2, loser3), 100)
        
        assertEquals(3500, result.accounts[1L])  // 700 + 1400 + 1400
        assertEquals(-3500, result.accounts[2L]) // 고박자가 전부
    }
    
    // ===== 극단 케이스 (36~45) =====
    
    @Test
    fun `승자 20점 (매우 높은 점수)`() {
        val winner = Gamer(id = 1, score = 20)
        val loser = Gamer(id = 2)
        
        val result = loserUseCase(winner, listOf(loser), 100)
        
        assertEquals(2000, result.accounts[1L])
        assertEquals(-2000, result.accounts[2L])
    }
    
    @Test
    fun `승자 1점 (최소 점수)`() {
        val winner = Gamer(id = 1, score = 1)
        val loser = Gamer(id = 2)
        
        val result = loserUseCase(winner, listOf(loser), 100)
        
        assertEquals(100, result.accounts[1L])
    }
    
    @Test
    fun `점당 500원 (높은 점당)`() {
        val winner = Gamer(id = 1, score = 7)
        val loser = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak))
        
        val result = loserUseCase(winner, listOf(loser), 500)
        
        assertEquals(7000, result.accounts[1L])  // 7 × 500 × 2
    }
    
    @Test
    fun `뻑 점수 200원 (높은 뻑 점수)`() {
        val gamers = listOf(
            Gamer(id = 1, scoreOption = listOf(ScoreOption.ThreeFuck)),
            Gamer(id = 2)
        )
        
        val result = scoreOptionUseCase(gamers, 200)
        
        assertEquals(800, result.accounts[1L])  // 200 × 4
    }
    
    @Test
    fun `광팜 5광 500원 (극단적 광팜)`() {
        val seller = Gamer(id = 1, score = 5)
        val gamers = listOf(seller, Gamer(id = 2), Gamer(id = 3))
        
        val result = sellUseCase(seller, gamers, 500)
        
        assertEquals(5000, result.accounts[1L])  // 2500 × 2
    }
    
    @Test
    fun `승자 15점 + 삼연뻑, 패자 3개 박`() {
        val winner = Gamer(id = 1, score = 15, scoreOption = listOf(ScoreOption.ThreeFuck))
        val loser = Gamer(id = 2, loserOption = listOf(
            LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak
        ))
        val gamers = listOf(winner, loser)
        
        val loserResult = loserUseCase(winner, listOf(loser), 100)
        val scoreResult = scoreOptionUseCase(gamers, 100)
        
        assertEquals(12000, loserResult.accounts[1L])  // 15 × 100 × 8
        assertEquals(400, scoreResult.accounts[1L])
    }
    
    @Test
    fun `4명 모두 연뻑 이상 (극단적 뻑 케이스)`() {
        val gamers = listOf(
            Gamer(id = 1, scoreOption = listOf(ScoreOption.ThreeFuck)),
            Gamer(id = 2, scoreOption = listOf(ScoreOption.ThreeFuck)),
            Gamer(id = 3, scoreOption = listOf(ScoreOption.SecondFuck)),
            Gamer(id = 4, scoreOption = listOf(ScoreOption.SecondFuck))
        )
        
        val result = scoreOptionUseCase(gamers, 100)
        
        // 총액은 0
        assertEquals(0, result.accounts.values.sum())
    }
    
    @Test
    fun `광팜 1광 + 승자 20점 + 삼연뻑`() {
        val seller = Gamer(id = 1, score = 1)
        val winner = Gamer(id = 2, score = 20, scoreOption = listOf(ScoreOption.ThreeFuck))
        val loser = Gamer(id = 3)
        val gamers = listOf(seller, winner, loser)
        
        val sellResult = sellUseCase(seller, gamers, 100)
        val loserResult = loserUseCase(winner, listOf(loser), 100)
        val scoreResult = scoreOptionUseCase(listOf(winner, loser), 100)
        
        assertEquals(200, sellResult.accounts[1L])
        assertEquals(2000, loserResult.accounts[2L])
        assertEquals(400, scoreResult.accounts[2L])
    }
    
    @Test
    fun `승자 7점 점당 1원 (최소 점당)`() {
        val winner = Gamer(id = 1, score = 7)
        val loser = Gamer(id = 2)
        
        val result = loserUseCase(winner, listOf(loser), 1)
        
        assertEquals(7, result.accounts[1L])
    }
    
    @Test
    fun `뻑 점수 1원 (최소 뻑 점수)`() {
        val gamers = listOf(
            Gamer(id = 1, scoreOption = listOf(ScoreOption.FirstFuck)),
            Gamer(id = 2)
        )
        
        val result = scoreOptionUseCase(gamers, 1)
        
        assertEquals(1, result.accounts[1L])
    }
    
    // ===== 복합 케이스 (46~50) =====
    
    @Test
    fun `광팜 + 고박 + 모든 뻑 옵션`() {
        val seller = Gamer(id = 1, score = 2)
        val winner = Gamer(id = 2, score = 8, scoreOption = listOf(ScoreOption.ThreeFuck))
        val goBakLoser = Gamer(id = 3, loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak),
                              scoreOption = listOf(ScoreOption.SecondFuck))
        val normalLoser = Gamer(id = 4, scoreOption = listOf(ScoreOption.FirstFuck))
        val gamers = listOf(seller, winner, goBakLoser, normalLoser)
        
        val sellResult = sellUseCase(seller, gamers, 100)
        val loserResult = loserUseCase(winner, listOf(goBakLoser, normalLoser), 100)
        val scoreResult = scoreOptionUseCase(listOf(winner, goBakLoser, normalLoser), 100)
        
        assertEquals(600, sellResult.accounts[1L])
        assertEquals(2400, loserResult.accounts[2L])  // 1600 + 800
        assertEquals(-2400, loserResult.accounts[3L]) // 고박
        
        // 총액 검증
        val total = sellResult.accounts.values.sum() + 
                   loserResult.accounts.values.sum() + 
                   scoreResult.accounts.values.sum()
        assertEquals(0, total)
    }
    
    @Test
    fun `4명 - 광팜 1광 + 승자 삼연뻑 + 6점, 패자 다양`() {
        val seller = Gamer(id = 1, score = 1)
        val winner = Gamer(id = 2, score = 6, scoreOption = listOf(ScoreOption.ThreeFuck))
        val loser1 = Gamer(id = 3, loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 4, loserOption = listOf(LoserOption.LightBak))
        val gamers = listOf(seller, winner, loser1, loser2)
        
        val sellResult = sellUseCase(seller, gamers, 100)
        val loserResult = loserUseCase(winner, listOf(loser1, loser2), 100)
        val scoreResult = scoreOptionUseCase(listOf(winner, loser1, loser2), 100)
        
        assertEquals(300, sellResult.accounts[1L])     // 100 × 3
        assertEquals(2400, loserResult.accounts[2L])   // 1200 + 1200
        assertEquals(800, scoreResult.accounts[2L])    // 400 × 2 (삼연뻑, 게임 종료)
    }
    
    @Test
    fun `모든 요소 포함 - 광팜 + 고박 + 다양한 박 + 다양한 뻑`() {
        val seller = Gamer(id = 1, score = 1)
        val winner = Gamer(id = 2, score = 7, 
                          scoreOption = listOf(ScoreOption.SecondFuck, ScoreOption.FirstDdadak))
        val goBakLoser = Gamer(id = 3, 
                              loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak, LoserOption.LightBak),
                              scoreOption = listOf(ScoreOption.FirstFuck))
        val normalLoser = Gamer(id = 4, loserOption = listOf(LoserOption.MongBak))
        val gamers = listOf(seller, winner, goBakLoser, normalLoser)
        
        val sellResult = sellUseCase(seller, gamers, 100)
        val loserResult = loserUseCase(winner, listOf(goBakLoser, normalLoser), 100)
        val scoreResult = scoreOptionUseCase(listOf(winner, goBakLoser, normalLoser), 100)
        
        // 각 단계별 검증
        assertEquals(300, sellResult.accounts[1L])
        assertEquals(4200, loserResult.accounts[2L])  // 2800(고박) + 1400(멍박)
        assertEquals(-4200, loserResult.accounts[3L])
        
        // 총액은 0
        val total = sellResult.accounts.values.sum() + 
                   loserResult.accounts.values.sum() + 
                   scoreResult.accounts.values.sum()
        assertEquals(0, total)
    }
    
    @Test
    fun `점당 200원 뻑 150원 광 300원 (모두 높은 금액)`() {
        val seller = Gamer(id = 1, score = 2)
        val winner = Gamer(id = 2, score = 8, scoreOption = listOf(ScoreOption.SecondFuck))
        val loser = Gamer(id = 3, loserOption = listOf(LoserOption.PeaBak))
        val gamers = listOf(seller, winner, loser)
        
        val sellResult = sellUseCase(seller, gamers, 300)
        val loserResult = loserUseCase(winner, listOf(loser), 200)
        val scoreResult = scoreOptionUseCase(listOf(winner, loser), 150)
        
        assertEquals(1200, sellResult.accounts[1L])  // 600 × 2
        assertEquals(3200, loserResult.accounts[2L]) // 8 × 200 × 2
        assertEquals(300, scoreResult.accounts[2L])  // 150 × 2
        
        // 승자 총 수입
        val winnerTotal = -600 + 3200 + 300  // 광팜(-) + 패자(+) + 뻑(+)
        assertEquals(2900, winnerTotal)
    }
    
    @Test
    fun `연속 라운드 시뮬레이션 - 일관성 검증`() {
        // 라운드 1
        val r1_winner = Gamer(id = 1, score = 7)
        val r1_loser = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak))
        val r1_result = loserUseCase(r1_winner, listOf(r1_loser), 100)
        
        // 라운드 2 (같은 조건)
        val r2_winner = Gamer(id = 1, score = 7)
        val r2_loser = Gamer(id = 2, loserOption = listOf(LoserOption.PeaBak))
        val r2_result = loserUseCase(r2_winner, listOf(r2_loser), 100)
        
        // 같은 조건이면 같은 결과
        assertEquals(r1_result.accounts[1L], r2_result.accounts[1L])
        assertEquals(r1_result.accounts[2L], r2_result.accounts[2L])
        
        // 총 수입 계산
        val player1Total = (r1_result.accounts[1L] ?: 0) + (r2_result.accounts[1L] ?: 0)
        val player2Total = (r1_result.accounts[2L] ?: 0) + (r2_result.accounts[2L] ?: 0)
        
        assertEquals(2800, player1Total)
        assertEquals(-2800, player2Total)
    }
}

