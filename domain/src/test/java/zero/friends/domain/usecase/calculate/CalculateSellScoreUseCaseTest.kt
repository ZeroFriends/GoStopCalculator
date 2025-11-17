package zero.friends.domain.usecase.calculate

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zero.friends.domain.model.Gamer

class CalculateSellScoreUseCaseTest {
    
    private lateinit var useCase: CalculateSellScoreUseCase
    
    @Before
    fun setup() {
        useCase = CalculateSellScoreUseCase()
    }
    
    @Test
    fun `광팜 - 4명 게임에서 광 2개를 100원에 팔았을 때`() {
        // Given: 4명이 게임 중, 플레이어1이 광 2개를 100원에 팔음
        val seller = Gamer(id = 1, name = "플레이어1", score = 2)
        val allGamers = listOf(
            seller,
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3"),
            Gamer(id = 4, name = "플레이어4")
        )
        val sellScorePerLight = 100
        
        // When: 광팜 계산
        val result = useCase(seller, allGamers, sellScorePerLight)
        
        // Then: 판 사람은 200 × 3 = 600원 받고, 나머지는 각각 200원 지불
        assertEquals(600, result.accounts[1L])
        assertEquals(-200, result.accounts[2L])
        assertEquals(-200, result.accounts[3L])
        assertEquals(-200, result.accounts[4L])
    }
    
    @Test
    fun `광팜 - 3명 게임에서 광 3개를 50원에 팔았을 때`() {
        // Given: 3명이 게임 중, 플레이어1이 광 3개를 50원에 팔음
        val seller = Gamer(id = 1, name = "플레이어1", score = 3)
        val allGamers = listOf(
            seller,
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3")
        )
        val sellScorePerLight = 50
        
        // When: 광팜 계산
        val result = useCase(seller, allGamers, sellScorePerLight)
        
        // Then: 판 사람은 150 × 2 = 300원 받고, 나머지는 각각 150원 지불
        assertEquals(300, result.accounts[1L])
        assertEquals(-150, result.accounts[2L])
        assertEquals(-150, result.accounts[3L])
    }
    
    @Test
    fun `광팜 - 광 1개만 팔았을 때`() {
        // Given: 4명이 게임 중, 플레이어2가 광 1개를 200원에 팔음
        val seller = Gamer(id = 2, name = "플레이어2", score = 1)
        val allGamers = listOf(
            Gamer(id = 1, name = "플레이어1"),
            seller,
            Gamer(id = 3, name = "플레이어3"),
            Gamer(id = 4, name = "플레이어4")
        )
        val sellScorePerLight = 200
        
        // When: 광팜 계산
        val result = useCase(seller, allGamers, sellScorePerLight)
        
        // Then: 판 사람은 200 × 3 = 600원 받고, 나머지는 각각 200원 지불
        assertEquals(600, result.accounts[2L])
        assertEquals(-200, result.accounts[1L])
        assertEquals(-200, result.accounts[3L])
        assertEquals(-200, result.accounts[4L])
    }
    
    @Test
    fun `광팜 - 광 가격이 0원일 때`() {
        // Given: 광 가격이 0원 (테스트용 엣지 케이스)
        val seller = Gamer(id = 1, name = "플레이어1", score = 2)
        val allGamers = listOf(
            seller,
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3")
        )
        val sellScorePerLight = 0
        
        // When: 광팜 계산
        val result = useCase(seller, allGamers, sellScorePerLight)
        
        // Then: 모든 금액이 0원
        assertEquals(0, result.accounts[1L])
        assertEquals(0, result.accounts[2L])
        assertEquals(0, result.accounts[3L])
    }
    
    @Test
    fun `광팜 - 총액 검증 (받는 돈과 내는 돈의 합은 0)`() {
        // Given
        val seller = Gamer(id = 1, name = "플레이어1", score = 4)
        val allGamers = listOf(
            seller,
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3"),
            Gamer(id = 4, name = "플레이어4")
        )
        val sellScorePerLight = 150
        
        // When
        val result = useCase(seller, allGamers, sellScorePerLight)
        
        // Then: 모든 계정의 합은 0이어야 함 (받는 돈 = 내는 돈)
        val totalSum = result.accounts.values.sum()
        assertEquals(0, totalSum)
    }
}

