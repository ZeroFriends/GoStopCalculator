package zero.friends.gostopcalculator.ui.history

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import zero.friends.domain.model.Game
import zero.friends.domain.preference.AppPreference
import zero.friends.domain.repository.GameRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HistoryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private class FakeAppPreference(
        private var firstStart: Boolean = true,
    ) : AppPreference {
        var setFirstStartCalls = 0
            private set

        override fun setFirstStart() {
            setFirstStartCalls++
            firstStart = false
        }

        override fun isFistStart(): Boolean = firstStart
    }

    private class FakeGameRepository : GameRepository {
        val gamesFlow = MutableStateFlow<List<Game>>(emptyList())
        val deletedGameIds = mutableListOf<Long>()

        override suspend fun newGame(name: String, createdAt: String): Long = 1L

        override suspend fun deleteGame(gameId: Long) {
            deletedGameIds += gameId
            gamesFlow.value = gamesFlow.value.filterNot { it.id == gameId }
        }

        override fun observeGameList(): Flow<List<Game>> = gamesFlow

        override suspend fun getGame(gameId: Long): Game =
            gamesFlow.value.first { it.id == gameId }

        override fun observeGame(gameId: Long): Flow<Game> =
            gamesFlow.map { list -> list.first { it.id == gameId } }
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
    fun `dismissGuide clears isFirstStart flag so navigating back does not reopen guide`() {
        val pref = FakeAppPreference(firstStart = true)
        val repo = FakeGameRepository()
        val viewModel = HistoryViewModel(repo, pref)

        assertTrue(viewModel.getUiState().value.isFirstStart)
        assertEquals(1, pref.setFirstStartCalls)

        viewModel.dismissGuide()
        assertFalse(viewModel.getUiState().value.isFirstStart)
    }

    @Test
    fun `deleteGame delegates to repository and updates observed games`() {
        val pref = FakeAppPreference(firstStart = false)
        val repo = FakeGameRepository().apply {
            gamesFlow.value = listOf(
                Game(id = 10L, name = "Game 10", createdAt = "2026-01-01"),
                Game(id = 20L, name = "Game 20", createdAt = "2026-01-02"),
            )
        }
        val viewModel = HistoryViewModel(repo, pref)

        assertEquals(2, viewModel.getUiState().value.games.size)

        viewModel.deleteGame(10L)
        assertEquals(listOf(10L), repo.deletedGameIds)
        assertEquals(listOf(20L), viewModel.getUiState().value.games.map { it.id })
    }
}
