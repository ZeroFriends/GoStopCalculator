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

/**
 * CalculateGameResultUseCase 통합 테스트
 * 
 * Mock Repository를 사용하여 실제 UseCase의 동작을 검증
 */
class CalculateGameResultUseCaseTest {
    
    private lateinit var useCase: CalculateGameResultUseCase
    private lateinit var mockRuleRepository: MockRuleRepository
    private lateinit var mockGamerRepository: MockGamerRepository
    private lateinit var updateAccountUseCase: UpdateAccountUseCase
    
    @Before
    fun setup() {
        mockRuleRepository = MockRuleRepository()
        mockGamerRepository = MockGamerRepository()
        updateAccountUseCase = UpdateAccountUseCase(mockGamerRepository)
        
        val sellUseCase = CalculateSellScoreUseCase()
        val loserUseCase = CalculateLoserScoreUseCase()
        val scoreOptionUseCase = CalculateScoreOptionUseCase()
        
        useCase = CalculateGameResultUseCase(
            ruleRepository = mockRuleRepository,
            gamerRepository = mockGamerRepository,
            updateAccountUseCase = updateAccountUseCase,
            calculateSellScoreUseCase = sellUseCase,
            calculateLoserScoreUseCase = loserUseCase,
            calculateScoreOptionUseCase = scoreOptionUseCase
        )
    }
    
    @Test
    fun `맞고 - 승자 연뻑 + 6점, 패자 광박`() = runTest {
        // Given: 2명 게임, 승자 연뻑 + 6점, 패자 광박
        val gameId = 1L
        val roundId = 1L
        
        val winner = Gamer(
            id = 1, 
            name = "승자", 
            roundId = roundId,
            score = 6,
            scoreOption = listOf(ScoreOption.SecondFuck)
        )
        val loser = Gamer(
            id = 2, 
            name = "패자", 
            roundId = roundId,
            loserOption = listOf(LoserOption.LightBak)
        )
        
        mockGamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        mockRuleRepository.setRules(gameId, listOf(
            Rule(name = "점당", score = 100),
            Rule(name = "뻑", score = 100)
        ))
        
        // When: 계산 실행
        useCase(gameId, roundId, seller = null, winner = winner)
        
        // Then: 계정 변경 검증
        val winnerAccount = mockGamerRepository.getGamerAccount(1L)
        val loserAccount = mockGamerRepository.getGamerAccount(2L)
        
        // 승자: +1,200 (패자 계산) + 200 (연뻑) = +1,400
        // 패자: -1,200 (패자 계산) - 200 (연뻑) = -1,400
        assertEquals(1400, winnerAccount)
        assertEquals(-1400, loserAccount)
    }
    
    @Test
    fun `3명 - 광팜 + 승자 + 패자`() = runTest {
        // Given: 3명 게임, 광팜 2광, 승자 7점, 패자 피박
        val gameId = 1L
        val roundId = 1L
        
        val seller = Gamer(id = 1, name = "광팜", roundId = roundId, score = 2)
        val winner = Gamer(id = 2, name = "승자", roundId = roundId, score = 7)
        val loser = Gamer(id = 3, name = "패자", roundId = roundId, 
                         loserOption = listOf(LoserOption.PeaBak))
        
        mockGamerRepository.setRoundGamers(roundId, listOf(seller, winner, loser))
        mockRuleRepository.setRules(gameId, listOf(
            Rule(name = "점당", score = 100),
            Rule(name = "뻑", score = 100),
            Rule(name = "광팔기", score = 150)
        ))
        
        // When
        useCase(gameId, roundId, seller = seller, winner = winner)
        
        // Then
        val sellerAccount = mockGamerRepository.getGamerAccount(1L)
        val winnerAccount = mockGamerRepository.getGamerAccount(2L)
        val loserAccount = mockGamerRepository.getGamerAccount(3L)
        
        // seller: +300 (150 × 2)
        // winner: -300 (광팜) + 1400 (패자 계산, 피박) = +1100
        // loser: -300 (광팜) - 1400 (패자 계산) = -1700
        assertEquals(600, sellerAccount)     // 300 × 2 (winner, loser에게 각각 받음)
        assertEquals(1100, winnerAccount)    // -300 + 1400
        assertEquals(-1700, loserAccount)    // -300 - 1400
    }
    
