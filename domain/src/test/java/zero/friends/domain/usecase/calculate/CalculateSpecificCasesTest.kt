package zero.friends.domain.usecase.calculate

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.ScoreOption

/**
 * 실제 게임에서 발생한 특정 케이스 검증
 */
class CalculateSpecificCasesTest {
    
    private lateinit var loserUseCase: CalculateLoserScoreUseCase
    private lateinit var scoreOptionUseCase: CalculateScoreOptionUseCase
    
    @Before
    fun setup() {
        loserUseCase = CalculateLoserScoreUseCase()
        scoreOptionUseCase = CalculateScoreOptionUseCase()
    }
    
    @Test
    fun `맞고 - 승자가 연뻑하고 6점, 패자가 광박으로 졌을 때`() {
        // Given: 2명 게임 (맞고), 승자 연뻑 + 6점, 패자 광박
        val winner = Gamer(
            id = 1, 
            name = "승자", 
            score = 6,
            scoreOption = listOf(ScoreOption.SecondFuck)
        )
        val loser = Gamer(
            id = 2, 
            name = "패자", 
            loserOption = listOf(LoserOption.LightBak)
        )
        val scorePerPoint = 100  // 점당 100원
        val fuckScore = 100      // 뻑 100원
        
        // When: 패자 계산
        val loserResult = loserUseCase(winner, listOf(loser), scorePerPoint)
        
        // Then: 패자는 6점 × 100원 × 2(광박) = 1200원 지불
        assertEquals(1200, loserResult.accounts[1L])  // 승자가 받음
        assertEquals(-1200, loserResult.accounts[2L]) // 패자가 냄
        
        // When: 점수옵션 계산
        val gamers = listOf(winner, loser)
        val scoreOptionResult = scoreOptionUseCase(gamers, fuckScore)
        
        // Then: 승자는 연뻑(200원) 받음
        assertEquals(200, scoreOptionResult.accounts[1L])  // 승자가 받음
        assertEquals(-200, scoreOptionResult.accounts[2L]) // 패자가 냄
        
        // 최종 정산:
        // 승자: +1200 (패자 계산) + 200 (연뻑) = +1400
        // 패자: -1200 (패자 계산) - 200 (연뻑) = -1400
        val winnerTotal = (loserResult.accounts[1L] ?: 0) + (scoreOptionResult.accounts[1L] ?: 0)
        val loserTotal = (loserResult.accounts[2L] ?: 0) + (scoreOptionResult.accounts[2L] ?: 0)
        
        assertEquals(1400, winnerTotal)
        assertEquals(-1400, loserTotal)
        assertEquals(0, winnerTotal + loserTotal) // 총합은 0
    }
    
    @Test
    fun `맞고 - 승자 연뻑 + 6점, 패자 광박, 점당 50원`() {
        // Given: 점당이 다른 경우
        val winner = Gamer(
            id = 1, 
            name = "승자", 
            score = 6,
            scoreOption = listOf(ScoreOption.SecondFuck)
        )
        val loser = Gamer(
            id = 2, 
            name = "패자", 
            loserOption = listOf(LoserOption.LightBak)
        )
        val scorePerPoint = 50   // 점당 50원
        val fuckScore = 50       // 뻑 50원
        
        // When
        val loserResult = loserUseCase(winner, listOf(loser), scorePerPoint)
        val scoreOptionResult = scoreOptionUseCase(listOf(winner, loser), fuckScore)
        
        // Then: 
        // 패자 계산: 6 × 50 × 2 = 600
        // 연뻑: 50 × 2 = 100
        assertEquals(600, loserResult.accounts[1L])
        assertEquals(-600, loserResult.accounts[2L])
        assertEquals(100, scoreOptionResult.accounts[1L])
        assertEquals(-100, scoreOptionResult.accounts[2L])
        
        // 총합
        val winnerTotal = 600 + 100
        val loserTotal = -600 - 100
        assertEquals(700, winnerTotal)
        assertEquals(-700, loserTotal)
    }
    
    @Test
    fun `맞고 - 승자 첫뻑 + 7점, 패자 피박`() {
        // Given
        val winner = Gamer(
            id = 1, 
            name = "승자", 
            score = 7,
            scoreOption = listOf(ScoreOption.FirstFuck)
        )
        val loser = Gamer(
            id = 2, 
            name = "패자", 
            loserOption = listOf(LoserOption.PeaBak)
        )
        val scorePerPoint = 100
        val fuckScore = 100
        
        // When
        val loserResult = loserUseCase(winner, listOf(loser), scorePerPoint)
        val scoreOptionResult = scoreOptionUseCase(listOf(winner, loser), fuckScore)
        
        // Then: 
        // 패자 계산: 7 × 100 × 2(피박) = 1400
        // 첫뻑: 100
        assertEquals(1400, loserResult.accounts[1L])
        assertEquals(-1400, loserResult.accounts[2L])
        assertEquals(100, scoreOptionResult.accounts[1L])
        assertEquals(-100, scoreOptionResult.accounts[2L])
        
        assertEquals(1500, loserResult.accounts[1L]!! + scoreOptionResult.accounts[1L]!!)
        assertEquals(-1500, loserResult.accounts[2L]!! + scoreOptionResult.accounts[2L]!!)
    }
    
