package zero.friends.gostopcalculator.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.repository.GameRepository
import javax.inject.Inject

data class MainUiState(
    val games: List<Game> = emptyList(),
)

data class MoreDialogState(
    val openDialog: Boolean = false,
    val gameId: Long? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(private val gameRepository: GameRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    fun getUiState() = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow(MoreDialogState())
    fun getDialogState() = _dialogState.asStateFlow()

    init {
        gameRepository.observeGameList().onEach { gameList ->
            _uiState.update {
                it.copy(games = gameList)
            }
        }.launchIn(viewModelScope)
    }

    fun deleteGame(gameId: Long) {
        viewModelScope.launch {
            gameRepository.deleteGame(gameId)
        }
    }

    fun openDialog(gameId: Long) {
        _dialogState.update { MoreDialogState(openDialog = true, gameId = gameId) }
    }

    fun closeDialog() {
        _dialogState.update { MoreDialogState(openDialog = false, null) }
    }
}