package zero.friends.gostopcalculator.ui.board.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.PlayerResult
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.usecase.ObservePlayerResultsUseCase
import javax.inject.Inject

data class ScoreUiState(
    val game: Game = Game(),
    val playerResults: List<PlayerResult> = emptyList(),
    val buttonText: String = ""
)

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val observePlayerResultsUseCase: ObservePlayerResultsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScoreUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observePlayerResultsUseCase().onEach { playerList ->
                _uiState.update {
                    it.copy(
                        game = requireNotNull(gameRepository.getCurrentGame()),
                        playerResults = playerList
                    )
                }
            }.launchIn(this)
        }

    }

}