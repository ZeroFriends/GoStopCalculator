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
 * 첫따닥 계산 검증 테스트
 * 
 * 문제: 점당 200원, 승자 22점 + 첫따닥
 * - 승자: +4900 예상
 * - 패자: -4400 실제 (500원 부족)
 * 
 * 첫따닥은 "3점에 해당하는 금액"이므로 점당 × 3이어야 함
 */
class FirstDdadakCalculationTest {

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
    fun `점당 200원 승자 22점 첫따닥 - account와 calculate 합계 검증`() = runTest {
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
            Rule(name = "뻑", score = 200)  // 뻑 점수는 200원
        ))
        
        // When: 게임 결과 계산
        useCase(gameId, roundId, seller = null, winner = winner)
        
        // When: 결과 조회
        val resultGamers = mockGamerRepository.getRoundGamers(roundId)
        val resultWinner = resultGamers.find { it.id == winner.id }!!
        val resultLoser = resultGamers.find { it.id == loser.id }!!
        
        // Then: 승자 account 검증
        // 사용자 보고: 승자 +4900, 패자 -4400
        // 패자 계산: 22 × 200 = 4400원
        // 첫따닥: 4900 - 4400 = 500원 (사용자 보고 기준)
        // 하지만 manual.json에 따르면 첫따닥은 "3점에 해당하는 금액" = 200 × 3 = 600원
        // 실제 값 확인
        println("승자 account: ${resultWinner.account}")
        println("승자 calculate: ${resultWinner.calculate}")
        println("패자 account: ${resultLoser.account}")
        println("패자 calculate: ${resultLoser.calculate}")
        
        // 사용자 보고: 승자 +4900, 패자 -4400
        // 패자 계산: 22 × 200 = 4400원
        // 첫따닥: 4900 - 4400 = 500원 (사용자 보고 기준)
        // manual.json에 따르면 첫따닥은 "3점에 해당하는 금액" = 200 × 3 = 600원이어야 함
        // 하지만 사용자가 보고한 값은 500원이므로, 실제 구현이 다를 수 있음
        
        // 실제 값 확인
        val actualWinnerAccount = resultWinner.account
        val actualLoserAccount = resultLoser.account
        
        // manual.json 규칙에 따르면 첫따닥은 점당 × 3 = 200 × 3 = 600원
        // 승자 총합: 4400 + 600 = 5000원이어야 함
        val expectedWinnerAccount = 4400 + (200 * 3)  // 5000원
        assertEquals(
            "승자의 account가 예상과 다름. 패자 계산(4400) + 첫따닥(600) = 5000원이어야 함. 실제: $actualWinnerAccount, 사용자 보고: 4900",
            expectedWinnerAccount,
            actualWinnerAccount
        )
        
        // manual.json 규칙에 따르면 첫따닥은 점당 × 3 = 200 × 3 = 600원
        // 패자 총합: -4400 - 600 = -5000원이어야 함
        val expectedLoserAccount = -4400 - (200 * 3)  // -5000원
        assertEquals(
            "패자의 account가 예상과 다름. 패자 계산(-4400) + 첫따닥(-600) = -5000원이어야 함. 실제: $actualLoserAccount, 사용자 보고: -4400",
            expectedLoserAccount,
            actualLoserAccount
        )
        
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
        
        // Then: 첫따닥 금액 검증 (calculate에서)
        // 승자가 패자로부터 받는 금액:
        // - 패자 계산: 4400원
        // - 첫따닥: 600원 (3점에 해당하는 금액)
        val winnerFromLoser = resultWinner.calculate.find { it.name == loser.name }?.account ?: 0
        assertEquals(
            "승자가 패자로부터 받는 금액이 예상과 다름. 4400 + 600 = 5000원이어야 함",
            5000,
            winnerFromLoser
        )
        
        val loserToWinner = resultLoser.calculate.find { it.name == winner.name }?.account ?: 0
        assertEquals(
            "패자가 승자에게 주는 금액이 예상과 다름. -4400 - 600 = -5000원이어야 함",
            -5000,
            loserToWinner
        )
    }

    @Test
    fun `첫따닥은 뻑 점수가 아니라 점당 × 3이어야 함`() = runTest {
        // Given: 점당 100원, 뻑 점수 200원, 승자 첫따닥, 2명 게임
        val gameId = 1L
        val roundId = 1L
        
        val winner = Gamer(
            id = 1L,
            name = "승자",
            roundId = roundId,
            gameId = gameId,
            score = 7,
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
            Rule(name = "점당", score = 100),
            Rule(name = "뻑", score = 200)  // 뻑 점수는 200원이지만, 첫따닥은 점당 × 3
        ))
        
        // When: 게임 결과 계산
        useCase(gameId, roundId, seller = null, winner = winner)
        
        // When: 결과 조회
        val resultGamers = mockGamerRepository.getRoundGamers(roundId)
        val resultWinner = resultGamers.find { it.id == winner.id }!!
        
        // Then: 첫따닥은 점당 × 3 = 100 × 3 = 300원이어야 함 (뻑 점수 200원이 아님)
        // 패자 계산: 7 × 100 = 700원
        // 첫따닥: 100 × 3 = 300원
        // 총합: 700 + 300 = 1000원
        val expectedWinnerAccount = 700 + 300  // 1000원
        assertEquals(
            "첫따닥은 뻑 점수(200)가 아니라 점당 × 3(300)이어야 함",
            expectedWinnerAccount,
            resultWinner.account
        )
    }
}

