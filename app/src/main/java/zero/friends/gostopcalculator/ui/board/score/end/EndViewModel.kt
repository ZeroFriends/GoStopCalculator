package zero.friends.gostopcalculator.ui.board.score.end

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.usecase.gamer.ObserveRoundGamerUseCase
import javax.inject.Inject

data class EndUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList()
)

@HiltViewModel
class EndViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val observeRoundGamerUseCase: ObserveRoundGamerUseCase,
) : ViewModel() {
    private val _endUiState = MutableStateFlow(EndUiState())
    fun endUiState() = _endUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _endUiState.update { it.copy(game = requireNotNull(gameRepository.getCurrentGame())) }
            observeRoundGamerUseCase()
                .onEach { gamers ->
                    _endUiState.update { it.copy(gamers = gamers) }
                }.launchIn(viewModelScope)
        }

    }


}
