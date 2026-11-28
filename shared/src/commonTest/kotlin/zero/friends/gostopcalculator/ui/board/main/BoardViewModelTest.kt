package zero.friends.gostopcalculator.ui.board.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Player
import zero.friends.domain.model.Round
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.usecase.ObservePlayerResultsUseCase
import zero.friends.domain.usecase.round.ObserveRoundListUseCase
import zero.friends.domain.mock.MockGamerRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BoardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private class FakeGameRepository(initialGame: Game) : GameRepository {
        val gameFlow = MutableStateFlow(initialGame)

        override suspend fun newGame(name: String, createdAt: String): Long = gameFlow.value.id
        override suspend fun deleteGame(gameId: Long) = Unit
        override fun observeGameList(): Flow<List<Game>> = gameFlow.map { listOf(it) }
        override suspend fun getGame(gameId: Long): Game = gameFlow.value
        override fun observeGame(gameId: Long): Flow<Game> = gameFlow
        override suspend fun updateGameName(gameId: Long, name: String) {
            if (gameFlow.value.id == gameId) {
                gameFlow.value = gameFlow.value.copy(name = name)
            }
        }
        override suspend fun updateGameOrder(orderedGameIds: List<Long>) = Unit
    }

    private class FakeRoundRepository(initialRounds: List<Round>) : RoundRepository {
        val roundsFlow = MutableStateFlow(initialRounds)

        override fun observeAllRound(gameId: Long): Flow<List<Round>> =
            roundsFlow.map { list -> list.filter { it.gameId == gameId } }

        override fun observeRound(roundId: Long): Flow<List<Gamer>> =
            MutableStateFlow(emptyList())

        override suspend fun createNewRound(gameId: Long): Long = 1L

        override suspend fun deleteRound(roundId: Long) {
            roundsFlow.value = roundsFlow.value.filterNot { it.id == roundId }
        }

        override suspend fun getRound(roundId: Long): Round? =
            roundsFlow.value.firstOrNull { it.id == roundId }

        override fun observeRoundMeta(roundId: Long): Flow<Round?> =
            roundsFlow.map { list -> list.firstOrNull { it.id == roundId } }

        override suspend fun updateRoundName(roundId: Long, name: String) {
            roundsFlow.value = roundsFlow.value.map { round ->
                if (round.id == roundId) round.copy(name = name) else round
            }
        }
    }

    private class FakePlayerRepository(private val players: List<Player>) : PlayerRepository {
        override suspend fun deletePlayer(gameId: Long, player: Player) = Unit
        override suspend fun getPlayers(gameId: Long): List<Player> = players
        override suspend fun editPlayer(gameId: Long, player: Player, editPlayer: Player) = Unit
        override suspend fun isExistPlayer(gameId: Long, name: String): Boolean = false
        override suspend fun addPlayers(gameId: Long, players: List<Player>) = Unit
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
    fun `renameRound sets custom name, enforces 20-char limit, and clears back to default on blank`() {
        val gameId = 1L
        val gameRepo = FakeGameRepository(Game(id = gameId, name = "테스트 판"))
        val roundRepo = FakeRoundRepository(
            listOf(
                Round(id = 10L, gameId = gameId, name = ""),
                Round(id = 11L, gameId = gameId, name = ""),
            )
        )
        val gamerRepo = MockGamerRepository()
        val playerRepo = FakePlayerRepository(listOf(Player(id = 1L, name = "P1")))

        val viewModel = BoardViewModel(
            gameId = gameId,
            gameRepository = gameRepo,
            observePlayerResultsUseCase = ObservePlayerResultsUseCase(playerRepo, gamerRepo),
            observeRoundListUseCase = ObserveRoundListUseCase(gamerRepo),
            roundRepository = roundRepo,
        )

        assertEquals("", viewModel.getUiState().value.roundNames[10L])

        // 앞뒤 공백·개행 제거 후 커스텀 이름 반영
        viewModel.renameRound(10L, "  광박 대역전판\n  ")
        assertEquals("광박 대역전판", viewModel.getUiState().value.roundNames[10L])

        // 20자 초과 절단 검증
        viewModel.renameRound(11L, "1234567890123456789012345")
        assertEquals("12345678901234567890", viewModel.getUiState().value.roundNames[11L])

        // 공백/빈 문자열 저장 시 기본 이름("")으로 초기화
        viewModel.renameRound(10L, "   ")
        assertEquals("", viewModel.getUiState().value.roundNames[10L])

        // 최대 길이 직전 후행 공백은 trimEnd 처리되며, deleteRound 시 roundNames에서도 함께 제거된다.
        viewModel.renameRound(10L, "1234567890123456789   x")
        assertEquals("1234567890123456789", viewModel.getUiState().value.roundNames[10L])

        viewModel.deleteRound(10L)
        assertEquals(null, viewModel.getUiState().value.roundNames[10L])
        assertEquals("12345678901234567890", viewModel.getUiState().value.roundNames[11L])
    }
}
