package zero.friends.gostopcalculator.ui.precondition

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.util.TimeUtil
import javax.inject.Inject


data class PlayerUiState(
    val players: SnapshotStateList<Player> = mutableStateListOf(),
    val currentTime: String = TimeUtil.getCurrentTime(),
    val scrollIndex: Int = 0
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    fun getUiState() = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    fun error() = _error.asSharedFlow()

    private var playerIdCache = 0

    fun addPlayer() {
        runCatching {
            _uiState.update {
                require(_uiState.value.players.size < LIMIT_PLAYER) {
                    "플레이어는 10명까지 선택 가능합니다."
                }
                it.players.add(
                    Player(
                        name = String.format(
                            appContext.getString(R.string.player_format),
                            ++playerIdCache
                        )
                    )
                )
                it.copy(scrollIndex = it.players.lastIndex)
            }
        }.onFailure {
            viewModelScope.launch {
                _error.emit(it.message.orEmpty())
            }
        }
    }

    fun editPlayer(originalPlayer: Player, name: String) {
        val editPlayer = if (name.isEmpty()) originalPlayer else originalPlayer.copy(name = name)
        val isExist = _uiState.value.players.any { it.name == name }
        if (isExist) throw IllegalStateException("중복된 이름이 존재합니다.")
        val index = _uiState.value.players.indexOf(originalPlayer)
        _uiState.value.players[index] = editPlayer
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