    @Test
    fun `4명 - 고박 + 연뻑 케이스`() = runTest {
        // Given: 4명 게임, 고박자 1명, 승자 연뻑 + 8점
        val gameId = 1L
        val roundId = 1L
        
        val winner = Gamer(id = 1, name = "승자", roundId = roundId, score = 8,
                          scoreOption = listOf(ScoreOption.SecondFuck))
        val goBakLoser = Gamer(id = 2, name = "고박자", roundId = roundId,
                              loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak))
        val loser2 = Gamer(id = 3, name = "패자2", roundId = roundId)
        val loser3 = Gamer(id = 4, name = "패자3", roundId = roundId)
        
        mockGamerRepository.setRoundGamers(roundId, listOf(winner, goBakLoser, loser2, loser3))
        mockRuleRepository.setRules(gameId, listOf(
            Rule(name = "점당", score = 100),
            Rule(name = "뻑", score = 100)
        ))
        
        // When
        useCase(gameId, roundId, seller = null, winner = winner)
        
        // Then
        val winnerAccount = mockGamerRepository.getGamerAccount(1L)
        val goBakAccount = mockGamerRepository.getGamerAccount(2L)
        val loser2Account = mockGamerRepository.getGamerAccount(3L)
        val loser3Account = mockGamerRepository.getGamerAccount(4L)
        
