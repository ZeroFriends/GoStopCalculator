package zero.friends.gostopcalculator.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import zero.friends.domain.model.Game
import zero.friends.domain.preference.AppPreference
import zero.friends.domain.repository.GameRepository

import me.tatarka.inject.annotations.Inject

data class HistoryUiState(
    val games: List<Game> = emptyList(),
    val isFirstStart: Boolean = false
)

@Inject
class HistoryViewModel(
    private val gameRepository: GameRepository,
    appPreference: AppPreference
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        HistoryUiState(
            isFirstStart = appPreference.isFistStart()
        )
    )

    private data class PendingOrder(
        val version: Long,
        val ids: List<Long>,
    )

    private val reorderMutex = Mutex()
    private var orderVersion = 0L
    private var inFlightReorders = 0
    private var lastConfirmedGames: List<Game> = emptyList()
    private var pendingOrder: PendingOrder? = null

    fun getUiState() = _uiState.asStateFlow()

    init {
        appPreference.setFirstStart()
        gameRepository.observeGameList().onEach { gameList ->
            lastConfirmedGames = gameList
            _uiState.update { state ->
                val activePending = pendingOrder
                val merged = if (activePending == null) {
                    gameList
                } else {
                    val liveSet = gameList.map { it.id }.toSet()
                    val survivingTargets = activePending.ids.filter { it in liveSet }
                    val targetSet = survivingTargets.toSet()
                    val liveRelative = gameList.map { it.id }.filter { it in targetSet }
                    if (inFlightReorders == 0 && liveRelative == survivingTargets) {
                        pendingOrder = null
                        gameList
                    } else {
                        val byId = gameList.associateBy { it.id }
                        val reordered = buildList {
                            survivingTargets.forEach { id -> byId[id]?.let(::add) }
                            gameList.forEach { game ->
                                if (game.id !in targetSet) add(game)
                            }
                        }.mapIndexed { index, game -> game.copy(sortOrder = index) }
                        pendingOrder = activePending.copy(ids = reordered.map { it.id })
                        reordered
                    }
                }
                state.copy(games = merged)
            }
        }.launchIn(viewModelScope)
    }

    fun dismissGuide() {
        _uiState.update { it.copy(isFirstStart = false) }
    }

    fun deleteGame(gameId: Long) {
        viewModelScope.launch {
            gameRepository.deleteGame(gameId)
        }
    }

    fun renameGame(gameId: Long, name: String) {
        val normalized = name
            .replace("\r", "")
            .replace("\n", " ")
            .trim()
            .take(MAX_GAME_NAME_LENGTH)
            .trimEnd()
        if (normalized.isBlank()) return
        viewModelScope.launch {
            gameRepository.updateGameName(gameId, normalized)
        }
    }

    fun reorderGames(orderedGameIds: List<Long>) {
        val distinctIds = orderedGameIds.distinct()
        if (distinctIds.isEmpty()) return

        val stateGames = _uiState.value.games
        val byId = stateGames.associateBy { it.id }
        val orderedSet = distinctIds.toSet()
        val reordered = buildList {
            distinctIds.forEach { id ->
                byId[id]?.let { add(it) }
            }
            stateGames.forEach { game ->
                if (game.id !in orderedSet) {
                    add(game)
                }
            }
        }.mapIndexed { index, game -> game.copy(sortOrder = index) }

        val resolvedIds = reordered.map { it.id }
        if (resolvedIds.isEmpty()) return

        val myVersion = ++orderVersion
        inFlightReorders++
        pendingOrder = PendingOrder(version = myVersion, ids = resolvedIds)
        _uiState.update { it.copy(games = reordered) }

        viewModelScope.launch {
            try {
                reorderMutex.withLock {
                    val target = if (pendingOrder?.version == myVersion) {
                        pendingOrder?.ids ?: resolvedIds
                    } else {
                        return@withLock
                    }
                    gameRepository.updateGameOrder(target)
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (_: Throwable) {
                if (pendingOrder?.version == myVersion) {
                    pendingOrder = null
                    _uiState.update { it.copy(games = lastConfirmedGames) }
                }
            } finally {
                inFlightReorders = (inFlightReorders - 1).coerceAtLeast(0)
                if (inFlightReorders == 0 && pendingOrder != null) {
                    val liveSet = lastConfirmedGames.map { it.id }.toSet()
                    val currentPending = pendingOrder
                    if (currentPending != null) {
                        val survivingTargets = currentPending.ids.filter { it in liveSet }
                        val targetSet = survivingTargets.toSet()
                        val liveRelative = lastConfirmedGames.map { it.id }.filter { it in targetSet }
                        if (liveRelative == survivingTargets) {
                            pendingOrder = null
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MAX_GAME_NAME_LENGTH = 15
    }
}