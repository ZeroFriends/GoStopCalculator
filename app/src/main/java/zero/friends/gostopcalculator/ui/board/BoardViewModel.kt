package zero.friends.gostopcalculator.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.repository.GameRepository
import zero.friends.gostopcalculator.di.factory.ViewModelFactory
import zero.friends.gostopcalculator.util.viewModelFactory

data class BoardUiState(
    val game: Game = Game()
)

class BoardViewModel @AssistedInject constructor(
    @Assisted private val gameId: Long,
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoardUiState(Game(gameId)))
    fun getUiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(game = gameRepository.getCurrentGame() ?: gameRepository.getGame(gameId))
            }
        }
    }

    companion object {
        fun provideFactory(
            boardViewModelFactory: ViewModelFactory.BoardViewModelFactory,
            gameId: Long
        ): ViewModelProvider.Factory = viewModelFactory { boardViewModelFactory.create(gameId) }
    }
}


