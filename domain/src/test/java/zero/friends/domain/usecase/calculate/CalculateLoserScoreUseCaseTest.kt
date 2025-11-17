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
        
        // Then: 고박자가 모든 패자의 금액 지불 (고스톱이므로 2배 아님)
        //   패자2: 700 (고박자가 대신 냄, 그대로)
        //   패자3: 700 (고박자가 대신 냄, 그대로)
        //   고박자 본인: 700 (고박 제외, 기본 금액만)
        //   총합: 700 + 700 + 700 = 2100원
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
        
        // Then: 고박자가 패자2의 금액 대신 냄 + 자신의 금액 (고스톱이므로 고박 제외)
        //   패자2: 600 (고박자가 대신 냄, 그대로)
        //   고박자 본인: 600 × 2 = 1200 (고박 제외, 피박만 = 2배)
        //   총합: 600 + 1200 = 1800원
        assertEquals(1800, result.accounts[1L])
        assertEquals(-1800, result.accounts[2L])
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
        
        // Then: 고박자가 패자2의 금액 대신 냄 + 자신의 금액 (고스톱이므로 고박 제외)
        //   패자2(피박): 500 × 2 = 1000 → 고박자가 대신 냄: 1000 (그대로)
        //   고박자 본인(고박 제외, 광박만): 500 × 2 = 1000
        //   총합: 1000 + 1000 = 2000원
        assertEquals(2000, result.accounts[1L])
        assertEquals(-2000, result.accounts[2L])
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
        
        // Then: 고박자가 2000원 지불 (고박 2배 적용)
        assertEquals(2000, result.accounts[1L])
        assertEquals(-2000, result.accounts[2L])
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
    fun `고박 케이스 - 맞고 vs 고스톱 비교 (다른 사람의 금액 대신 내기)`() {
        // Given: 같은 점수와 박 옵션으로 맞고와 고스톱 비교
        // 주의: 맞고는 2명 게임이므로 다른 사람이 없지만, 
        //       테스트를 위해 3명으로 설정하여 비교 (losers.size로 판단)
        val winnerScore = 7
        val scorePerPoint = 100
        val normalLoser = Gamer(id = 3, name = "일반 패자", loserOption = listOf(LoserOption.PeaBak))
        
        // When: 맞고 (losers.size = 1) - 고박자가 다른 사람의 금액을 2배로 대신 냄
        //   하지만 맞고는 고박자와 승자만 있으므로 다른 사람이 없음
        //   따라서 이 테스트는 실제로는 고스톱 3명 케이스로 테스트
        val matgoWinner = Gamer(id = 1, name = "맞고 승자", score = winnerScore)
        val matgoGoBakLoser = Gamer(id = 2, name = "맞고 고박자", loserOption = listOf(LoserOption.GoBak))
        // 맞고: losers.size = 1 (고박자만) → remainLosers가 비어있음
        val matgoResult = useCase(matgoWinner, listOf(matgoGoBakLoser), scorePerPoint)
        
        // When: 고스톱 (losers.size = 2) - 고박자가 다른 사람의 금액을 그대로 대신 냄
        val gostopWinner = Gamer(id = 1, name = "고스톱 승자", score = winnerScore)
        val gostopGoBakLoser = Gamer(id = 2, name = "고스톱 고박자", loserOption = listOf(LoserOption.GoBak))
        val gostopResult = useCase(gostopWinner, listOf(gostopGoBakLoser, normalLoser), scorePerPoint)
        
        // Then: 맞고 - 고박자 본인만 계산 (다른 사람 없음)
        //   고박자 본인: 700 × 2 = 1400 (고박 2배)
        assertEquals(1400, matgoResult.accounts[1L])
        assertEquals(-1400, matgoResult.accounts[2L])
        
        // Then: 고스톱 - 고박자가 패자의 금액을 그대로 대신 냄
        //   패자(피박): 700 × 2 = 1400 → 고박자가 대신 냄: 1400 (그대로)
        //   고박자 본인: 700 (고박 제외, 기본 금액만)
        //   총합: 1400 + 700 = 2100원
        assertEquals(2100, gostopResult.accounts[1L])
        assertEquals(-2100, gostopResult.accounts[2L])
        assertEquals(null, gostopResult.accounts[3L])
    }
    
    @Test
    fun `고박 케이스 - 맞고 (2명) 고박자만 있는 경우`() {
        // Given: 맞고, 승자 6점, 고박자만 있음 (다른 사람 없음)
        val winner = Gamer(id = 1, name = "승자", score = 6)
        val goBakLoser = Gamer(id = 2, name = "고박자", loserOption = listOf(LoserOption.GoBak))
        val scorePerPoint = 100
        
        // When: 패자 계산 (losers.size = 1이므로 맞고)
        val result = useCase(winner, listOf(goBakLoser), scorePerPoint)
        
        // Then: 맞고 - 고박자 본인만 계산 (다른 사람 없음)
        //   고박자 본인: 600 × 2 = 1200 (고박 2배)
        assertEquals(1200, result.accounts[1L])
        assertEquals(-1200, result.accounts[2L])
    }
    
    @Test
    fun `고박 케이스 - 맞고와 고스톱의 차이점 명확히 검증`() {
        // Given: 같은 조건으로 맞고와 고스톱 비교
        val winnerScore = 8
        val scorePerPoint = 100
        val normalLoser = Gamer(id = 3, name = "일반 패자", loserOption = listOf(LoserOption.PeaBak))
        
        // When: 맞고 케이스 (losers.size = 1)
        //   실제 맞고는 2명 게임이므로 고박자와 승자만 있음
        //   다른 사람이 없으므로 고박자 본인 금액만 계산
        val matgoWinner = Gamer(id = 1, name = "맞고 승자", score = winnerScore)
        val matgoGoBakLoser = Gamer(id = 2, name = "맞고 고박자", loserOption = listOf(LoserOption.GoBak))
        val matgoResult = useCase(matgoWinner, listOf(matgoGoBakLoser), scorePerPoint)
        
        // When: 고스톱 케이스 (losers.size = 2)
        //   고스톱은 3명 이상이므로 고박자가 다른 사람의 금액을 대신 냄
        val gostopWinner = Gamer(id = 1, name = "고스톱 승자", score = winnerScore)
        val gostopGoBakLoser = Gamer(id = 2, name = "고스톱 고박자", loserOption = listOf(LoserOption.GoBak))
        val gostopResult = useCase(gostopWinner, listOf(gostopGoBakLoser, normalLoser), scorePerPoint)
        
        // Then: 맞고 - 고박자 본인만 계산
        //   고박자 본인: 800 × 2 = 1600 (고박 2배)
        assertEquals(1600, matgoResult.accounts[1L])
        assertEquals(-1600, matgoResult.accounts[2L])
        
        // Then: 고스톱 - 고박자가 다른 사람의 금액을 그대로 대신 냄
        //   패자(피박): 800 × 2 = 1600 → 고박자가 대신 냄: 1600 (그대로, 고스톱이므로)
        //   고박자 본인: 800 (고박 제외, 기본 금액만)
        //   총합: 1600 + 800 = 2400원
        assertEquals(2400, gostopResult.accounts[1L])
        assertEquals(-2400, gostopResult.accounts[2L])
        assertEquals(null, gostopResult.accounts[3L])
        
        // 검증: 고스톱이 맞고보다 800원 더 많이 냄 (다른 사람 금액 대신 냄)
        val difference = gostopResult.accounts[2L]!! - matgoResult.accounts[2L]!!
        assertEquals(-800, difference) // 고스톱이 800원 더 많이 냄
    }
    
    @Test
    fun `고박 케이스 - 고스톱 (3명) 다른 사람 금액 그대로 적용`() {
        // Given: 고스톱 3명, 승자 6점, 고박자, 일반 패자(피박)
        val winner = Gamer(id = 1, name = "승자", score = 6)
        val goBakLoser = Gamer(id = 2, name = "고박자", loserOption = listOf(LoserOption.GoBak))
        val normalLoser = Gamer(id = 3, name = "일반 패자", loserOption = listOf(LoserOption.PeaBak))
        val scorePerPoint = 100
        
        // When: 패자 계산 (losers.size = 2이므로 고스톱)
        val result = useCase(winner, listOf(goBakLoser, normalLoser), scorePerPoint)
        
        // Then: 고스톱이므로 고박자가 다른 사람의 금액을 그대로 대신 냄
        //   패자(피박): 600 × 2 = 1200 → 고박자가 대신 냄: 1200 (그대로)
        //   고박자 본인: 600 (고박 제외, 기본 금액만)
        //   총합: 1200 + 600 = 1800원
        assertEquals(1800, result.accounts[1L])
        assertEquals(-1800, result.accounts[2L])
        assertEquals(null, result.accounts[3L])
    }
    
    @Test
    fun `고박 케이스 - 맞고와 고스톱 로직 차이 검증 (다른 사람 금액 대신 내기)`() {
        // Given: 같은 조건으로 맞고와 고스톱의 로직 차이 검증
        //   주의: 실제 맞고는 2명이므로 다른 사람이 없지만,
        //         로직상 losers.size로 판단하므로 테스트 가능
        val winnerScore = 5
        val scorePerPoint = 100
        
        // When: 맞고 케이스 (losers.size = 1)
        //   로직상 맞고로 인식되지만, 실제로는 다른 사람이 없음
        val matgoWinner = Gamer(id = 1, name = "맞고 승자", score = winnerScore)
        val matgoGoBakLoser = Gamer(id = 2, name = "맞고 고박자", loserOption = listOf(LoserOption.GoBak))
        val matgoResult = useCase(matgoWinner, listOf(matgoGoBakLoser), scorePerPoint)
        
        // When: 고스톱 케이스 (losers.size = 2) - 다른 사람이 있는 경우
        val gostopWinner = Gamer(id = 1, name = "고스톱 승자", score = winnerScore)
        val gostopGoBakLoser = Gamer(id = 2, name = "고스톱 고박자", loserOption = listOf(LoserOption.GoBak))
        val gostopNormalLoser = Gamer(id = 3, name = "일반 패자", loserOption = listOf(LoserOption.PeaBak))
        val gostopResult = useCase(gostopWinner, listOf(gostopGoBakLoser, gostopNormalLoser), scorePerPoint)
        
        // Then: 맞고 - 고박자 본인만 계산 (다른 사람 없음)
        //   고박자 본인: 500 × 2 = 1000 (고박 2배)
        assertEquals(1000, matgoResult.accounts[1L])
        assertEquals(-1000, matgoResult.accounts[2L])
        
        // Then: 고스톱 - 고박자가 다른 사람의 금액을 그대로 대신 냄 (2배 아님)
        //   패자(피박): 500 × 2 = 1000 → 고박자가 대신 냄: 1000 (그대로, 고스톱이므로 2배 아님)
        //   고박자 본인: 500 (고박 제외, 기본 금액만)
        //   총합: 1000 + 500 = 1500원
        assertEquals(1500, gostopResult.accounts[1L])
        assertEquals(-1500, gostopResult.accounts[2L])
        assertEquals(null, gostopResult.accounts[3L])
        
        // 검증: 고스톱이 맞고보다 500원 더 많이 냄 (다른 사람 금액 대신 냄)
        val difference = gostopResult.accounts[2L]!! - matgoResult.accounts[2L]!!
        assertEquals(-500, difference) // 고스톱이 500원 더 많이 냄
    }
    
    @Test
    fun `고박 케이스 - 나무위키 규칙 검증 맞고는 2배 고스톱은 다른 사람꺼 내주기 2배 아님`() {
        // Given: 나무위키 규칙에 따른 검증
        //   맞고 플레이: 고박 플레이어는 2배의 비용을 지불한다
        //   고스톱 플레이: 고박 플레이어는 다른 패자의 비용까지 대신 지불한다 (2배 언급 없음)
        val winnerScore = 6
        val scorePerPoint = 100
        
        // When: 맞고 케이스 (losers.size = 1) - 고박자만 있음
        //   나무위키: "맞고 플레이 : 고박 플레이어는 2배의 비용을 지불한다"
        val matgoWinner = Gamer(id = 1, name = "맞고 승자", score = winnerScore)
        val matgoGoBakLoser = Gamer(id = 2, name = "맞고 고박자", loserOption = listOf(LoserOption.GoBak))
        val matgoResult = useCase(matgoWinner, listOf(matgoGoBakLoser), scorePerPoint)
        
        // When: 고스톱 케이스 (losers.size = 2) - 고박자 + 다른 패자
        //   나무위키: "고스톱 플레이 : 고박 플레이어는 다른 패자의 비용까지 대신 지불한다" (2배 언급 없음)
        val gostopWinner = Gamer(id = 1, name = "고스톱 승자", score = winnerScore)
        val gostopGoBakLoser = Gamer(id = 2, name = "고스톱 고박자", loserOption = listOf(LoserOption.GoBak))
        val gostopNormalLoser = Gamer(id = 3, name = "일반 패자", loserOption = listOf(LoserOption.PeaBak))
        val gostopResult = useCase(gostopWinner, listOf(gostopGoBakLoser, gostopNormalLoser), scorePerPoint)
        
        // Then: 맞고 - 고박자 본인만 계산 (다른 사람 없음)
        //   고박자 본인: 600 × 2 = 1200 (고박 2배)
        //   나무위키 규칙: "맞고 플레이 : 고박 플레이어는 2배의 비용을 지불한다" ✅
        assertEquals(1200, matgoResult.accounts[1L])
        assertEquals(-1200, matgoResult.accounts[2L])
        
        // Then: 고스톱 - 고박자가 다른 사람의 금액을 그대로 대신 냄 (2배 아님)
        //   패자(피박): 600 × 2 = 1200 → 고박자가 대신 냄: 1200 (그대로, 2배 아님)
        //   고박자 본인: 600 (고박 제외, 기본 금액만)
        //   총합: 1200 + 600 = 1800원
        //   나무위키 규칙: "고스톱 플레이 : 고박 플레이어는 다른 패자의 비용까지 대신 지불한다" (2배 언급 없음) ✅
        assertEquals(1800, gostopResult.accounts[1L])
        assertEquals(-1800, gostopResult.accounts[2L])
        assertEquals(null, gostopResult.accounts[3L])
        
        // 검증: 고스톱에서 고박자가 다른 사람의 금액을 2배로 내지 않음
        //   패자(피박) 금액: 600 × 2 = 1200원
        //   고박자가 대신 낸 금액: 1200원 (2배 아님, 그대로)
        //   고박자 본인 금액: 600원 (고박 제외)
        //   총합: 1200 + 600 = 1800원
        val normalLoserAmount = winnerScore * scorePerPoint * 2 // 피박 = 2배
        val goBakOwnAmount = winnerScore * scorePerPoint // 고박 제외, 기본 금액
        val expectedTotal = normalLoserAmount + goBakOwnAmount // 1200 + 600 = 1800
        assertEquals(expectedTotal, -gostopResult.accounts[2L]!!) // 고박자가 낸 총액
    }
    
    @Test
    fun `고박 케이스 - 고스톱 (4명) 다른 사람 금액 그대로 적용`() {
        // Given: 고스톱 4명, 승자 5점, 고박자, 일반 패자 2명
        val winner = Gamer(id = 1, name = "승자", score = 5)
        val goBakLoser = Gamer(id = 2, name = "고박자", loserOption = listOf(LoserOption.GoBak))
        val normalLoser1 = Gamer(id = 3, name = "일반 패자1", loserOption = listOf(LoserOption.PeaBak))
        val normalLoser2 = Gamer(id = 4, name = "일반 패자2")
        val scorePerPoint = 100
        
        // When: 패자 계산 (losers.size = 3이므로 고스톱)
        val result = useCase(winner, listOf(goBakLoser, normalLoser1, normalLoser2), scorePerPoint)
        
        // Then: 고스톱이므로 고박자가 다른 사람의 금액을 그대로 대신 냄
        //   패자1(피박): 500 × 2 = 1000 → 고박자가 대신 냄: 1000 (그대로)
        //   패자2: 500 → 고박자가 대신 냄: 500 (그대로)
        //   고박자 본인: 500 (고박 제외, 기본 금액만)
        //   총합: 1000 + 500 + 500 = 2000원
        assertEquals(2000, result.accounts[1L])
        assertEquals(-2000, result.accounts[2L])
        assertEquals(null, result.accounts[3L])
        assertEquals(null, result.accounts[4L])
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

