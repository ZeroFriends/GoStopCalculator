package zero.friends.domain.usecase.calculate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.ScoreOption

/**
 * 엣지 케이스 테스트
 * 
 * 경계값, 극단적인 케이스, 예외 상황 등을 검증
 */
class CalculateEdgeCasesTest {
    
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
    fun `극단값 - 매우 큰 점수 (1000점)`() {
        // Given: 승자가 매우 높은 점수를 획득
        val winner = Gamer(id = 1, name = "승자", score = 1000)
        val loser = Gamer(id = 2, name = "패자")
        val scorePerPoint = 100
        
        // When
        val result = loserUseCase(winner, listOf(loser), scorePerPoint)
        
        // Then: 100,000원
        assertEquals(100000, result.accounts[1L])
        assertEquals(-100000, result.accounts[2L])
    }
    
    @Test
    fun `극단값 - 4개의 박을 모두 가진 경우`() {
        // Given: 한 패자가 4개의 박을 모두 가진 경우
        val winner = Gamer(id = 1, name = "승자", score = 10)
        val loser = Gamer(id = 2, name = "패자 (4개 박)", 
                         loserOption = listOf(
                             LoserOption.PeaBak,
                             LoserOption.LightBak,
                             LoserOption.MongBak,
                             LoserOption.GoBak
                         ))
        val scorePerPoint = 100
        
        // When
        val result = loserUseCase(winner, listOf(loser), scorePerPoint)
        
        // Then: 1000 × 2^3 = 8000 (고박 제외하고 3개 박)
        assertEquals(8000, result.accounts[1L])
        assertEquals(-8000, result.accounts[2L])
    }
    
    @Test
    fun `극단값 - 광 10개 팔기`() {
        // Given: 광 10개를 팔 경우 (실제로는 불가능하지만 엣지 케이스)
        val seller = Gamer(id = 1, name = "seller", score = 10)
        val gamers = listOf(
            seller,
            Gamer(id = 2, name = "buyer1"),
            Gamer(id = 3, name = "buyer2")
        )
        val sellScorePerLight = 1000
        
        // When
        val result = sellUseCase(seller, gamers, sellScorePerLight)
        
        // Then: 10000 × 2 = 20000
        assertEquals(20000, result.accounts[1L])
        assertEquals(-10000, result.accounts[2L])
        assertEquals(-10000, result.accounts[3L])
    }
    
    @Test
    fun `극단값 - 매우 높은 뻑 점수`() {
        // Given: 뻑 점수가 매우 높음
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1", scoreOption = listOf(ScoreOption.ThreeFuck)),
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3")
        )
        val fuckScore = 10000
        
        // When
        val result = scoreOptionUseCase(gamers, fuckScore)
        
