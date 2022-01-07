package zero.friends.gostopcalculator.ui.precondition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.usecase.player.AddAutoGeneratePlayerUseCase
import zero.friends.domain.usecase.player.DeletePlayerUseCase
import zero.friends.gostopcalculator.util.TimeUtil
import javax.inject.Inject


data class PlayerUiState(
    val players: List<Player> = emptyList(),
    val gameName: String = "",
    val currentTime: String = TimeUtil.getCurrentTime(),
)


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val addAutoGeneratePlayerUseCase: AddAutoGeneratePlayerUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
    private val playerRepository: PlayerRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    fun getUiState() = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    fun error() = _error.asSharedFlow()

    init {
        viewModelScope.launch {
            val gameId = gameRepository.newGame(_uiState.value.currentTime, _uiState.value.currentTime)
            playerRepository.observePlayer(gameId = gameId)
                .onEach { players ->
                    _uiState.update {
                        it.copy(players = players)
                    }
                }
                .launchIn(this)

            gameRepository.observeGameName().onEach { gameName ->
                _uiState.update { it.copy(gameName = gameName) }
            }.launchIn(this)
        }

    }

    fun addPlayer() {
        viewModelScope.launch {
            kotlin.runCatching {
                addAutoGeneratePlayerUseCase()
            }.onFailure {
                _error.emit(it.message.orEmpty())
            }
        }
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            deletePlayerUseCase.invoke(player)
        }
    }

    fun clearGame() {
        viewModelScope.launch {
            val gameId = requireNotNull(gameRepository.getCurrentGameId())
            gameRepository.deleteGame(gameId)
        }
    }

    fun editGameName(gameName: String) {
        viewModelScope.launch {
            val name = if (gameName.isEmpty()) getUiState().value.currentTime else gameName
            gameRepository.editGameName(name)
        }
    }

}
