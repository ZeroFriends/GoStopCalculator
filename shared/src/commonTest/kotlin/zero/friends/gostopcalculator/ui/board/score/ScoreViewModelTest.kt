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
        override suspend fun updateGameName(gameId: Long, name: String) = Unit
        override suspend fun updateGameOrder(orderedGameIds: List<Long>) = Unit
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
        override suspend fun getRound(roundId: Long): Round? = null
        override fun observeRoundMeta(roundId: Long): Flow<Round?> = emptyFlow()
        override suspend fun updateRoundName(roundId: Long, name: String) = Unit
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

    @Test
    fun `흔들기·폭탄 배수는 범위 클램프·단계 복귀 보존·승자 교체 초기화·DB 저장이 정확히 동작한다`() = runTest {
        val gameId = 1L
        val roundId = 8L
        val gamers = (1L..3L).map {
            Gamer(id = it, playerId = it, gameId = gameId, roundId = roundId, name = "플레이어 $it")
        }
        gamerRepository.setRoundGamers(roundId, gamers)
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val vm = createViewModel(gameId, roundId)
        vm.onNextPhase() // Winner

        // 승자 1에게 점수 + 흔들기(상한 초과 -> 3 클램프) + 폭탄(1) 적용
        val p1 = vm.uiState().value.playerResults[0]
        vm.updateWinner(p1, point = 5, go = 0, shakeCount = 9, bombCount = 1)
        assertEquals(3, vm.uiState().value.winner?.shakeCount)
        assertEquals(1, vm.uiState().value.winner?.bombCount)

        // Loser 진입 후 다시 뒤로가기 -> 승자 흔들기/폭탄 보존
        vm.onNextPhase()
        assertEquals(Loser, vm.uiState().value.phase)
        vm.onBackPhase()
        assertEquals(Winner(true), vm.uiState().value.phase)
        assertEquals(3, vm.uiState().value.winner?.shakeCount)
        assertEquals(1, vm.uiState().value.winner?.bombCount)

        // 다른 승자(플레이어 2)로 교체 시 플레이어 1 배수 초기화 및 새 승자 배수 반영
        val p2 = vm.uiState().value.playerResults[1]
        vm.updateWinner(p2, point = 4, go = 0, shakeCount = 1, bombCount = 1)
        assertEquals(0, vm.uiState().value.playerResults[0].shakeCount)
        assertEquals(0, vm.uiState().value.playerResults[0].bombCount)
        assertEquals(1, vm.uiState().value.winner?.shakeCount)
        assertEquals(1, vm.uiState().value.winner?.bombCount)

        // 정산 확정 -> DB 저장 및 배수 정산 검증 (4점 * 100 * 4 = 1,600원/패자)
        vm.onNextPhase() // Loser
        vm.calculateGameResult()
        val settled = gamerRepository.getRoundGamers(roundId)
        val settledP2 = settled.first { it.id == 2L }
        assertEquals(1, settledP2.shakeCount)
        assertEquals(1, settledP2.bombCount)
        assertEquals(3200, settledP2.account)
        assertEquals(-1600, settled.first { it.id == 1L }.account)
        assertEquals(-1600, settled.first { it.id == 3L }.account)
    }

    @Test
    fun `비승자 0입력은 기존 승자 배수를 지우지 않으며, 승자 해제·Scoring 복귀·연속 흔들기 폭탄 탭을 정확히 처리한다`() = runTest {
        val gameId = 1L
        val roundId = 9L
        val gamers = (1L..4L).map {
            Gamer(id = it, playerId = it, gameId = gameId, roundId = roundId, name = "플레이어 $it")
        }
        gamerRepository.setRoundGamers(roundId, gamers)
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val vm = createViewModel(gameId, roundId)
        vm.updateSeller(gamers.last(), 1L)
        vm.onNextPhase() // Selling -> Scoring
        vm.onNextPhase() // Scoring -> Winner

        // 승자(플레이어 1): +5점 ➔ 흔들기 2회 ➔ 폭탄 1회 원자적 연속 호출
        vm.updateWinnerPoint(1L, 5L)
        vm.cycleWinnerShake(1L)
        vm.cycleWinnerShake(1L)
        vm.cycleWinnerBomb(1L)
        assertEquals(2, vm.uiState().value.winner?.shakeCount)
        assertEquals(1, vm.uiState().value.winner?.bombCount)

        // 비승자(플레이어 2) 필드에서 0 입력이 발생해도 기존 승자(플레이어 1) 상태 보존
        vm.updateWinnerPoint(2L, 0L)
        assertEquals(1L, vm.uiState().value.winner?.id)
        assertEquals(2, vm.uiState().value.winner?.shakeCount)
        assertEquals(1, vm.uiState().value.winner?.bombCount)

        // 승자 본인을 0점·0고로 내리면 승자 및 흔들기/폭탄 모두 초기화
        vm.updateWinnerPoint(1L, 0L)
        assertNull(vm.uiState().value.winner)
        assertEquals(0, vm.uiState().value.playerResults[0].shakeCount)
        assertEquals(0, vm.uiState().value.playerResults[0].bombCount)

        // 다시 승자 설정 후 Winner -> Scoring 뒤로가기 시 배수 초기화
        vm.adjustWinnerPoint(1L, 3L)
        vm.cycleWinnerShake(1L)
        assertEquals(1, vm.uiState().value.winner?.shakeCount)
        vm.onBackPhase()
        assertEquals(Scoring, vm.uiState().value.phase)
        assertNull(vm.uiState().value.winner)
        assertEquals(0, vm.uiState().value.playerResults[0].shakeCount)
        assertEquals(0, vm.uiState().value.playerResults[0].bombCount)
    }
}
