package zero.friends.domain.usecase.result

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.Target
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.usecase.calculate.CalculateGameResultUseCase
import zero.friends.domain.usecase.calculate.UpdateAccountUseCase

/**
 * account 값과 calculate의 합계가 일치하는지 검증하는 테스트
 * 
 * CalculatedGamerAdapter에서:
 * - account: gamer.account (전체 금액)
 * - calculate: gamer.calculate.sumOf { it.account } (상세 내역 합계)
 * 
 * 이 두 값이 일치해야 함
 */
class AccountCalculateConsistencyTest {

    private lateinit var gamerRepository: MockGamerRepository
    private lateinit var ruleRepository: MockRuleRepository
    private lateinit var useCase: CalculateGameResultUseCase

    @Before
    fun setup() {
        gamerRepository = MockGamerRepository()
        ruleRepository = MockRuleRepository()
        useCase = CalculateGameResultUseCase(
            ruleRepository = ruleRepository,
            gamerRepository = gamerRepository,
            updateAccountUseCase = UpdateAccountUseCase(gamerRepository),
            calculateSellScoreUseCase = zero.friends.domain.usecase.calculate.CalculateSellScoreUseCase(),
            calculateLoserScoreUseCase = zero.friends.domain.usecase.calculate.CalculateLoserScoreUseCase(),
            calculateScoreOptionUseCase = zero.friends.domain.usecase.calculate.CalculateScoreOptionUseCase()
        )
    }

    @Test
    fun `DETAIL 화면 - account와 calculate 합계 일치 검증 - 단일 라운드`() = runTest {
        // Given: 게임 1개, 라운드 1개, 3명 플레이어
        val gameId = 1L
        val roundId = 1L
        
        val winner = Gamer(
            id = 1L,
            name = "플레이어1",
            roundId = roundId,
            gameId = gameId,
            score = 7,
            winnerOption = zero.friends.domain.model.WinnerOption.Winner
        )
        val loser1 = Gamer(
            id = 2L,
            name = "플레이어2",
            roundId = roundId,
            gameId = gameId,
            loserOption = listOf(LoserOption.PeaBak)
        )
        val loser2 = Gamer(
            id = 3L,
            name = "플레이어3",
            roundId = roundId,
            gameId = gameId
        )
        
        gamerRepository.setRoundGamers(roundId, listOf(winner, loser1, loser2))
        ruleRepository.setRules(gameId, listOf(
            zero.friends.domain.model.Rule(name = "점당", score = 100),
            zero.friends.domain.model.Rule(name = "뻑", score = 100)
        ))
        
        // When: 게임 결과 계산
        useCase(gameId, roundId, seller = null, winner = winner)
        
        // When: DETAIL 화면 데이터 (해당 라운드만)
        val detailGamers = gamerRepository.getRoundGamers(roundId)
        
        // Then: 각 플레이어의 account와 calculate 합계가 일치해야 함
        detailGamers.forEach { gamer ->
            val calculateSum = gamer.calculate.sumOf { it.account }
            assertEquals(
                "${gamer.name}의 account(${gamer.account})와 calculate 합계(${calculateSum})가 일치하지 않음",
                gamer.account,
                calculateSum
            )
        }
    }

    @Test
    fun `DETAIL 화면 - account와 calculate 합계 일치 검증 - 고박 케이스`() = runTest {
        // Given: 게임 1개, 라운드 1개, 4명 플레이어, 고박 케이스
        val gameId = 1L
        val roundId = 1L
        
        val winner = Gamer(
            id = 1L,
            name = "플레이어1",
            roundId = roundId,
            gameId = gameId,
            score = 8,
            winnerOption = zero.friends.domain.model.WinnerOption.Winner
        )
        val goBakLoser = Gamer(
            id = 2L,
            name = "플레이어2",
            roundId = roundId,
            gameId = gameId,
            loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak)
        )
        val loser2 = Gamer(
            id = 3L,
            name = "플레이어3",
            roundId = roundId,
            gameId = gameId
        )
        val loser3 = Gamer(
            id = 4L,
            name = "플레이어4",
            roundId = roundId,
            gameId = gameId
        )
        
        gamerRepository.setRoundGamers(roundId, listOf(winner, goBakLoser, loser2, loser3))
        ruleRepository.setRules(gameId, listOf(
            zero.friends.domain.model.Rule(name = "점당", score = 100),
            zero.friends.domain.model.Rule(name = "뻑", score = 100)
        ))
        
        // When: 게임 결과 계산
        useCase(gameId, roundId, seller = null, winner = winner)
        
        // When: DETAIL 화면 데이터 (해당 라운드만)
        val detailGamers = gamerRepository.getRoundGamers(roundId)
        
