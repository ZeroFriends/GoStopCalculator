package zero.friends.gostopcalculator.ui.precondition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.usecase.AddAutoGeneratePlayerUseCase
import zero.friends.domain.usecase.DeletePlayerUseCase
import zero.friends.domain.usecase.EditPlayerUseCase
import zero.friends.gostopcalculator.util.TimeUtil
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


data class PlayerUiState(
    val players: List<Player> = emptyList(),
    val gameName: String = "",
    val currentTime: String = TimeUtil.getCurrentTime(),
)

data class DialogState(
    val openDialog: Boolean = false,
    val originalPlayer: Player = Player(),
    val editPlayer: Player? = null,
    val error: Throwable? = null,
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val addAutoGeneratePlayerUseCase: AddAutoGeneratePlayerUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
    private val playerRepository: PlayerRepository,
    private val editPlayerUseCase: EditPlayerUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    fun getUiState() = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow(DialogState())
    fun getDialogState() = _dialogState.asStateFlow()

    private val editNameExceptionHandler =
        CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
            _dialogState.update { it.copy(openDialog = true, error = throwable) }
        }

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
            addAutoGeneratePlayerUseCase()
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

    fun openDialog(player: Player) {
        _dialogState.update {
            it.copy(openDialog = true, originalPlayer = player, editPlayer = null)
        }
    }

    fun closeDialog() {
        _dialogState.update {
            it.copy(openDialog = false, error = null)
        }
    }

    fun editPlayer(name: String) {
        viewModelScope.launch(editNameExceptionHandler) {
            val originalPlayer = _dialogState.value.originalPlayer
            val editPlayer = if (name.isEmpty()) originalPlayer else originalPlayer.copy(name = name)
            editPlayerUseCase(originalPlayer, editPlayer)
            closeDialog()
        }
    }

    fun editGameName(gameName: String) {
        viewModelScope.launch {
            val name = if (gameName.isEmpty()) getUiState().value.currentTime else gameName
            gameRepository.editGameName(name)
        }
    }

}
