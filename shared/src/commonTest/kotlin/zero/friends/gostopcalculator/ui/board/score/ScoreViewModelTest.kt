package zero.friends.gostopcalculator.ui.board.score

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Round
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.usecase.calculate.CalculateGameResultUseCase
import zero.friends.domain.usecase.calculate.CalculateLoserScoreUseCase
import zero.friends.domain.usecase.calculate.CalculateScoreOptionUseCase
import zero.friends.domain.usecase.calculate.CalculateSellScoreUseCase
import zero.friends.domain.usecase.calculate.UpdateAccountUseCase
import zero.friends.domain.usecase.option.SellingUseCase
import zero.friends.domain.usecase.option.ToggleLoserOptionUseCase
import zero.friends.domain.usecase.option.ToggleScoreOptionUseCase
import zero.friends.domain.usecase.option.UpdateOptionsUseCase
import zero.friends.domain.usecase.option.UpdateWinnerUseCase
import zero.friends.domain.util.Const

class ScoreViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var gamerRepository: MockGamerRepository
    private lateinit var ruleRepository: MockRuleRepository

    private class FakeGameRepository(private val game: Game) : GameRepository {
        override suspend fun newGame(name: String, createdAt: String): Long = game.id
        override suspend fun deleteGame(gameId: Long) = Unit
        override fun observeGameList(): Flow<List<Game>> = emptyFlow()
        override suspend fun getGame(gameId: Long): Game = game
        override fun observeGame(gameId: Long): Flow<Game> = emptyFlow()
    }

    private class FakeRoundRepository(
        private val onDeleteRound: (Long) -> Unit = {}
    ) : RoundRepository {
        override fun observeAllRound(gameId: Long): Flow<List<Round>> = emptyFlow()
        override fun observeRound(roundId: Long): Flow<List<Gamer>> = emptyFlow()
        override suspend fun createNewRound(gameId: Long): Long = 1L
        override suspend fun deleteRound(roundId: Long) {
            onDeleteRound(roundId)
        }
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        gamerRepository = MockGamerRepository()
        ruleRepository = MockRuleRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(gameId: Long = 1L, roundId: Long = 1L): ScoreViewModel {
        val updateOptionsUseCase = UpdateOptionsUseCase(gamerRepository)
        val calculateGameResultUseCase = CalculateGameResultUseCase(
            ruleRepository = ruleRepository,
            gamerRepository = gamerRepository,
            updateAccountUseCase = UpdateAccountUseCase(gamerRepository),
            calculateSellScoreUseCase = CalculateSellScoreUseCase(ruleRepository, gamerRepository),
            calculateLoserScoreUseCase = CalculateLoserScoreUseCase(ruleRepository, gamerRepository),
            calculateScoreOptionUseCase = CalculateScoreOptionUseCase(ruleRepository, gamerRepository)
        )
        return ScoreViewModel(
            gameRepository = FakeGameRepository(Game(id = gameId, name = "상태전이 테스트")),
            toggleScoreOptionUseCase = ToggleScoreOptionUseCase(gamerRepository, updateOptionsUseCase),
            toggleLoserOption = ToggleLoserOptionUseCase(gamerRepository, updateOptionsUseCase),
            updateWinnerUseCase = UpdateWinnerUseCase(gamerRepository),
            calculateGameResultUseCase = calculateGameResultUseCase,
            sellingUseCase = SellingUseCase(gamerRepository),
            roundRepository = FakeRoundRepository { deletedRoundId ->
                gamerRepository.setRoundGamers(deletedRoundId, emptyList())
            },
            gamerRepository = gamerRepository,
            gameId = gameId,
            roundId = roundId
        )
    }

    @Test
    fun `패자 단계까지 진입 후 점수기록 단계로 되돌아가 삼연뻑 즉시 종료 시 이전 승자와 박 옵션이 완벽히 초기화된다`() = runTest {
        val gameId = 1L
        val roundId = 1L
        val gamers = listOf(
            Gamer(id = 1L, playerId = 1L, gameId = gameId, roundId = roundId, name = "플레이어 1"),
            Gamer(id = 2L, playerId = 2L, gameId = gameId, roundId = roundId, name = "플레이어 2"),
            Gamer(id = 3L, playerId = 3L, gameId = gameId, roundId = roundId, name = "플레이어 3"),
        )
        gamerRepository.setRoundGamers(roundId, gamers)
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = 100),
                Rule(name = Const.Rule.Fuck, score = 500),
                Rule(name = Const.Rule.Sell, score = 500)
            )
        )

        val vm = createViewModel(gameId, roundId)
        assertEquals(Scoring, vm.uiState().value.phase)

        // Scoring -> Winner(P1=7점) -> Loser(P2=고박, 피박)
        vm.onNextPhase()
        vm.updateWinner(vm.uiState().value.playerResults[0], point = 7, go = 2)
        vm.onNextPhase()
        assertEquals(Loser, vm.uiState().value.phase)

        val p2InLoser = vm.uiState().value.playerResults[1]
        vm.selectLoser(p2InLoser, LoserOption.PeaBak)
        vm.selectLoser(p2InLoser, LoserOption.GoBak)

        // Loser -> Back -> Winner -> Back -> Scoring
        vm.onBackPhase()
        vm.onBackPhase()
        assertEquals(Scoring, vm.uiState().value.phase)
        assertNull(vm.uiState().value.winner)

        // P3가 삼연뻑 달성하여 즉시 종료
        val p3InScoring = vm.uiState().value.playerResults[2]
        vm.selectScore(p3InScoring, ScoreOption.ThreeFuck)
        vm.calculateGameResult()

        // 삼연뻑(500 * 4 = 2,000원씩 수령)만 정산되고, P1의 이전 7점 승자 정산 및 P2의 박 옵션이 모두 제거되어야 함
        val settled = gamerRepository.getRoundGamers(roundId)
        assertEquals(-2000, settled.first { it.id == 1L }.account)
        assertEquals(-2000, settled.first { it.id == 2L }.account)
        assertEquals(4000, settled.first { it.id == 3L }.account)
        assertTrue(settled.all { it.loserOption.isEmpty() })
        assertTrue(settled.all { it.winnerOption == null })
    }

    @Test
    fun `패자 단계에서 박을 선택한 플레이어가 뒤로가기 후 새 승자로 변경되면 기존 박 옵션이 자동 해제된다`() = runTest {
        val gameId = 1L
        val roundId = 2L
        val gamers = listOf(
            Gamer(id = 1L, playerId = 1L, gameId = gameId, roundId = roundId, name = "플레이어 1"),
            Gamer(id = 2L, playerId = 2L, gameId = gameId, roundId = roundId, name = "플레이어 2"),
            Gamer(id = 3L, playerId = 3L, gameId = gameId, roundId = roundId, name = "플레이어 3"),
        )
        gamerRepository.setRoundGamers(roundId, gamers)
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val vm = createViewModel(gameId, roundId)
        vm.onNextPhase() // Winner
        vm.updateWinner(vm.uiState().value.playerResults[0], point = 5, go = 0)
        vm.onNextPhase() // Loser

        // P2에게 고박 + 광박 부여
        val p2 = vm.uiState().value.playerResults[1]
        vm.selectLoser(p2, LoserOption.LightBak)
        vm.selectLoser(p2, LoserOption.GoBak)

        // 뒤로가기 -> 승자를 P2(4점)로 변경 -> 정산 확정
        vm.onBackPhase() // Winner
        vm.updateWinner(vm.uiState().value.playerResults[1], point = 4, go = 0)
        vm.onNextPhase() // Loser
        vm.calculateGameResult()

        val settled = gamerRepository.getRoundGamers(roundId)
        val settledP2 = settled.first { it.id == 2L }
        assertTrue(settledP2.loserOption.isEmpty())
        assertEquals(800, settledP2.account)
        assertEquals(-400, settled.first { it.id == 1L }.account)
        assertEquals(-400, settled.first { it.id == 3L }.account)
    }

    @Test
    fun `4인 게임에서 점수옵션 보유 플레이어가 뒤로가기 후 광팔이로 변경되면 기존 점수옵션이 자동 해제된다`() = runTest {
        val gameId = 1L
        val roundId = 3L
        val gamers = (1L..4L).map {
            Gamer(id = it, playerId = it, gameId = gameId, roundId = roundId, name = "플레이어 $it")
        }
        gamerRepository.setRoundGamers(roundId, gamers)
        ruleRepository.setRules(
            gameId,
            listOf(
                Rule(name = Const.Rule.Score, score = 100),
                Rule(name = Const.Rule.Fuck, score = 500),
                Rule(name = Const.Rule.Sell, score = 300)
            )
        )

        val vm = createViewModel(gameId, roundId)
        // 처음엔 P1이 광 1장 판매 -> Scoring에서 P2 첫뻑 체크
        vm.updateSeller(vm.uiState().value.playerResults[0], count = 1)
        vm.onNextPhase() // Scoring
        vm.selectScore(vm.uiState().value.playerResults[1], ScoreOption.FirstFuck)

        // 뒤로가기(Selling) -> 광팔이를 P2(광 2장 판매)로 변경 -> P3가 5점 승리
        vm.onBackPhase() // Selling
        vm.updateSeller(vm.uiState().value.playerResults[1], count = 2)
        vm.onNextPhase() // Scoring
        vm.onNextPhase() // Winner
        vm.updateWinner(vm.uiState().value.playerResults[2], point = 5, go = 0)
        vm.onNextPhase() // Loser
        vm.calculateGameResult()

        val settled = gamerRepository.getRoundGamers(roundId)
        val settledSellerP2 = settled.first { it.id == 2L }
        // P2는 광 2장 * 300원 * 3명 = +1,800원만 받아야 하며 이전 첫뻑 옵션은 제거되어야 함
        assertTrue(settledSellerP2.scoreOption.isEmpty())
        assertEquals(1800, settledSellerP2.account)
        assertEquals(0, settled.sumOf { it.account })
    }

    @Test
    fun `3인 게임 Winner 단계에서 뒤로가기 연속 호출 시에도 Selling 화면으로 진입하지 않는다`() = runTest {
        val gameId = 1L
        val roundId = 4L
        val gamers = (1L..3L).map {
            Gamer(id = it, playerId = it, gameId = gameId, roundId = roundId, name = "플레이어 $it")
        }
        gamerRepository.setRoundGamers(roundId, gamers)
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val vm = createViewModel(gameId, roundId)
        var escapedCount = 0
        val escapeJob = backgroundScope.launch(testDispatcher) {
            vm.escapeEvent().collect {
                escapedCount += 1
            }
        }
        assertEquals(Scoring, vm.uiState().value.phase)

        vm.onNextPhase() // Winner
        vm.updateWinner(vm.uiState().value.playerResults[0], point = 7, go = 0)
        assertEquals(Winner(true), vm.uiState().value.phase)

        // 연속 2회 뒤로가기 호출 -> Winner -> Scoring 전환 후 두 번째 호출은 escapeEvent 방출, Selling 전이 금지
        vm.onBackPhase()
        vm.onBackPhase()

        assertEquals(Scoring, vm.uiState().value.phase)
        assertNull(vm.uiState().value.winner)
        assertEquals(1, escapedCount)
        escapeJob.cancel()
    }

    @Test
    fun `Scoring 및 Winner 단계에서 onNextPhase 연타 시 단계 우회나 IllegalStateException이 발생하지 않는다`() = runTest {
        val gameId = 1L
        val roundId = 5L
        val gamers = (1L..3L).map {
            Gamer(id = it, playerId = it, gameId = gameId, roundId = roundId, name = "플레이어 $it")
        }
        gamerRepository.setRoundGamers(roundId, gamers)
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val vm = createViewModel(gameId, roundId)
        assertEquals(Scoring, vm.uiState().value.phase)

        // Scoring 단계에서 연타해도 Winner(false)를 통과해 Loser로 우회하지 않음
        vm.onNextPhase(Scoring)
        vm.onNextPhase()
        assertEquals(Winner(false), vm.uiState().value.phase)

        // 승자 입력 후 Winner(true)에서 연타해도 Loser에 멈추며 예외를 던지지 않음
        vm.updateWinner(vm.uiState().value.playerResults[0], point = 5, go = 0)
        assertEquals(Winner(true), vm.uiState().value.phase)
        vm.onNextPhase(Winner(true))
        vm.onNextPhase(Winner(true))
        vm.onNextPhase()
        assertEquals(Loser, vm.uiState().value.phase)
    }

    @Test
    fun `정산 완료 이후 뒤로가기나 패자 옵션 변경 호출은 무시되어 DB 정산 결과가 보존된다`() = runTest {
        val gameId = 1L
        val roundId = 6L
        val gamers = (1L..3L).map {
            Gamer(id = it, playerId = it, gameId = gameId, roundId = roundId, name = "플레이어 $it")
        }
        gamerRepository.setRoundGamers(roundId, gamers)
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val vm = createViewModel(gameId, roundId)
        vm.onNextPhase() // Winner
        vm.updateWinner(vm.uiState().value.playerResults[0], point = 5, go = 0)
        vm.onNextPhase() // Loser
        val p2 = vm.uiState().value.playerResults[1]
        vm.selectLoser(p2, LoserOption.PeaBak)

        vm.calculateGameResult()

        // 정산 이후 뒤로가기 연타 및 패자 박 추가 시도 모두 무시되어야 함
        val p3 = vm.uiState().value.playerResults[2]
        vm.onBackPhase()
        vm.onBackPhase()
        vm.selectLoser(p3, LoserOption.GoBak)

        assertEquals(Loser, vm.uiState().value.phase)
        val settled = gamerRepository.getRoundGamers(roundId)
        val settledP1 = settled.first { it.id == 1L }
        val settledP2 = settled.first { it.id == 2L }
        val settledP3 = settled.first { it.id == 3L }
        assertEquals(WinnerOption.Winner, settledP1.winnerOption)
        assertEquals(5, settledP1.score)
        assertEquals(1500, settledP1.account)
        assertEquals(listOf(LoserOption.PeaBak), settledP2.loserOption)
        assertEquals(-1000, settledP2.account)
        assertTrue(settledP3.loserOption.isEmpty())
        assertEquals(-500, settledP3.account)
    }

    @Test
    fun `deleteRound 호출 이후 후속 selectScore 및 selectLoser 호출은 크래시 없이 무시된다`() = runTest {
        val gameId = 1L
        val roundId = 7L
        val gamers = (1L..3L).map {
            Gamer(id = it, playerId = it, gameId = gameId, roundId = roundId, name = "플레이어 $it")
        }
        gamerRepository.setRoundGamers(roundId, gamers)
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val vm = createViewModel(gameId, roundId)
        val snapshotGamers = vm.uiState().value.playerResults
        vm.deleteRound()

        vm.selectScore(snapshotGamers[0], ScoreOption.FirstFuck)
        vm.selectLoser(snapshotGamers[1], LoserOption.GoBak)
        vm.onNextPhase()
        vm.onBackPhase()

        assertTrue(gamerRepository.getRoundGamers(roundId).isEmpty())
    }
}
