package zero.friends.domain.usecase.calculate

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption

class CalculateLoserScoreUseCaseTest {
    
    private lateinit var useCase: CalculateLoserScoreUseCase
    
    @Before
    fun setup() {
        useCase = CalculateLoserScoreUseCase()
    }
    
    @Test
    fun `기본 케이스 - 박 없이 승자 7점, 점당 100원`() {
        // Given: 승자 7점, 패자 3명 (박 없음), 점당 100원
        val winner = Gamer(id = 1, name = "승자", score = 7)
        val losers = listOf(
            Gamer(id = 2, name = "패자1"),
            Gamer(id = 3, name = "패자2"),
            Gamer(id = 4, name = "패자3")
        )
        val scorePerPoint = 100
        
        // When: 패자 계산
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 각 패자는 700원 지불, 승자는 2100원 받음
        assertEquals(2100, result.accounts[1L])
        assertEquals(-700, result.accounts[2L])
        assertEquals(-700, result.accounts[3L])
        assertEquals(-700, result.accounts[4L])
    }
    
    @Test
    fun `피박 케이스 - 한 명이 피박`() {
        // Given: 승자 5점, 한 명이 피박, 점당 100원
        val winner = Gamer(id = 1, name = "승자", score = 5)
        val losers = listOf(
            Gamer(id = 2, name = "패자1 (피박)", loserOption = listOf(LoserOption.PeaBak)),
            Gamer(id = 3, name = "패자2"),
            Gamer(id = 4, name = "패자3")
        )
        val scorePerPoint = 100
        
        // When: 패자 계산
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 피박은 1000원 (500 × 2), 나머지는 500원, 승자는 2000원
        assertEquals(2000, result.accounts[1L])
        assertEquals(-1000, result.accounts[2L]) // 피박: 500 × 2
        assertEquals(-500, result.accounts[3L])
        assertEquals(-500, result.accounts[4L])
    }
    
    @Test
    fun `멍박 케이스 - 한 명이 멍박`() {
        // Given: 승자 6점, 한 명이 멍박, 점당 50원
        val winner = Gamer(id = 1, name = "승자", score = 6)
        val losers = listOf(
            Gamer(id = 2, name = "패자1"),
            Gamer(id = 3, name = "패자2 (멍박)", loserOption = listOf(LoserOption.MongBak)),
            Gamer(id = 4, name = "패자3")
        )
        val scorePerPoint = 50
        
        // When: 패자 계산
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 멍박은 600원 (300 × 2), 나머지는 300원, 승자는 1200원
        assertEquals(1200, result.accounts[1L])
        assertEquals(-300, result.accounts[2L])
        assertEquals(-600, result.accounts[3L]) // 멍박: 300 × 2
        assertEquals(-300, result.accounts[4L])
    }
    
    @Test
    fun `다중 박 케이스 - 피박과 광박`() {
        // Given: 승자 8점, 한 명이 피박+광박, 점당 100원
        val winner = Gamer(id = 1, name = "승자", score = 8)
        val losers = listOf(
            Gamer(id = 2, name = "패자1 (피박+광박)", 
                  loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak)),
            Gamer(id = 3, name = "패자2")
        )
        val scorePerPoint = 100
        