    @Test
    fun `맞고 - 승자 삼연뻑 + 10점, 패자 멍박`() {
        // Given
        val winner = Gamer(
            id = 1, 
            name = "승자", 
            score = 10,
            scoreOption = listOf(ScoreOption.ThreeFuck)
        )
        val loser = Gamer(
            id = 2, 
            name = "패자", 
            loserOption = listOf(LoserOption.MongBak)
        )
        val scorePerPoint = 100
        val fuckScore = 100
        
        // When
        val loserResult = loserUseCase(winner, listOf(loser), scorePerPoint)
        val scoreOptionResult = scoreOptionUseCase(listOf(winner, loser), fuckScore)
        
        // Then: 
        // 패자 계산: 10 × 100 × 2(멍박) = 2000
        // 삼연뻑: 100 × 4 = 400
        assertEquals(2000, loserResult.accounts[1L])
        assertEquals(-2000, loserResult.accounts[2L])
        assertEquals(400, scoreOptionResult.accounts[1L])
        assertEquals(-400, scoreOptionResult.accounts[2L])
        
        assertEquals(2400, loserResult.accounts[1L]!! + scoreOptionResult.accounts[1L]!!)
    }
    
    @Test
    fun `맞고 - 승자 5점, 패자 연뻑 + 피박`() {
        // Given: 패자가 연뻑을 한 경우
        val winner = Gamer(
            id = 1, 
            name = "승자", 
            score = 5
        )
        val loser = Gamer(
            id = 2, 
            name = "패자", 
            loserOption = listOf(LoserOption.PeaBak),
            scoreOption = listOf(ScoreOption.SecondFuck)
        )
        val scorePerPoint = 100
        val fuckScore = 100
        
        // When
        val loserResult = loserUseCase(winner, listOf(loser), scorePerPoint)
        val scoreOptionResult = scoreOptionUseCase(listOf(winner, loser), fuckScore)
        
        // Then: 
        // 패자 계산: 5 × 100 × 2(피박) = 1000
        // 연뻑: 패자가 승자에게서 200원 받음
        assertEquals(1000, loserResult.accounts[1L])
        assertEquals(-1000, loserResult.accounts[2L])
        assertEquals(-200, scoreOptionResult.accounts[1L])  // 승자가 냄
        assertEquals(200, scoreOptionResult.accounts[2L])   // 패자가 받음
        
        // 총합: 승자 800, 패자 -800
        assertEquals(800, loserResult.accounts[1L]!! + scoreOptionResult.accounts[1L]!!)
        assertEquals(-800, loserResult.accounts[2L]!! + scoreOptionResult.accounts[2L]!!)
    }
    
    @Test
    fun `맞고 - 승자 연뻑 + 첫따닥 + 8점, 패자 광박`() {
        // Given: 승자가 여러 옵션을 가진 경우
        val winner = Gamer(
            id = 1, 
            name = "승자", 
            score = 8,
            scoreOption = listOf(ScoreOption.SecondFuck, ScoreOption.FirstDdadak)
        )
        val loser = Gamer(
            id = 2, 
            name = "패자", 
            loserOption = listOf(LoserOption.LightBak)
        )
        val scorePerPoint = 100
        val fuckScore = 100
        
        // When
        val loserResult = loserUseCase(winner, listOf(loser), scorePerPoint)
        val scoreOptionResult = scoreOptionUseCase(listOf(winner, loser), fuckScore)
        
        // Then: 
        // 패자 계산: 8 × 100 × 2(광박) = 1600
        // 연뻑: 200, 첫따닥: 100 = 총 300
        assertEquals(1600, loserResult.accounts[1L])
        assertEquals(-1600, loserResult.accounts[2L])
        assertEquals(300, scoreOptionResult.accounts[1L])
        assertEquals(-300, scoreOptionResult.accounts[2L])
        
        assertEquals(1900, loserResult.accounts[1L]!! + scoreOptionResult.accounts[1L]!!)
    }
    
    @Test
    fun `맞고 - 승자 9점, 패자 피박 + 광박 (2개 박)`() {
        // Given: 패자가 2개 박
        val winner = Gamer(
            id = 1, 
            name = "승자", 
            score = 9
        )
        val loser = Gamer(
            id = 2, 
            name = "패자", 
            loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak)
        )
        val scorePerPoint = 100
        
        // When
        val loserResult = loserUseCase(winner, listOf(loser), scorePerPoint)
        
        // Then: 9 × 100 × 4(2개 박 = 2^2) = 3600
        assertEquals(3600, loserResult.accounts[1L])
        assertEquals(-3600, loserResult.accounts[2L])
    }
    
    @Test
    fun `맞고 - 승자 삼연뻑 + 7점, 패자 첫뻑 + 멍박`() {
        // Given: 양쪽 모두 옵션이 있는 경우
        val winner = Gamer(
            id = 1, 
            name = "승자", 
            score = 7,
            scoreOption = listOf(ScoreOption.ThreeFuck)
        )
        val loser = Gamer(
            id = 2, 
            name = "패자", 
            loserOption = listOf(LoserOption.MongBak),
            scoreOption = listOf(ScoreOption.FirstFuck)
        )
        val scorePerPoint = 100
        val fuckScore = 100
        
        // When
        val loserResult = loserUseCase(winner, listOf(loser), scorePerPoint)
        val scoreOptionResult = scoreOptionUseCase(listOf(winner, loser), fuckScore)
        
        // Then: 
        // 패자 계산: 7 × 100 × 2(멍박) = 1400
        // 삼연뻑: 승자가 400 받음, 첫뻑: 패자가 100 받음
        assertEquals(1400, loserResult.accounts[1L])
        assertEquals(-1400, loserResult.accounts[2L])
        assertEquals(300, scoreOptionResult.accounts[1L])  // 400 - 100
        assertEquals(-300, scoreOptionResult.accounts[2L]) // 100 - 400
        
        // 총합
        assertEquals(1700, loserResult.accounts[1L]!! + scoreOptionResult.accounts[1L]!!)
        assertEquals(-1700, loserResult.accounts[2L]!! + scoreOptionResult.accounts[2L]!!)
    }
}

