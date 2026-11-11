package zero.friends.gostopcalculator.ui.precondition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.max_player_alert
import zero.friends.gostopcalculator.shared.generated.resources.player_format
import zero.friends.gostopcalculator.util.TimeUtil

data class PlayerUiState(
    val players: List<Player> = emptyList(),
    val currentTime: String = TimeUtil.getCurrentTime(),
    val scrollIndex: Int = 0
)

class PlayerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    fun getUiState() = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    fun error() = _error.asSharedFlow()

    private val addMutex = Mutex()
    private var playerIdCache = 0

    fun addPlayer() {
        viewModelScope.launch {
            addMutex.withLock {
                val currentPlayers = _uiState.value.players
                if (currentPlayers.size >= LIMIT_PLAYER) {
                    _error.emit(getString(Res.string.max_player_alert))
                    return@withLock
                }

                val existingNames = currentPlayers.map(Player::name).toSet()
                var defaultName: String
                do {
                    defaultName = getString(Res.string.player_format, ++playerIdCache)
                } while (defaultName in existingNames)

                _uiState.update { state ->
                    if (state.players.size >= LIMIT_PLAYER) return@update state
                    val updated = state.players + Player(name = defaultName)
                    state.copy(
                        players = updated,
                        scrollIndex = updated.lastIndex
                    )
                }
            }
        }
    }

    fun editPlayer(originalPlayer: Player, name: String) {
        val trimmedName = name.trim()
        val updatedName = trimmedName.ifEmpty { originalPlayer.name }
        val currentPlayers = _uiState.value.players
        val isExist = currentPlayers.any { it != originalPlayer && it.name == updatedName }
        if (isExist) throw IllegalStateException("Duplicate player name")

        _uiState.update { state ->
            val index = state.players.indexOf(originalPlayer)
            if (index == -1) return@update state
            val updated = state.players.toMutableList().apply {
                this[index] = originalPlayer.copy(name = updatedName)
            }
            state.copy(players = updated)
        }
    }

    fun removePlayer(player: Player) {
        _uiState.update { state ->
            val index = state.players.indexOf(player)
            if (index == -1) return@update state
            val updated = state.players.toMutableList().apply { removeAt(index) }
            val nextScrollIndex = if (updated.isEmpty()) 0 else index.coerceAtMost(updated.lastIndex)
            state.copy(
                players = updated,
                scrollIndex = nextScrollIndex
            )
        }
    }

    companion object {
        const val LIMIT_PLAYER = 10
    }
}
