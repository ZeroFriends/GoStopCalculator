package zero.friends.domain.usecase.calculate

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.WinnerOption

/**
 * 첫따닥 target(calculate) 검증 테스트
 * 
 * 문제: 점당 200원, 승자 22점 + 첫따닥
 * - account는 5000으로 잘 계산됨 (4400 + 600)
 * - 하지만 target에 첫따닥 금액이 반영되지 않음
 * 
 * 예상:
 * - 승자 target: [{"playerId":패자id,"name":"패자","account":5000}] (4400 + 600)
 * - 패자 target: [{"playerId":승자id,"name":"승자","account":-5000}] (-4400 - 600)
 */
class FirstDdadakTargetTest {

    private lateinit var useCase: CalculateGameResultUseCase
    private lateinit var mockRuleRepository: MockRuleRepository
    private lateinit var mockGamerRepository: MockGamerRepository

    @Before
    fun setup() {
        mockRuleRepository = MockRuleRepository()
        mockGamerRepository = MockGamerRepository()
        val updateAccountUseCase = UpdateAccountUseCase(mockGamerRepository)

        useCase = CalculateGameResultUseCase(
            ruleRepository = mockRuleRepository,
            gamerRepository = mockGamerRepository,
            updateAccountUseCase = updateAccountUseCase,
            calculateSellScoreUseCase = CalculateSellScoreUseCase(),
            calculateLoserScoreUseCase = CalculateLoserScoreUseCase(),
            calculateScoreOptionUseCase = CalculateScoreOptionUseCase()
        )
    }

    @Test
    fun `점당 200원 승자 22점 첫따닥 - target에 첫따닥 금액이 반영되어야 함`() = runTest {
        // Given: 점당 200원, 승자 22점 + 첫따닥, 2명 게임
        val gameId = 1L
        val roundId = 1L
        
        val winner = Gamer(
            id = 1L,
            name = "승자",
            roundId = roundId,
            gameId = gameId,
            score = 22,
            winnerOption = WinnerOption.Winner,
            scoreOption = listOf(ScoreOption.FirstDdadak)
        )
        val loser = Gamer(
            id = 2L,
            name = "패자",
            roundId = roundId,
            gameId = gameId
        )
        
        mockGamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        mockRuleRepository.setRules(gameId, listOf(
            Rule(name = "점당", score = 200),
            Rule(name = "뻑", score = 200)
        ))
        
        // When: 게임 결과 계산
        useCase(gameId, roundId, seller = null, winner = winner)
        
        // When: 결과 조회
        val resultGamers = mockGamerRepository.getRoundGamers(roundId)
        val resultWinner = resultGamers.find { it.id == winner.id }!!
        val resultLoser = resultGamers.find { it.id == loser.id }!!
        
        // Then: 승자 account 검증
        // 패자 계산: 22 × 200 = 4400원
        // 첫따닥: 200 × 3 = 600원
        // 총합: 4400 + 600 = 5000원
        assertEquals(5000, resultWinner.account)
        
        // Then: 패자 account 검증
        // 패자 계산: -4400원
        // 첫따닥: -600원
        // 총합: -5000원
        assertEquals(-5000, resultLoser.account)
        
        // Then: 승자 target(calculate) 검증
        // 승자가 패자로부터 받는 금액: 4400(패자 계산) + 600(첫따닥) = 5000원
        val winnerTarget = resultWinner.calculate.find { it.name == loser.name }
        assertEquals("승자의 target에 패자 정보가 있어야 함", loser.name, winnerTarget?.name)
        assertEquals("승자가 패자로부터 받는 금액이 5000원이어야 함 (4400 + 600)", 5000, winnerTarget?.account)
        
        // Then: 패자 target(calculate) 검증
        // 패자가 승자에게 주는 금액: -4400(패자 계산) - 600(첫따닥) = -5000원
        val loserTarget = resultLoser.calculate.find { it.name == winner.name }
        assertEquals("패자의 target에 승자 정보가 있어야 함", winner.name, loserTarget?.name)
        assertEquals("패자가 승자에게 주는 금액이 -5000원이어야 함 (-4400 - 600)", -5000, loserTarget?.account)
        
        // Then: account와 calculate 합계 일치 검증
        val winnerCalculateSum = resultWinner.calculate.sumOf { it.account }
        assertEquals(
            "승자의 account(${resultWinner.account})와 calculate 합계(${winnerCalculateSum})가 일치하지 않음",
            resultWinner.account,
            winnerCalculateSum
        )
        
        val loserCalculateSum = resultLoser.calculate.sumOf { it.account }
        assertEquals(
            "패자의 account(${resultLoser.account})와 calculate 합계(${loserCalculateSum})가 일치하지 않음",
            resultLoser.account,
            loserCalculateSum
        )
    }
}

