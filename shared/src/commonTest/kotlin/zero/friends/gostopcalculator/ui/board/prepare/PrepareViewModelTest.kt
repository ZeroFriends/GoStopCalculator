package zero.friends.gostopcalculator.ui.board.prepare

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
    }

    private class FakePlayerRepository(private val players: List<Player>) : PlayerRepository {
        override suspend fun deletePlayer(gameId: Long, player: Player) = Unit
        override suspend fun getPlayers(gameId: Long): List<Player> = players
        override suspend fun editPlayer(gameId: Long, player: Player, editPlayer: Player) = Unit
        override suspend fun isExistPlayer(gameId: Long, name: String): Boolean = false
        override suspend fun addPlayers(gameId: Long, players: List<Player>) = Unit
    }

    private class FakeRoundRepository : RoundRepository {
        override fun observeAllRound(gameId: Long): Flow<List<Round>> = emptyFlow()
        override fun observeRound(roundId: Long): Flow<List<Gamer>> = emptyFlow()
        override suspend fun createNewRound(gameId: Long): Long = 1L
        override suspend fun deleteRound(roundId: Long) = Unit
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
}
