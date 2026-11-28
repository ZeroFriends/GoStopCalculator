package zero.friends.gostopcalculator.ui.history

import kotlinx.coroutines.CompletableDeferred
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
        var failOnUpdateOrder: Boolean = false
        var delayUpdateOrderGate: CompletableDeferred<Unit>? = null

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

        override suspend fun updateGameName(gameId: Long, name: String) {
            gamesFlow.value = gamesFlow.value.map { game ->
                if (game.id == gameId) game.copy(name = name) else game
            }
        }

        override suspend fun updateGameOrder(orderedGameIds: List<Long>) {
            delayUpdateOrderGate?.await()
            if (failOnUpdateOrder) throw IllegalStateException("DB write failed")
            val orderMap = orderedGameIds.withIndex().associate { (index, id) -> id to index }
            gamesFlow.value = gamesFlow.value.map { game ->
                val newOrder = orderMap[game.id]
                if (newOrder != null) game.copy(sortOrder = newOrder) else game
            }.sortedWith(compareBy({ it.sortOrder }, { it.id }))
        }
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

    @Test
    fun `renameGame trims whitespace, enforces 15-char limit, ignores blank, and updates games`() {
        val pref = FakeAppPreference(firstStart = false)
        val repo = FakeGameRepository().apply {
            gamesFlow.value = listOf(
                Game(id = 10L, name = "기존 게임", createdAt = "2026-11-27"),
            )
        }
        val viewModel = HistoryViewModel(repo, pref)

        viewModel.renameGame(10L, "   금요일 밤 고스톱   ")
        assertEquals("금요일 밤 고스톱", viewModel.getUiState().value.games.single().name)

        // 공백-only 입력은 기존 이름을 유지해야 한다.
        viewModel.renameGame(10L, "   \n  ")
        assertEquals("금요일 밤 고스톱", viewModel.getUiState().value.games.single().name)

        // 최대 15자 초과 시 절단 및 후행 공백 trimEnd 검증
        viewModel.renameGame(10L, "12345678901234   x")
        assertEquals("12345678901234", viewModel.getUiState().value.games.single().name)
    }

    @Test
    fun `reorderGames handles duplicates, partial ids, empty list, and persists order`() {
        val pref = FakeAppPreference(firstStart = false)
        val repo = FakeGameRepository().apply {
            gamesFlow.value = listOf(
                Game(id = 10L, name = "게임 1", createdAt = "2026-11-26", sortOrder = 0),
                Game(id = 20L, name = "게임 2", createdAt = "2026-11-27", sortOrder = 1),
                Game(id = 30L, name = "게임 3", createdAt = "2026-11-28", sortOrder = 2),
            )
        }
        val viewModel = HistoryViewModel(repo, pref)

        // 빈 리스트 무시
        viewModel.reorderGames(emptyList())
        assertEquals(listOf(10L, 20L, 30L), viewModel.getUiState().value.games.map { it.id })

        // 중복 및 부분 ID 전달 시 중복 없이 나머지 항목 보존
        viewModel.reorderGames(listOf(30L, 30L, 10L, 999L))

        val reordered = viewModel.getUiState().value.games
        assertEquals(listOf(30L, 10L, 20L), reordered.map { it.id })
        assertEquals(listOf(0, 1, 2), reordered.map { it.sortOrder })
        assertEquals(listOf(30L, 10L, 20L), repo.gamesFlow.value.map { it.id })
    }

    @Test
    fun `reorderGames recovers from delete during flight and rolls back on DB failure`() {
        val pref = FakeAppPreference(firstStart = false)
        val gate = CompletableDeferred<Unit>()
        val repo = FakeGameRepository().apply {
            gamesFlow.value = listOf(
                Game(id = 10L, name = "게임 1", createdAt = "2026-11-26", sortOrder = 0),
                Game(id = 20L, name = "게임 2", createdAt = "2026-11-27", sortOrder = 1),
                Game(id = 30L, name = "게임 3", createdAt = "2026-11-28", sortOrder = 2),
            )
            delayUpdateOrderGate = gate
        }
        val viewModel = HistoryViewModel(repo, pref)

        // 비동기 정렬 중 게임 10 삭제 + 신규 게임 40 추가 시 상대 순서 유지 및 완료 후 정상 해제
        viewModel.reorderGames(listOf(30L, 10L, 20L))
        repo.gamesFlow.value = listOf(
            Game(id = 20L, name = "게임 2", createdAt = "2026-11-27", sortOrder = 0),
            Game(id = 30L, name = "게임 3", createdAt = "2026-11-28", sortOrder = 1),
            Game(id = 40L, name = "게임 4", createdAt = "2026-11-29", sortOrder = 2),
        )
        assertEquals(listOf(30L, 20L, 40L), viewModel.getUiState().value.games.map { it.id })

        gate.complete(Unit)
        assertEquals(listOf(30L, 20L, 40L), viewModel.getUiState().value.games.map { it.id })

        // DB 쓰기 실패 시 마지막 확정 순서로 롤백
        repo.delayUpdateOrderGate = null
        repo.failOnUpdateOrder = true
        viewModel.reorderGames(listOf(40L, 20L, 30L))
        assertEquals(listOf(30L, 20L, 40L), viewModel.getUiState().value.games.map { it.id })
    }
}
