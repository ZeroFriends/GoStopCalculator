package zero.friends.domain.usecase.calculate.audit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.reflect.KClass
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Option
import zero.friends.domain.model.Player
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.Target
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.usecase.calculate.AggregateGamerResultsUseCase
import zero.friends.domain.usecase.calculate.BuildRoundTraceUseCase
import zero.friends.domain.usecase.calculate.CalculateGameResultUseCase
import zero.friends.domain.usecase.calculate.CalculateLoserScoreUseCase
import zero.friends.domain.usecase.calculate.CalculateScoreOptionUseCase
import zero.friends.domain.usecase.calculate.CalculateSellScoreUseCase
import zero.friends.domain.usecase.calculate.UpdateAccountUseCase
import zero.friends.domain.util.Const
import zero.friends.gostopcalculator.ui.board.result.ResultViewModel
import zero.friends.gostopcalculator.ui.board.result.ScreenType

class CalculationAuditIntegrationTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class RecordingReporter(
        private val throwOnReport: Boolean = false,
    ) : CalculationAuditReporter {
        val reports = mutableListOf<CalculationAuditReport>()

        override fun report(report: CalculationAuditReport) {
            if (throwOnReport) {
                error("Crashlytics transport failure")
            }
            reports += report
        }
    }

    private class FakeGameRepository(private val game: Game) : GameRepository {
        override suspend fun newGame(name: String, createdAt: String): Long = game.id
        override suspend fun deleteGame(gameId: Long) = Unit
        override fun observeGameList(): Flow<List<Game>> = emptyFlow()
        override suspend fun getGame(gameId: Long): Game = game
        override fun observeGame(gameId: Long): Flow<Game> = emptyFlow()
    }

    private class FlowGamerRepository(
        initialAllGamers: List<Gamer> = emptyList(),
        initialRoundGamers: List<Gamer> = emptyList(),
    ) : GamerRepository {
        val allGamersFlow = MutableStateFlow(initialAllGamers)
        val roundGamersFlow = MutableStateFlow(initialRoundGamers)

        override suspend fun getGamer(gamerId: Long): Gamer =
            roundGamersFlow.value.firstOrNull { it.id == gamerId } ?: allGamersFlow.value.first { it.id == gamerId }

        override suspend fun getRoundGamers(roundId: Long): List<Gamer> =
            roundGamersFlow.value.filter { it.roundId == roundId }

        override fun observeGamers(gameId: Long): Flow<List<Gamer>> = allGamersFlow
        override suspend fun createGamer(gameId: Long, roundId: Long, player: Player) = Unit
        override suspend fun deleteGamer(roundId: Long, player: Player) = Unit
        override suspend fun updateOption(id: Long, options: List<Option>) = Unit
        override suspend fun clearOption(id: Long, kClass: KClass<out Option>) = Unit
        override suspend fun updateAccount(gamer: Gamer, account: Int) = Unit
        override suspend fun updateScore(gamer: Gamer, score: Int, go: Int) = Unit
        override suspend fun addAccount(gamer: Gamer, account: Int) = Unit
        override suspend fun findWinner(roundId: Long): Gamer = roundGamersFlow.value.first { it.winnerOption != null }
        override suspend fun findSeller(roundId: Long): Gamer? = roundGamersFlow.value.firstOrNull { it.sellerOption != null }
        override suspend fun updateTarget(gamer: Gamer, account: Int, target: Gamer) = Unit
        override fun observeRoundGamers(roundId: Long): Flow<List<Gamer>> = roundGamersFlow
    }

    private fun rules(): List<Rule> = listOf(
        Rule(name = Const.Rule.Score, score = 100),
        Rule(name = Const.Rule.Fuck, score = 500),
        Rule(name = Const.Rule.Sell, score = 500),
    )

    @Test
    fun `DefaultCalculationAuditor는 중복 리포트를 제거하고 리포터 예외를 격리한다`() = runTest {
        val reporter = RecordingReporter()
        val auditor = DefaultCalculationAuditor(
            validator = CalculationInvariantValidator(),
            reporter = reporter,
            deduplicator = AuditDeduplicator(maxEntries = 10),
        )
        val broken = listOf(
            Gamer(id = 1, playerId = 1, name = "A", score = 3, account = 500, winnerOption = WinnerOption.Winner),
            Gamer(id = 2, playerId = 2, name = "B", account = -300),
        )

        auditor.auditRound(AuditPhase.ROUND_DETAIL, gameId = 1L, roundId = 10L, gamers = broken, rules = rules())
        auditor.auditRound(AuditPhase.ROUND_DETAIL, gameId = 1L, roundId = 10L, gamers = broken, rules = rules())
        assertEquals(1, reporter.reports.size)

        // roundId 또는 phase가 달라지면 새로 전송
        auditor.auditRound(AuditPhase.ROUND_CALCULATE, gameId = 1L, roundId = 10L, gamers = broken, rules = rules())
        auditor.auditRound(AuditPhase.ROUND_DETAIL, gameId = 1L, roundId = 11L, gamers = broken, rules = rules())
        assertEquals(3, reporter.reports.size)

        // 리포터가 예외를 던져도 호출부가 크래시하지 않음
        val throwingAuditor = DefaultCalculationAuditor(
            validator = CalculationInvariantValidator(),
            reporter = RecordingReporter(throwOnReport = true),
        )
        throwingAuditor.auditRound(AuditPhase.ROUND_CALCULATE, gameId = 1L, roundId = 10L, gamers = broken, rules = rules())
        throwingAuditor.auditSettlement(gameId = 1L, rawGamers = broken, aggregatedGamers = broken, rules = rules())
    }

    @Test
    fun `CalculateGameResultUseCase는 정상 정산 시 리포트 0건이고 손상된 저장소에서 ROUND_CALCULATE 리포트를 발생시킨다`() = runTest {
        val reporter = RecordingReporter()
        val auditor = DefaultCalculationAuditor(CalculationInvariantValidator(), reporter)
        val ruleRepo = MockRuleRepository().apply { setRules(1L, rules()) }
        val baseGamerRepo = MockGamerRepository()

        val seller = Gamer(id = 1, playerId = 1, gameId = 1L, roundId = 1L, name = "S", score = 2, sellerOption = SellerOption.Seller)
        val winner = Gamer(id = 2, playerId = 2, gameId = 1L, roundId = 1L, name = "W", score = 5, go = 2, winnerOption = WinnerOption.Winner, scoreOption = listOf(ScoreOption.FirstDdadak))
        val loser1 = Gamer(id = 3, playerId = 3, gameId = 1L, roundId = 1L, name = "L1", loserOption = listOf(LoserOption.PeaBak), scoreOption = listOf(ScoreOption.FirstFuck))
        val loser2 = Gamer(id = 4, playerId = 4, gameId = 1L, roundId = 1L, name = "L2", loserOption = listOf(LoserOption.GoBak))
        baseGamerRepo.setRoundGamers(1L, listOf(seller, winner, loser1, loser2))

        val cleanUseCase = CalculateGameResultUseCase(
            ruleRepository = ruleRepo,
            gamerRepository = baseGamerRepo,
            updateAccountUseCase = UpdateAccountUseCase(baseGamerRepo),
            calculateSellScoreUseCase = CalculateSellScoreUseCase(ruleRepo, baseGamerRepo),
            calculateLoserScoreUseCase = CalculateLoserScoreUseCase(ruleRepo, baseGamerRepo),
            calculateScoreOptionUseCase = CalculateScoreOptionUseCase(ruleRepo, baseGamerRepo),
            calculationAuditor = auditor,
        )
        cleanUseCase(gameId = 1L, roundId = 1L, seller = seller, winner = winner)
        assertEquals(0, reporter.reports.size)

        // 저장소에서 target 기록을 고의로 누락시켜 원장 불일치 유발
        val corruptedGamerRepo = object : GamerRepository by baseGamerRepo {
            override suspend fun updateTarget(gamer: Gamer, account: Int, target: Gamer) {
                // 타겟 업데이트 누락 시뮬레이션
            }
        }
        baseGamerRepo.clear()
        baseGamerRepo.setRoundGamers(2L, listOf(
            winner.copy(roundId = 2L),
            loser1.copy(roundId = 2L, loserOption = emptyList(), scoreOption = emptyList()),
        ))

        val corruptUseCase = CalculateGameResultUseCase(
            ruleRepository = ruleRepo,
            gamerRepository = corruptedGamerRepo,
            updateAccountUseCase = UpdateAccountUseCase(corruptedGamerRepo),
            calculateSellScoreUseCase = CalculateSellScoreUseCase(ruleRepo, corruptedGamerRepo),
            calculateLoserScoreUseCase = CalculateLoserScoreUseCase(ruleRepo, corruptedGamerRepo),
            calculateScoreOptionUseCase = CalculateScoreOptionUseCase(ruleRepo, corruptedGamerRepo),
            calculationAuditor = auditor,
        )
        corruptUseCase(gameId = 1L, roundId = 2L, seller = null, winner = winner.copy(roundId = 2L))

        assertEquals(1, reporter.reports.size)
        val report = reporter.reports.single()
        assertEquals(AuditPhase.ROUND_CALCULATE, report.phase)
        assertTrue(report.violations.any { it.code == CalculationViolationCode.TARGET_SUM_MISMATCH })
    }

    @Test
    fun `ResultViewModel은 누적 정산과 라운드 상세 화면에서 위반을 감시하고 중복을 차단한다`() = runTest {
        val game = Game(id = 5L, name = "테스트 모임")
        val ruleRepo = MockRuleRepository().apply { setRules(5L, rules()) }
        val cleanAll = listOf(
            Gamer(id = 1, roundId = 10, gameId = 5, playerId = 1, name = "A", account = 500, calculate = listOf(Target(2, "B", 500))),
            Gamer(id = 2, roundId = 10, gameId = 5, playerId = 2, name = "B", account = -500, calculate = listOf(Target(1, "A", -500))),
        )
        val gamerRepo = FlowGamerRepository(
            initialAllGamers = cleanAll,
            initialRoundGamers = listOf(
                cleanAll[0].copy(score = 5, winnerOption = WinnerOption.Winner),
                cleanAll[1],
            ),
        )
        val reporter = RecordingReporter()
        val auditor = DefaultCalculationAuditor(CalculationInvariantValidator(), reporter)

        // 1. 누적 정산 화면 (ScreenType.Calculate): 정상 -> 0건, 깨진 방출 2회 -> 1건(디듀프)
        ResultViewModel(
            gameRepository = FakeGameRepository(game),
            gamerRepository = gamerRepo,
            ruleRepository = ruleRepo,
            aggregateGamerResultsUseCase = AggregateGamerResultsUseCase(),
            buildRoundTraceUseCase = BuildRoundTraceUseCase(ruleRepo, gamerRepo, CalculateLoserScoreUseCase(ruleRepo, gamerRepo)),
            calculationAuditor = auditor,
            gameId = 5L,
            roundId = 0L,
            initialScreenType = ScreenType.Calculate,
        )
        assertEquals(0, reporter.reports.size)

        val brokenAll = listOf(
            cleanAll[0].copy(account = 700),
            cleanAll[1],
        )
        gamerRepo.allGamersFlow.value = brokenAll
        // 동일 데이터 재방출은 디듀프되어야 함
        gamerRepo.allGamersFlow.value = brokenAll.map { it.copy() }
        assertEquals(1, reporter.reports.size)
        assertEquals(AuditPhase.GAME_SETTLEMENT, reporter.reports.single().phase)

        // 2. 라운드 상세 화면 (ScreenType.Detail): 정상 -> 추가 0건, 깨진 상세 데이터 -> 1건
        ResultViewModel(
            gameRepository = FakeGameRepository(game),
            gamerRepository = gamerRepo,
            ruleRepository = ruleRepo,
            aggregateGamerResultsUseCase = AggregateGamerResultsUseCase(),
            buildRoundTraceUseCase = BuildRoundTraceUseCase(ruleRepo, gamerRepo, CalculateLoserScoreUseCase(ruleRepo, gamerRepo)),
            calculationAuditor = auditor,
            gameId = 5L,
            roundId = 10L,
            initialScreenType = ScreenType.Detail,
        )
        assertEquals(1, reporter.reports.size)

        gamerRepo.roundGamersFlow.value = listOf(
            cleanAll[0].copy(score = 5, account = 999, winnerOption = WinnerOption.Winner),
            cleanAll[1],
        )
        assertEquals(2, reporter.reports.size)
        assertEquals(AuditPhase.ROUND_DETAIL, reporter.reports.last().phase)
        assertTrue(reporter.reports.last().violations.any { it.code == CalculationViolationCode.TRACE_TOTAL_MISMATCH })
    }
}