        // When: 패자 계산
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 피박+광박은 3200원 (800 × 2^2), 나머지는 800원, 승자는 4000원
        assertEquals(4000, result.accounts[1L])
        assertEquals(-3200, result.accounts[2L]) // 800 × 4
        assertEquals(-800, result.accounts[3L])
    }
    
    @Test
    fun `다중 박 케이스 - 피박, 광박, 멍박 (3개 박)`() {
        // Given: 승자 10점, 한 명이 피박+광박+멍박, 점당 100원
        val winner = Gamer(id = 1, name = "승자", score = 10)
        val losers = listOf(
            Gamer(id = 2, name = "패자1 (3개 박)", 
                  loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak)),
            Gamer(id = 3, name = "패자2")
        )
        val scorePerPoint = 100
        
        // When: 패자 계산
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 3개 박은 8000원 (1000 × 2^3), 나머지는 1000원, 승자는 9000원
        assertEquals(9000, result.accounts[1L])
        assertEquals(-8000, result.accounts[2L]) // 1000 × 8
        assertEquals(-1000, result.accounts[3L])
    }
    
    @Test
    fun `고박 케이스 - 고박자가 모든 패자의 돈을 냄`() {
        // Given: 승자 7점, 한 명이 고박, 점당 100원
        val winner = Gamer(id = 1, name = "승자", score = 7)
        val losers = listOf(
            Gamer(id = 2, name = "패자1 (고박)", loserOption = listOf(LoserOption.GoBak)),
            Gamer(id = 3, name = "패자2"),
            Gamer(id = 4, name = "패자3")
        )
        val scorePerPoint = 100
        
        // When: 패자 계산
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 고박자가 모든 패자의 금액 지불 (700 + 700 + 700 = 2100원)
        assertEquals(2100, result.accounts[1L])
        assertEquals(-2100, result.accounts[2L]) // 고박자가 전부 냄
        assertEquals(null, result.accounts[3L]) // 내지 않음
        assertEquals(null, result.accounts[4L]) // 내지 않음
    }
    
    @Test
    fun `고박 케이스 - 고박자가 피박도 함께 가지고 있을 때`() {
        // Given: 승자 6점, 고박자가 피박도 가지고 있음, 점당 100원
        val winner = Gamer(id = 1, name = "승자", score = 6)
        val losers = listOf(
            Gamer(id = 2, name = "패자1 (고박+피박)", 
                  loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak)),
            Gamer(id = 3, name = "패자2")
        )
        val scorePerPoint = 100
        
        // When: 패자 계산
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 고박자가 패자2의 600원 + 자신의 1200원(피박) = 1800원
        assertEquals(1800, result.accounts[1L])
        assertEquals(-1800, result.accounts[2L]) // 600 + (600 × 2)
        assertEquals(null, result.accounts[3L])
    }
    
    @Test
    fun `고박 케이스 - 고박자와 다른 패자가 모두 박을 가지고 있을 때`() {
        // Given: 승자 5점, 고박자(광박), 패자2(피박), 점당 100원
        val winner = Gamer(id = 1, name = "승자", score = 5)
        val losers = listOf(
            Gamer(id = 2, name = "패자1 (고박+광박)", 
                  loserOption = listOf(LoserOption.GoBak, LoserOption.LightBak)),
            Gamer(id = 3, name = "패자2 (피박)", 
                  loserOption = listOf(LoserOption.PeaBak))
        )
        val scorePerPoint = 100
        
        // When: 패자 계산
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 고박자가 패자2의 1000원(피박) + 자신의 1000원(광박) = 2000원
        assertEquals(2000, result.accounts[1L])
        assertEquals(-2000, result.accounts[2L]) // (500 × 2) + (500 × 2)
        assertEquals(null, result.accounts[3L])
    }
    
    @Test
    fun `고박 케이스 - 맞고 (2인 게임, 고박자만 있음)`() {
        // Given: 맞고, 승자와 고박자만 있음
        val winner = Gamer(id = 1, name = "승자", score = 10)
        val losers = listOf(
            Gamer(id = 2, name = "패자 (고박)", loserOption = listOf(LoserOption.GoBak))
        )
        val scorePerPoint = 100
        
        // When: 패자 계산
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 고박자가 1000원 지불
        assertEquals(1000, result.accounts[1L])
        assertEquals(-1000, result.accounts[2L])
    }
    
    @Test
    fun `총액 검증 - 받는 돈과 내는 돈의 합은 0`() {
        // Given: 복잡한 케이스
        val winner = Gamer(id = 1, name = "승자", score = 7)
        val losers = listOf(
            Gamer(id = 2, name = "패자1 (피박)", loserOption = listOf(LoserOption.PeaBak)),
            Gamer(id = 3, name = "패자2 (광박+멍박)", 
                  loserOption = listOf(LoserOption.LightBak, LoserOption.MongBak)),
            Gamer(id = 4, name = "패자3")
        )
        val scorePerPoint = 100
        
        // When
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 모든 계정의 합은 0이어야 함
        val totalSum = result.accounts.values.sum()
        assertEquals(0, totalSum)
    }
    
    @Test
    fun `고박 케이스 - 총액 검증`() {
        // Given: 고박 케이스
        val winner = Gamer(id = 1, name = "승자", score = 8)
        val losers = listOf(
            Gamer(id = 2, name = "패자1 (고박+피박)", 
                  loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak)),
            Gamer(id = 3, name = "패자2 (광박)", loserOption = listOf(LoserOption.LightBak)),
            Gamer(id = 4, name = "패자3")
        )
        val scorePerPoint = 150
        
        // When
        val result = useCase(winner, losers, scorePerPoint)
        
        // Then: 모든 계정의 합은 0이어야 함
        val totalSum = result.accounts.values.sum()
        assertEquals(0, totalSum)
    }
}