        // 패자 계산: 고박자가 모든 패자의 돈을 대신 냄 (고스톱이므로 고박 제외)
        //   - 패자2: 800 (고박자가 대신 냄, 그대로)
        //   - 패자3: 800 (고박자가 대신 냄, 그대로)
        //   - 고박자 본인(고박 제외, 피박만 = 2배): 800×2 = 1600
        //   총합: 800 + 800 + 1600 = 3200
        // 연뻑: 각자 냄 (고박과 무관) 200 × 3 = 600
        assertEquals(3800, winnerAccount)    // 3200 + 600
        assertEquals(-3400, goBakAccount)    // -3200 (패자) - 200 (연뻑)
        assertEquals(-200, loser2Account)    // 0 (고박자 대신) - 200 (연뻑)
        assertEquals(-200, loser3Account)    // 0 (고박자 대신) - 200 (연뻑)
    }
    
    @Test
    fun `4명 - 광팜 + 고박 + 첫뻑 복합`() = runTest {
        // Given: 복잡한 케이스
        val gameId = 1L
        val roundId = 1L
        
        val seller = Gamer(id = 1, name = "광팜", roundId = roundId, score = 1)
        val winner = Gamer(id = 2, name = "승자", roundId = roundId, score = 7,
                          scoreOption = listOf(ScoreOption.FirstFuck))
        val goBakLoser = Gamer(id = 3, name = "고박자", roundId = roundId,
                              loserOption = listOf(LoserOption.GoBak))
        val normalLoser = Gamer(id = 4, name = "일반패자", roundId = roundId)
        
        mockGamerRepository.setRoundGamers(roundId, listOf(seller, winner, goBakLoser, normalLoser))
        mockRuleRepository.setRules(gameId, listOf(
            Rule(name = "점당", score = 100),
            Rule(name = "뻑", score = 100),
            Rule(name = "광팔기", score = 200)
        ))
        
        // When
        useCase(gameId, roundId, seller = seller, winner = winner)
        
        // Then
        val sellerAccount = mockGamerRepository.getGamerAccount(1L)
        val winnerAccount = mockGamerRepository.getGamerAccount(2L)
        val goBakAccount = mockGamerRepository.getGamerAccount(3L)
        val normalLoserAccount = mockGamerRepository.getGamerAccount(4L)
        
        // 광팜: seller가 200 × 3 = 600 받음
        // 패자 계산: 고박자가 패자2의 금액 대신 냄 + 자신의 금액 (고스톱이므로 고박 제외)
        //   - 패자2: 700 (고박자가 대신 냄, 그대로)
        //   - 고박자 본인: 700 (고박 제외, 기본 금액만)
        //   총합: 700 + 700 = 1400
        // 첫뻑: 각자 냄 (seller 제외, winner, goBak, normalLoser) 100 × 2 = 200
        assertEquals(600, sellerAccount)     // 600 (광팜)
        assertEquals(1400, winnerAccount)    // -200 (광팜) + 1400 (패자) + 200 (첫뻑)
        assertEquals(-1700, goBakAccount)    // -200 (광팜) - 1400 (패자, 대신 냄) - 100 (첫뻑)
        assertEquals(-300, normalLoserAccount) // -200 (광팜) - 0 (고박자 대신) - 100 (첫뻑)
    }
    
    @Test
    fun `3명 - 첫따닥 + 광박 케이스`() = runTest {
        // Given
        val gameId = 1L
        val roundId = 1L
        
        val winner = Gamer(id = 1, name = "승자", roundId = roundId, score = 5,
                          scoreOption = listOf(ScoreOption.FirstDdadak))
        val loser1 = Gamer(id = 2, name = "패자1", roundId = roundId,
                          loserOption = listOf(LoserOption.LightBak))
        val loser2 = Gamer(id = 3, name = "패자2", roundId = roundId)
        
        mockGamerRepository.setRoundGamers(roundId, listOf(winner, loser1, loser2))
        mockRuleRepository.setRules(gameId, listOf(
            Rule(name = "점당", score = 100),
            Rule(name = "뻑", score = 50)
        ))
        
        // When
        useCase(gameId, roundId, seller = null, winner = winner)
        
        // Then
        val winnerAccount = mockGamerRepository.getGamerAccount(1L)
        val loser1Account = mockGamerRepository.getGamerAccount(2L)
        val loser2Account = mockGamerRepository.getGamerAccount(3L)
        
        // 패자 계산: 1000 (광박) + 500 (기본) = 1500
        // 첫따닥: 100 × 3 = 300원 (점당 × 3), 패자 2명에게서 받음 = 300 × 2 = 600원
        // 승자 총합: 1500 + 600 = 2100원
        // 패자1: -1000 (광박) - 300 (첫따닥) = -1300원
        // 패자2: -500 (기본) - 300 (첫따닥) = -800원
        assertEquals(2100, winnerAccount)  // 1500 + 600
        assertEquals(-1300, loser1Account)   // -1000 - 300
        assertEquals(-800, loser2Account)    // -500 - 300
    }
    
    @Test
    fun `맞고 - 삼연뻑으로 게임 종료`() = runTest {
        // Given: 삼연뻑으로 게임 종료
        val gameId = 1L
        val roundId = 1L
        
        val winner = Gamer(id = 1, name = "승자", roundId = roundId, score = 10,
                          scoreOption = listOf(ScoreOption.ThreeFuck))
        val loser = Gamer(id = 2, name = "패자", roundId = roundId,
                         loserOption = listOf(LoserOption.PeaBak, LoserOption.MongBak))
        
        mockGamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        mockRuleRepository.setRules(gameId, listOf(
            Rule(name = "점당", score = 100),
            Rule(name = "뻑", score = 100)
        ))
        
        // When
        useCase(gameId, roundId, seller = null, winner = winner)
        
        // Then
        val winnerAccount = mockGamerRepository.getGamerAccount(1L)
        val loserAccount = mockGamerRepository.getGamerAccount(2L)
        
        // 패자 계산: 10 × 100 × 4 (2개 박) = 4000
        // 삼연뻑: 100 × 4 = 400
        assertEquals(4400, winnerAccount)
        assertEquals(-4400, loserAccount)
    }
    
    @Test
    fun `총액 검증 - 모든 계정의 합은 0`() = runTest {
        // Given
        val gameId = 1L
        val roundId = 1L
        
        val winner = Gamer(id = 1, name = "승자", roundId = roundId, score = 7,
                          scoreOption = listOf(ScoreOption.SecondFuck))
        val loser1 = Gamer(id = 2, name = "패자1", roundId = roundId,
                          loserOption = listOf(LoserOption.PeaBak))
        val loser2 = Gamer(id = 3, name = "패자2", roundId = roundId,
                          scoreOption = listOf(ScoreOption.FirstFuck))
        val loser3 = Gamer(id = 4, name = "패자3", roundId = roundId)
        
        mockGamerRepository.setRoundGamers(roundId, listOf(winner, loser1, loser2, loser3))
        mockRuleRepository.setRules(gameId, listOf(
            Rule(name = "점당", score = 100),
            Rule(name = "뻑", score = 100)
        ))
        
        // When
        useCase(gameId, roundId, seller = null, winner = winner)
        
        // Then: 모든 계정의 합은 0이어야 함
        val totalSum = mockGamerRepository.getAllAccounts().values.sum()
        assertEquals(0, totalSum)
    }
}