        // Then: 40000 × 2 = 80000
        assertEquals(80000, result.accounts[1L])
        assertEquals(-40000, result.accounts[2L])
        assertEquals(-40000, result.accounts[3L])
    }
    
    @Test
    fun `경계값 - 승자 점수 0점`() {
        // Given: 승자가 0점 (가능한 케이스)
        val winner = Gamer(id = 1, name = "승자", score = 0)
        val loser = Gamer(id = 2, name = "패자")
        val scorePerPoint = 100
        
        // When
        val result = loserUseCase(winner, listOf(loser), scorePerPoint)
        
        // Then: 0원
        assertEquals(0, result.accounts[1L])
        assertEquals(0, result.accounts[2L])
    }
    
    @Test
    fun `경계값 - 광 0개 팔기`() {
        // Given: 광 0개를 팔 경우
        val seller = Gamer(id = 1, name = "seller", score = 0)
        val gamers = listOf(
            seller,
            Gamer(id = 2, name = "buyer")
        )
        val sellScorePerLight = 100
        
        // When
        val result = sellUseCase(seller, gamers, sellScorePerLight)
        
        // Then: 0원
        assertEquals(0, result.accounts[1L])
        assertEquals(0, result.accounts[2L])
    }
    
    @Test
    fun `2인 게임 - 광팜`() {
        // Given: 2인 게임에서 광팜
        val seller = Gamer(id = 1, name = "seller", score = 3)
        val gamers = listOf(
            seller,
            Gamer(id = 2, name = "buyer")
        )
        val sellScorePerLight = 100
        
        // When
        val result = sellUseCase(seller, gamers, sellScorePerLight)
        
        // Then
        assertEquals(300, result.accounts[1L])
        assertEquals(-300, result.accounts[2L])
    }
    
    @Test
    fun `2인 게임 - 고박 (맞고)`() {
        // Given: 맞고에서 고박
        val winner = Gamer(id = 1, name = "승자", score = 7)
        val loser = Gamer(id = 2, name = "패자 (고박)", 
                         loserOption = listOf(LoserOption.GoBak))
        
        // When
        val result = loserUseCase(winner, listOf(loser), 100)
        
        // Then: 고박자가 혼자 냄
        assertEquals(700, result.accounts[1L])
        assertEquals(-700, result.accounts[2L])
    }
    
    @Test
    fun `모든 플레이어가 뻑을 가진 경우`() {
        // Given: 4명 모두 뻑 옵션을 가진 경우
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1", scoreOption = listOf(ScoreOption.FirstFuck)),
            Gamer(id = 2, name = "플레이어2", scoreOption = listOf(ScoreOption.SecondFuck)),
            Gamer(id = 3, name = "플레이어3", scoreOption = listOf(ScoreOption.ThreeFuck)),
            Gamer(id = 4, name = "플레이어4", scoreOption = listOf(ScoreOption.FirstDdadak))
        )
        val fuckScore = 100
        
        // When
        val result = scoreOptionUseCase(gamers, fuckScore)
        
        // Then: 각자 주고받은 결과
        // 플레이어1: +300 (100×3), -200 (연뻑), -400 (삼연뻑), -100 (따닥) = -400
        // 플레이어2: +600 (200×3), -100 (첫뻑), -400 (삼연뻑), -100 (따닥) = 0
        // 플레이어3: +1200 (400×3), -100 (첫뻑), -200 (연뻑), -100 (따닥) = +800
        // 플레이어4: +300 (100×3), -100 (첫뻑), -200 (연뻑), -400 (삼연뻑) = -400
        assertEquals(-400, result.accounts[1L])
        assertEquals(0, result.accounts[2L])
        assertEquals(800, result.accounts[3L])
        assertEquals(-400, result.accounts[4L])
        
        // 총액은 0이어야 함
        assertEquals(0, result.accounts.values.sum())
    }
    
    @Test
    fun `3명 고박 케이스 - 2명이 패자`() {
        // Given: 3명 게임에서 한 명이 고박
        val winner = Gamer(id = 1, name = "승자", score = 8)
        val goBakLoser = Gamer(id = 2, name = "고박자", 
                              loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak))
        val normalLoser = Gamer(id = 3, name = "일반 패자",
                               loserOption = listOf(LoserOption.LightBak))
        
        // When
        val result = loserUseCase(winner, listOf(goBakLoser, normalLoser), 100)
        
        // Then: 고박자가 자신의 금액(피박: 1600) + 일반 패자 금액(광박: 1600) = 3200
        assertEquals(3200, result.accounts[1L])
        assertEquals(-3200, result.accounts[2L])
        assertEquals(null, result.accounts[3L]) // 고박자가 대신 냄
    }
    
    @Test
    fun `복합 시나리오 - 음수가 발생하지 않는지 확인`() {
        // Given: 여러 복잡한 케이스
        val seller = Gamer(id = 1, name = "seller", score = 5)
        val winner = Gamer(id = 2, name = "winner", score = 10)
        val loser1 = Gamer(id = 3, name = "loser1", 
                          loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak),
                          scoreOption = listOf(ScoreOption.FirstFuck))
        val loser2 = Gamer(id = 4, name = "loser2",
                          scoreOption = listOf(ScoreOption.SecondFuck))
        val allGamers = listOf(seller, winner, loser1, loser2)
        
        // When: 모든 계산 수행
        val sellResult = sellUseCase(seller, allGamers, 200)
        val loserResult = loserUseCase(winner, listOf(loser1, loser2), 100)
        val scoreOptionResult = scoreOptionUseCase(allGamers.filter { it.id != seller.id }, 100)
        
        // Then: 각 단계별 총액은 0
        assertEquals(0, sellResult.accounts.values.sum())
        assertEquals(0, loserResult.accounts.values.sum())
        assertEquals(0, scoreOptionResult.accounts.values.sum())
    }
    
    @Test
    fun `빈 리스트 처리 - 패자가 없는 경우`() {
        // Given: 패자가 없는 경우 (있을 수 없지만 방어 코드 확인)
        val winner = Gamer(id = 1, name = "승자", score = 10)
        val emptyLosers = emptyList<Gamer>()
        
        // When
        val result = loserUseCase(winner, emptyLosers, 100)
        
        // Then: 승자도 0원
        assertEquals(0, result.accounts.getOrDefault(1L, 0))
    }
    
    @Test
    fun `빈 리스트 처리 - 옵션이 없는 플레이어들만`() {
        // Given: 아무도 옵션이 없음
        val gamers = listOf(
            Gamer(id = 1, name = "플레이어1"),
            Gamer(id = 2, name = "플레이어2"),
            Gamer(id = 3, name = "플레이어3")
        )
        val fuckScore = 100
        
        // When
        val result = scoreOptionUseCase(gamers, fuckScore)
        
        // Then: 빈 맵 또는 모두 0
        assertTrue(result.accounts.isEmpty() || result.accounts.values.all { it == 0 })
    }
    
    @Test
    fun `오버플로우 테스트 - 매우 큰 값 계산`() {
        // Given: Int의 한계에 가까운 값
        val winner = Gamer(id = 1, name = "승자", score = 8191)  // 8191 × 1000 = 8,191,000
        val loser = Gamer(id = 2, name = "패자 (3개 박)",
                         loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak))
        val scorePerPoint = 1000
        
        // When
        val result = loserUseCase(winner, listOf(loser), scorePerPoint)
        
        // Then: 8,191,000 × 8 = 65,528,000 (Int 범위 내)
        assertEquals(65528000, result.accounts[1L])
        assertEquals(-65528000, result.accounts[2L])
    }
    
    @Test
    fun `일관성 검증 - 여러 번 계산해도 같은 결과`() {
        // Given: 같은 입력
        val seller = Gamer(id = 1, name = "seller", score = 2)
        val gamers = listOf(seller, Gamer(id = 2), Gamer(id = 3))
        val sellScorePerLight = 100
        
        // When: 여러 번 계산
        val result1 = sellUseCase(seller, gamers, sellScorePerLight)
        val result2 = sellUseCase(seller, gamers, sellScorePerLight)
        val result3 = sellUseCase(seller, gamers, sellScorePerLight)
        
        // Then: 모두 같은 결과
        assertEquals(result1.accounts, result2.accounts)
        assertEquals(result2.accounts, result3.accounts)
    }
}

