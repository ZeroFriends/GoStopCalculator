package zero.friends.gostopcalculator.ui.board.score.end

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

data class EndUiState(
    val game: Game = Game(),
    val players: List<PlayerResult> = emptyList()
)

@HiltViewModel
class EndViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val observePlayerResultsUseCase: ObservePlayerResultsUseCase
) : ViewModel() {
    private val _endUiState = MutableStateFlow(EndUiState())
    fun endUiState() = _endUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _endUiState.update { it.copy(game = requireNotNull(gameRepository.getCurrentGame())) }
        }
        observePlayerResultsUseCase()
            .onEach { playerResults ->
                //todo 이전결과 + 총결과 combine 을 잘 해보자...
                _endUiState.update { it.copy(players = playerResults) }
            }.launchIn(viewModelScope)
    }

}
