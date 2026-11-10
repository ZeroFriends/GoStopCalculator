package zero.friends.gostopcalculator.ui.precondition

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.max_player_alert
import zero.friends.gostopcalculator.shared.generated.resources.player_format
import zero.friends.gostopcalculator.util.TimeUtil

data class PlayerUiState(
    val players: SnapshotStateList<Player> = mutableStateListOf(),
    val currentTime: String = TimeUtil.getCurrentTime(),
    val scrollIndex: Int = 0
)

class PlayerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    fun getUiState() = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    fun error() = _error.asSharedFlow()

    private var playerIdCache = 0

    fun addPlayer() {
        if (_uiState.value.players.size >= LIMIT_PLAYER) {
            viewModelScope.launch {
                _error.emit(getString(Res.string.max_player_alert))
            }
            return
        }
        viewModelScope.launch {
            val defaultName = getString(Res.string.player_format, ++playerIdCache)
            _uiState.update {
                it.players.add(
                    Player(
                        name = defaultName
                    )
                )
                it.copy(scrollIndex = it.players.lastIndex)
            }
        }
    }

    fun editPlayer(originalPlayer: Player, name: String) {
        val editPlayer = if (name.isEmpty()) originalPlayer else originalPlayer.copy(name = name)
        val isExist = _uiState.value.players.any { it != originalPlayer && it.name == name }
        if (isExist) throw IllegalStateException("Duplicate player name")
        val index = _uiState.value.players.indexOf(originalPlayer)
        if (index != -1) {
            _uiState.value.players[index] = editPlayer
        }
    }

    fun removePlayer(player: Player) {
        _uiState.update {
            val index = it.players.indexOf(player)
            it.players.remove(player)
            it.copy(scrollIndex = index)
        }
    }

    companion object {
        const val LIMIT_PLAYER = 10
    }
}
