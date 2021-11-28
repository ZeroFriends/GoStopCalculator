package zero.friends.gostopcalculator.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import zero.friends.domain.model.Game
import zero.friends.domain.repository.GameRepository
import zero.friends.gostopcalculator.di.factory.ViewModelFactory
import zero.friends.gostopcalculator.util.viewModelFactory

data class BoardUiState(
    val game: Game = Game(),
    val gameHistory: List<String> = emptyList(),
    val showMoreDropDown: Boolean = false
)

class BoardViewModel @AssistedInject constructor(
    @Assisted private val gameId: Long,
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoardUiState(Game(gameId)))
    fun getUiState() = _uiState.asStateFlow()

    init {
        gameRepository.observeGame(gameId)
            .onEach { game ->
                _uiState.update {
                    it.copy(game = game)
                }
            }.launchIn(viewModelScope)
    }

    fun closeDropDown() {
        _uiState.update { it.copy(showMoreDropDown = false) }
    }

    fun openDropDown() {
        _uiState.update { it.copy(showMoreDropDown = true) }
    }

    companion object {
        fun provideFactory(
            boardViewModelFactory: ViewModelFactory.BoardViewModelFactory,
            gameId: Long
        ): ViewModelProvider.Factory = viewModelFactory { boardViewModelFactory.create(gameId) }
    }
}


