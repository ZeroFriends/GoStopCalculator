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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


data class PlayerUiState(
    val players: List<Player> = emptyList(),
    val gameName: String = "",
    val currentTime: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis()),
    val dialogState: DialogState = DialogState(),
)

data class DialogState(
    val openDialog: Boolean = false,
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

    private val editNameExceptionHandler =
        CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
            _uiState.update {
                val dialogState = it.dialogState.copy(openDialog = true, error = throwable)
                it.copy(dialogState = dialogState)
            }
        }

    init {
        viewModelScope.launch {
            gameRepository.newGame(_uiState.value.currentTime, _uiState.value.currentTime)
            val gameId = gameRepository.getCurrentGameId()
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
            gameRepository.clearGame()
        }
    }

    fun openDialog(player: Player) {
        _uiState.update {
            val dialogState = it.dialogState.copy(openDialog = true, editPlayer = player)
            it.copy(dialogState = dialogState)
        }
    }

    fun closeDialog() {
        _uiState.update {
            val dialogState = it.dialogState.copy(openDialog = false, error = null)
            it.copy(dialogState = dialogState)
        }
    }

    fun editPlayer(originalPlayer: Player, editPlayer: Player) {
        viewModelScope.launch(editNameExceptionHandler) {
            editPlayerUseCase(originalPlayer, editPlayer)
            closeDialog()
        }
    }

    fun editGameName(gameName: String) {
        viewModelScope.launch {
            gameRepository.editGameName(gameName)
        }
    }

}
