package zero.friends.domain.usecase.calculate

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption

class CalculateScoreOptionUseCaseTest {
    
    private lateinit var useCase: CalculateScoreOptionUseCase
    private lateinit var mockRuleRepository: MockRuleRepository
    
    @Before
    fun setup() {
        mockRuleRepository = MockRuleRepository()
        useCase = CalculateScoreOptionUseCase(mockRuleRepository)
    }
    
    @Test
    fun `첫 뻑 - 한 명이 첫 뻑을 했을 때`() = runTest {
        // Given: 4명이 게임 중, 플레이어1이 첫 뻑, 뻑 점수 100원
        val gameId = 1L
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1", scoreOption = listOf(ScoreOption.FirstFuck)),
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3"),
            Gamer(id = 4, name = "플레이어4")
        )
        mockRuleRepository.setRules(gameId, listOf(
            Rule(name = "뻑", score = 100),
            Rule(name = "점당", score = 100)
        ))
        
        // When: 점수 옵션 계산
        val result = useCase(gameId, gamers)
        
        // Then: 플레이어1은 300원 받고 (100 × 3), 나머지는 각각 100원 지불
        assertEquals(300, result.accounts[1L])
        assertEquals(-100, result.accounts[2L])
        assertEquals(-100, result.accounts[3L])
        assertEquals(-100, result.accounts[4L])
    }
    
    @Test
    fun `연 뻑 - 한 명이 연 뻑을 했을 때`() {
        // Given: 4명이 게임 중, 플레이어2가 연 뻑, 뻑 점수 100원
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1"),
            Gamer(id = 2, name = "플레이어2", scoreOption = listOf(ScoreOption.SecondFuck)),
            Gamer(id = 3, name = "플레이어3"),
            Gamer(id = 4, name = "플레이어4")
        )
        val fuckScore = 100
        
        // When: 점수 옵션 계산
        val result = useCase(gamers, fuckScore)
        
        // Then: 플레이어2는 600원 받고 (200 × 3), 나머지는 각각 200원 지불
        assertEquals(600, result.accounts[2L])
        assertEquals(-200, result.accounts[1L])
        assertEquals(-200, result.accounts[3L])
        assertEquals(-200, result.accounts[4L])
    }
    
    @Test
    fun `삼연 뻑 - 한 명이 삼연 뻑을 했을 때`() {
        // Given: 3명이 게임 중, 플레이어1이 삼연 뻑, 뻑 점수 100원
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1", scoreOption = listOf(ScoreOption.ThreeFuck)),
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3")
        )
        val fuckScore = 100
        
        // When: 점수 옵션 계산
        val result = useCase(gamers, fuckScore)
        
        // Then: 플레이어1은 800원 받고 (400 × 2), 나머지는 각각 400원 지불
        assertEquals(800, result.accounts[1L])
        assertEquals(-400, result.accounts[2L])
        assertEquals(-400, result.accounts[3L])
    }
    
    @Test
    fun `첫 따닥 - 한 명이 첫 따닥을 했을 때`() {
        // Given: 4명이 게임 중, 플레이어3이 첫 따닥, 점당 50원, 뻑 점수 150원
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1"),
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3", scoreOption = listOf(ScoreOption.FirstDdadak)),
            Gamer(id = 4, name = "플레이어4")
        )
        val fuckScore = 150
        val scorePerPoint = 50  // 점당 50원
        
        // When: 점수 옵션 계산
        val result = useCase(gamers, fuckScore, scorePerPoint)
        
        // Then: 첫따닥은 점당 × 3 = 50 × 3 = 150원
        // 플레이어3은 450원 받고 (150 × 3), 나머지는 각각 150원 지불
        assertEquals(450, result.accounts[3L])
        assertEquals(-150, result.accounts[1L])
        assertEquals(-150, result.accounts[2L])
        assertEquals(-150, result.accounts[4L])
    }
    
    @Test
    fun `복수 옵션 - 한 명이 첫 뻑과 첫 따닥을 모두 했을 때`() {
        // Given: 4명이 게임 중, 플레이어1이 첫 뻑 + 첫 따닥, 점당 50원, 뻑 점수 100원
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1", 
                  scoreOption = listOf(ScoreOption.FirstFuck, ScoreOption.FirstDdadak)),
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3"),
            Gamer(id = 4, name = "플레이어4")
        )
        val fuckScore = 100
        val scorePerPoint = 50  // 점당 50원
        
        // When: 점수 옵션 계산
        val result = useCase(gamers, fuckScore, scorePerPoint)
        
        // Then: 첫뻑 100원 + 첫따닥(점당 × 3 = 50 × 3 = 150원) = 250원
        // 플레이어1은 750원 받고 (250 × 3), 나머지는 각각 250원 지불
        assertEquals(750, result.accounts[1L])
        assertEquals(-250, result.accounts[2L])
        assertEquals(-250, result.accounts[3L])
        assertEquals(-250, result.accounts[4L])
    }
    
    @Test
    fun `여러 명이 옵션을 가진 경우 - 플레이어1 첫뻑, 플레이어2 연뻑`() {
        // Given: 4명이 게임 중, 플레이어1 첫뻑, 플레이어2 연뻑, 뻑 점수 100원
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1", scoreOption = listOf(ScoreOption.FirstFuck)),
            Gamer(id = 2, name = "플레이어2", scoreOption = listOf(ScoreOption.SecondFuck)),
            Gamer(id = 3, name = "플레이어3"),
            Gamer(id = 4, name = "플레이어4")
        )
        val fuckScore = 100
        
        // When: 점수 옵션 계산
        val result = useCase(gamers, fuckScore)
        
        // Then: 
        // 플레이어1: 받음 300 (100 × 3), 냄 200 (연뻑) = 100
        // 플레이어2: 받음 600 (200 × 3), 냄 100 (첫뻑) = 500
        // 플레이어3: 냄 100 (첫뻑) + 냄 200 (연뻑) = -300
        // 플레이어4: 냄 100 (첫뻑) + 냄 200 (연뻑) = -300
        assertEquals(100, result.accounts[1L])
        assertEquals(500, result.accounts[2L])
        assertEquals(-300, result.accounts[3L])
        assertEquals(-300, result.accounts[4L])
    }
    
    @Test
    fun `여러 명이 복잡한 옵션을 가진 경우`() {
        // Given: 4명이 게임 중, 다양한 옵션, 뻑 점수 100원, 점당 약 33원 (첫따닥 100원 = 점당 × 3)
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1", 
                  scoreOption = listOf(ScoreOption.FirstFuck, ScoreOption.FirstDdadak)),
            Gamer(id = 2, name = "플레이어2", 
                  scoreOption = listOf(ScoreOption.SecondFuck)),
            Gamer(id = 3, name = "플레이어3", 
                  scoreOption = listOf(ScoreOption.FirstFuck)),
            Gamer(id = 4, name = "플레이어4")
        )
        val fuckScore = 100
        val scorePerPoint = 33  // 첫따닥 100원 = 점당 × 3, 따라서 점당 약 33원
        
        // When: 점수 옵션 계산
        val result = useCase(gamers, fuckScore, scorePerPoint)
        
        // Then: 각 플레이어의 수입/지출 계산
        // 플레이어1: 첫뻑 100 + 첫따닥 99(33×3) = 199, 받음 199 × 3 = 597, 냄 200(연뻑) + 100(플3 첫뻑) = 300, 총합 597 - 300 = 297
        // 플레이어2: 연뻑 200, 받음 200 × 3 = 600, 냄 199(플1) + 100(플3) = 299, 총합 600 - 299 = 301
        // 플레이어3: 첫뻑 100, 받음 100 × 3 = 300, 냄 199(플1) + 200(연뻑) = 399, 총합 300 - 399 = -99
        // 플레이어4: 받음 0, 냄 199(플1) + 200(연뻑) + 100(플3) = 499, 총합 -499
        // 정확한 계산: 첫따닥 = 33 × 3 = 99원
        val firstDdadakAmount = scorePerPoint * 3  // 99원
        val player1Receive = (fuckScore + firstDdadakAmount) * 3  // (100 + 99) × 3 = 597
        val player1Pay = (fuckScore * 2) + fuckScore  // 200(연뻑) + 100(플3 첫뻑) = 300
        assertEquals(player1Receive - player1Pay, result.accounts[1L])
        
        val player2Receive = (fuckScore * 2) * 3  // 200 × 3 = 600
        val player2Pay = (fuckScore + firstDdadakAmount) + fuckScore  // 199(플1) + 100(플3) = 299
        assertEquals(player2Receive - player2Pay, result.accounts[2L])
        
        val player3Receive = fuckScore * 3  // 100 × 3 = 300
        val player3Pay = (fuckScore + firstDdadakAmount) + (fuckScore * 2)  // 199(플1) + 200(연뻑) = 399
        assertEquals(player3Receive - player3Pay, result.accounts[3L])
        
        val player4Pay = (fuckScore + firstDdadakAmount) + (fuckScore * 2) + fuckScore  // 199(플1) + 200(연뻑) + 100(플3) = 499
        assertEquals(-player4Pay, result.accounts[4L])
    }
    
    @Test
    fun `옵션이 없는 경우`() {
        // Given: 4명이 게임 중, 아무도 옵션이 없음
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1"),
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3"),
            Gamer(id = 4, name = "플레이어4")
        )
        val fuckScore = 100
        
        // When: 점수 옵션 계산
        val result = useCase(gamers, fuckScore)
        
        // Then: 모두 0원
        assertEquals(true, result.accounts.isEmpty())
    }
    
    @Test
    fun `뻑 점수가 0원일 때`() {
        // Given: 뻑 점수가 0원 (테스트용 엣지 케이스)
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1", scoreOption = listOf(ScoreOption.FirstFuck)),
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3")
        )
        val fuckScore = 0
        
        // When: 점수 옵션 계산
        val result = useCase(gamers, fuckScore)
        
        // Then: 모든 금액이 0원
        assertEquals(0, result.accounts[1L])
        assertEquals(0, result.accounts[2L])
        assertEquals(0, result.accounts[3L])
    }
    
    @Test
    fun `총액 검증 - 받는 돈과 내는 돈의 합은 0`() {
        // Given: 복잡한 옵션 케이스
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1", 
                  scoreOption = listOf(ScoreOption.ThreeFuck)),
            Gamer(id = 2, name = "플레이어2", 
                  scoreOption = listOf(ScoreOption.SecondFuck)),
            Gamer(id = 3, name = "플레이어3", 
                  scoreOption = listOf(ScoreOption.FirstFuck, ScoreOption.FirstDdadak)),
            Gamer(id = 4, name = "플레이어4")
        )
        val fuckScore = 150
        
        // When
        val result = useCase(gamers, fuckScore)
        
        // Then: 모든 계정의 합은 0이어야 함
        val totalSum = result.accounts.values.sum()
        assertEquals(0, totalSum)
    }
    
    @Test
    fun `3명 게임 - 한 명이 삼연뻑을 했을 때`() {
        // Given: 3명이 게임 중, 플레이어2가 삼연 뻑, 뻑 점수 200원
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1"),
            Gamer(id = 2, name = "플레이어2", scoreOption = listOf(ScoreOption.ThreeFuck)),
            Gamer(id = 3, name = "플레이어3")
        )
        val fuckScore = 200
        
        // When: 점수 옵션 계산
        val result = useCase(gamers, fuckScore)
        
        // Then: 플레이어2는 1600원 받고 (800 × 2), 나머지는 각각 800원 지불
        assertEquals(1600, result.accounts[2L])
        assertEquals(-800, result.accounts[1L])
        assertEquals(-800, result.accounts[3L])
    }
}