        // Then: 각 플레이어의 account와 calculate 합계가 일치해야 함
        detailGamers.forEach { gamer ->
            val calculateSum = gamer.calculate.sumOf { it.account }
            assertEquals(
                "${gamer.name}의 account(${gamer.account})와 calculate 합계(${calculateSum})가 일치하지 않음",
                gamer.account,
                calculateSum
            )
        }
    }

    @Test
    fun `DETAIL 화면 - account와 calculate 합계 일치 검증 - 광팜 + 고박 + 뻑`() = runTest {
        // Given: 게임 1개, 라운드 1개, 4명 플레이어, 광팜 + 고박 + 뻑
        val gameId = 1L
        val roundId = 1L
        
        val seller = Gamer(
            id = 1L,
            name = "플레이어1",
            roundId = roundId,
            gameId = gameId,
            score = 2,
            sellerOption = zero.friends.domain.model.SellerOption.Seller
        )
        val winner = Gamer(
            id = 2L,
            name = "플레이어2",
            roundId = roundId,
            gameId = gameId,
            score = 7,
            scoreOption = listOf(ScoreOption.SecondFuck),
            winnerOption = zero.friends.domain.model.WinnerOption.Winner
        )
        val goBakLoser = Gamer(
            id = 3L,
            name = "플레이어3",
            roundId = roundId,
            gameId = gameId,
            loserOption = listOf(LoserOption.GoBak)
        )
        val loser2 = Gamer(
            id = 4L,
            name = "플레이어4",
            roundId = roundId,
            gameId = gameId
        )
        
        gamerRepository.setRoundGamers(roundId, listOf(seller, winner, goBakLoser, loser2))
        ruleRepository.setRules(gameId, listOf(
            zero.friends.domain.model.Rule(name = "점당", score = 100),
            zero.friends.domain.model.Rule(name = "뻑", score = 100),
            zero.friends.domain.model.Rule(name = "광팔기", score = 200)
        ))
        
        // When: 게임 결과 계산
        useCase(gameId, roundId, seller = seller, winner = winner)
        
        // When: DETAIL 화면 데이터 (해당 라운드만)
        val detailGamers = gamerRepository.getRoundGamers(roundId)
        
        // Then: 각 플레이어의 account와 calculate 합계가 일치해야 함
        detailGamers.forEach { gamer ->
            val calculateSum = gamer.calculate.sumOf { it.account }
            assertEquals(
                "${gamer.name}의 account(${gamer.account})와 calculate 합계(${calculateSum})가 일치하지 않음",
                gamer.account,
                calculateSum
            )
        }
    }

    @Test
    fun `CALCULATE 화면 - account와 calculate 합계 일치 검증`() = runTest {
        // Given: 게임 1개, 라운드 2개, 3명 플레이어
        val gameId = 1L
        val roundId1 = 1L
        val roundId2 = 2L
        
        // 라운드1
        val winner1 = Gamer(
            id = 1L,
            name = "플레이어1",
            roundId = roundId1,
            gameId = gameId,
            score = 7,
            winnerOption = zero.friends.domain.model.WinnerOption.Winner
        )
        val loser1Round1 = Gamer(
            id = 2L,
            name = "플레이어2",
            roundId = roundId1,
            gameId = gameId,
            loserOption = listOf(LoserOption.PeaBak)
        )
        val loser2Round1 = Gamer(
            id = 3L,
            name = "플레이어3",
            roundId = roundId1,
            gameId = gameId
        )
        
        gamerRepository.setRoundGamers(roundId1, listOf(winner1, loser1Round1, loser2Round1))
        ruleRepository.setRules(gameId, listOf(
            zero.friends.domain.model.Rule(name = "점당", score = 100),
            zero.friends.domain.model.Rule(name = "뻑", score = 100)
        ))
        
        useCase(gameId, roundId1, seller = null, winner = winner1)
        
        // 라운드2
        val winner2 = Gamer(
            id = 4L,
            name = "플레이어2",
            roundId = roundId2,
            gameId = gameId,
            score = 8,
            winnerOption = zero.friends.domain.model.WinnerOption.Winner
        )
        val loser1Round2 = Gamer(
            id = 5L,
            name = "플레이어1",
            roundId = roundId2,
            gameId = gameId
        )
        val loser2Round2 = Gamer(
            id = 6L,
            name = "플레이어3",
            roundId = roundId2,
            gameId = gameId
        )
        
        gamerRepository.setRoundGamers(roundId2, listOf(winner2, loser1Round2, loser2Round2))
        useCase(gameId, roundId2, seller = null, winner = winner2)
        
        // When: CALCULATE 화면 데이터 (전체 라운드 누적)
        val allGamers = gamerRepository.observeGamers(gameId).first()
        val calculateResult = allGamers
            .groupBy { it.name }
            .mapValues { (_, sameGamers) ->
                val calculate = sameGamers
                    .map { gamer -> gamer.calculate }
                    .scan<List<Target>, MutableMap<String, Target>>(mutableMapOf()) { map, targets ->
                        targets.forEach { target ->
                            val existingTarget = map[target.name]
                            if (existingTarget != null) {
                                existingTarget.account += target.account
                            } else {
                                map[target.name] = target
                            }
                        }
                        map
                    }
                    .last()
                    .values
                    .toList()
                
                Gamer(
                    name = sameGamers.first().name,
                    account = sameGamers.sumOf { gamer -> gamer.account },
                    calculate = calculate
                )
            }
            .values
            .toList()
        
        // Then: 각 플레이어의 account와 calculate 합계가 일치해야 함
        calculateResult.forEach { gamer ->
            val calculateSum = gamer.calculate.sumOf { it.account }
            assertEquals(
                "${gamer.name}의 account(${gamer.account})와 calculate 합계(${calculateSum})가 일치하지 않음",
                gamer.account,
                calculateSum
            )
        }
    }
}

