package zero.friends.gostopcalculator.ui.board.prepare

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Player
import zero.friends.domain.model.Round
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.usecase.round.CreateNewRoundUseCase

class PrepareViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private class FakeGameRepository(private val game: Game) : GameRepository {
        override suspend fun newGame(name: String, createdAt: String): Long = game.id
        override suspend fun deleteGame(gameId: Long) = Unit
        override fun observeGameList(): Flow<List<Game>> = emptyFlow()
        override suspend fun getGame(gameId: Long): Game = game
        override fun observeGame(gameId: Long): Flow<Game> = emptyFlow()
        override suspend fun updateGameName(gameId: Long, name: String) = Unit
        override suspend fun updateGameOrder(orderedGameIds: List<Long>) = Unit
    }

    private class FakePlayerRepository(private val players: List<Player>) : PlayerRepository {
        override suspend fun deletePlayer(gameId: Long, player: Player) = Unit
        override suspend fun getPlayers(gameId: Long): List<Player> = players
        override suspend fun editPlayer(gameId: Long, player: Player, editPlayer: Player) = Unit
        override suspend fun isExistPlayer(gameId: Long, name: String): Boolean = false
        override suspend fun addPlayers(gameId: Long, players: List<Player>) = Unit
    }

    private class FakeRoundRepository(
        private val onCreateRound: suspend () -> Long = { 1L },
    ) : RoundRepository {
        var createCount = 0
            private set

        override fun observeAllRound(gameId: Long): Flow<List<Round>> = emptyFlow()
        override fun observeRound(roundId: Long): Flow<List<Gamer>> = emptyFlow()
        override suspend fun createNewRound(gameId: Long): Long {
            createCount++
            return onCreateRound()
        }
        override suspend fun deleteRound(roundId: Long) = Unit
        override suspend fun getRound(roundId: Long): Round? = null
        override fun observeRoundMeta(roundId: Long): Flow<Round?> = emptyFlow()
        override suspend fun updateRoundName(roundId: Long, name: String) = Unit
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `3인 게임에서는 전체 플레이어 수가 3명으로 로드되고 3명 모두 선택 시 3 대 3 상태가 된다`() {
        val players = listOf(
            Player(id = 1L, name = "플레이어 1"),
            Player(id = 2L, name = "플레이어 2"),
            Player(id = 3L, name = "플레이어 3"),
        )
        val viewModel = PrepareViewModel(
            gameId = 100L,
            gameRepository = FakeGameRepository(Game(id = 100L, name = "3인 고스톱")),
            playerRepository = FakePlayerRepository(players),
            createNewRound = CreateNewRoundUseCase(MockGamerRepository(), FakeRoundRepository()),
        )

        assertEquals(3, viewModel.uiState().value.players.size)
        assertEquals(0, viewModel.uiState().value.selectedPlayers.size)

        players.forEach { player ->
            viewModel.onClickPlayer(check = true, player = player)
        }

        val state = viewModel.uiState().value
        assertEquals(3, state.selectedPlayers.size)
        assertEquals(3, state.players.size)
    }

    @Test
    fun `5인 게임에서 4명을 초과해 선택하면 overPlayerCount 콜백이 호출되고 선택 인원은 4명으로 유지된다`() {
        val players = (1L..5L).map { Player(id = it, name = "플레이어 $it") }
        val viewModel = PrepareViewModel(
            gameId = 200L,
            gameRepository = FakeGameRepository(Game(id = 200L, name = "5인 고스톱")),
            playerRepository = FakePlayerRepository(players),
            createNewRound = CreateNewRoundUseCase(MockGamerRepository(), FakeRoundRepository()),
        )

        var overCountTriggered = false
        players.forEach { player ->
            viewModel.onClickPlayer(
                check = true,
                player = player,
                overPlayerCount = { overCountTriggered = true }
            )
        }

        val state = viewModel.uiState().value
        assertTrue(overCountTriggered)
        assertEquals(4, state.selectedPlayers.size)
        assertEquals(5, state.players.size)
    }

    @Test
    fun `라운드 생성 중 중복 호출하면 1회만 생성되고 완료 콜백도 1회만 호출된다`() {
        val pendingRoundId = CompletableDeferred<Long>()
        val roundRepository = FakeRoundRepository(onCreateRound = { pendingRoundId.await() })
        val players = listOf(
            Player(id = 1L, name = "플레이어 1"),
            Player(id = 2L, name = "플레이어 2"),
        )
        val viewModel = PrepareViewModel(
            gameId = 300L,
            gameRepository = FakeGameRepository(Game(id = 300L, name = "중복 클릭 테스트")),
            playerRepository = FakePlayerRepository(players),
            createNewRound = CreateNewRoundUseCase(MockGamerRepository(), roundRepository),
        )
        players.forEach { viewModel.onClickPlayer(check = true, player = it) }

        val createdPairs = mutableListOf<Pair<Long, Long>>()
        viewModel.createNewRound(onCreate = { gameId, roundId -> createdPairs += gameId to roundId })
        assertTrue(viewModel.uiState().value.isSubmitting)

        viewModel.createNewRound(onCreate = { gameId, roundId -> createdPairs += gameId to roundId })
        assertEquals(1, roundRepository.createCount)
        assertTrue(createdPairs.isEmpty())

        pendingRoundId.complete(77L)
        assertEquals(listOf(300L to 77L), createdPairs)
        assertTrue(viewModel.uiState().value.isSubmitting)
    }

    @Test
    fun `라운드 생성 실패 시 isSubmitting이 해제되고 onError 호출 후 재시도가 성공한다`() {
        var shouldFail = true
        val roundRepository = FakeRoundRepository(
            onCreateRound = {
                if (shouldFail) throw IllegalStateException("DB insert failed")
                88L
            }
        )
        val players = listOf(
            Player(id = 1L, name = "플레이어 1"),
            Player(id = 2L, name = "플레이어 2"),
        )
        val viewModel = PrepareViewModel(
            gameId = 400L,
            gameRepository = FakeGameRepository(Game(id = 400L, name = "재시도 테스트")),
            playerRepository = FakePlayerRepository(players),
            createNewRound = CreateNewRoundUseCase(MockGamerRepository(), roundRepository),
        )
        players.forEach { viewModel.onClickPlayer(check = true, player = it) }

        var capturedError: Throwable? = null
        val createdPairs = mutableListOf<Pair<Long, Long>>()
        viewModel.createNewRound(
            onCreate = { gameId, roundId -> createdPairs += gameId to roundId },
            onError = { capturedError = it },
        )

        assertNotNull(capturedError)
        assertFalse(viewModel.uiState().value.isSubmitting)
        assertTrue(createdPairs.isEmpty())

        shouldFail = false
        viewModel.createNewRound(onCreate = { gameId, roundId -> createdPairs += gameId to roundId })
        assertEquals(listOf(400L to 88L), createdPairs)
    }

    @Test
    fun `제출 중에는 플레이어 선택 토글이 무시된다`() {
        val pendingRoundId = CompletableDeferred<Long>()
        val roundRepository = FakeRoundRepository(onCreateRound = { pendingRoundId.await() })
        val players = listOf(
            Player(id = 1L, name = "플레이어 1"),
            Player(id = 2L, name = "플레이어 2"),
            Player(id = 3L, name = "플레이어 3"),
        )
        val viewModel = PrepareViewModel(
            gameId = 500L,
            gameRepository = FakeGameRepository(Game(id = 500L, name = "선택 잠금 테스트")),
            playerRepository = FakePlayerRepository(players),
            createNewRound = CreateNewRoundUseCase(MockGamerRepository(), roundRepository),
        )
        viewModel.onClickPlayer(check = true, player = players[0])
        viewModel.onClickPlayer(check = true, player = players[1])

        viewModel.createNewRound(onCreate = { _, _ -> })
        assertTrue(viewModel.uiState().value.isSubmitting)

        viewModel.onClickPlayer(check = false, player = players[0])
        viewModel.onClickPlayer(check = true, player = players[2])

        assertEquals(listOf(players[0], players[1]), viewModel.uiState().value.selectedPlayers)
        pendingRoundId.complete(99L)
    }

    @Test
    fun `2명 미만 선택 시 createNewRound가 라운드를 생성하지 않는다`() {
        val roundRepository = FakeRoundRepository()
        val players = listOf(
            Player(id = 1L, name = "플레이어 1"),
            Player(id = 2L, name = "플레이어 2"),
        )
        val viewModel = PrepareViewModel(
            gameId = 600L,
            gameRepository = FakeGameRepository(Game(id = 600L, name = "최소 인원 테스트")),
            playerRepository = FakePlayerRepository(players),
            createNewRound = CreateNewRoundUseCase(MockGamerRepository(), roundRepository),
        )
        viewModel.onClickPlayer(check = true, player = players[0])

        var createdCount = 0
        viewModel.createNewRound(onCreate = { _, _ -> createdCount++ })

        assertEquals(0, roundRepository.createCount)
        assertEquals(0, createdCount)
        assertFalse(viewModel.uiState().value.isSubmitting)
    }

    @Test
    fun `동일 플레이어를 중복 선택해도 selectedPlayers에는 한 번만 포함된다`() {
        val players = listOf(
            Player(id = 1L, name = "플레이어 1"),
            Player(id = 2L, name = "플레이어 2"),
        )
        val viewModel = PrepareViewModel(
            gameId = 650L,
            gameRepository = FakeGameRepository(Game(id = 650L, name = "중복 선택 테스트")),
            playerRepository = FakePlayerRepository(players),
            createNewRound = CreateNewRoundUseCase(MockGamerRepository(), FakeRoundRepository()),
        )

        viewModel.onClickPlayer(check = true, player = players[0])
        viewModel.onClickPlayer(check = true, player = players[0])

        assertEquals(listOf(players[0]), viewModel.uiState().value.selectedPlayers)
    }

    @Test
    fun `CancellationException 발생 시 isSubmitting이 복구되고 onError와 onCreate는 호출되지 않는다`() {
        val roundRepository = FakeRoundRepository(
            onCreateRound = { throw CancellationException("Scope cancelled") }
        )
        val players = listOf(
            Player(id = 1L, name = "플레이어 1"),
            Player(id = 2L, name = "플레이어 2"),
        )
        val viewModel = PrepareViewModel(
            gameId = 700L,
            gameRepository = FakeGameRepository(Game(id = 700L, name = "취소 테스트")),
            playerRepository = FakePlayerRepository(players),
            createNewRound = CreateNewRoundUseCase(MockGamerRepository(), roundRepository),
        )
        players.forEach { viewModel.onClickPlayer(check = true, player = it) }

        var createCalled = false
        var errorCalled = false
        viewModel.createNewRound(
            onCreate = { _, _ -> createCalled = true },
            onError = { errorCalled = true },
        )

        assertFalse(createCalled)
        assertFalse(errorCalled)
        assertFalse(viewModel.uiState().value.isSubmitting)
    }
}
