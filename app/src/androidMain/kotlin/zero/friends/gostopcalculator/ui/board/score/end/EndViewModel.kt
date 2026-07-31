package zero.friends.gostopcalculator.ui.board.score.end

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository

data class EndUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList()
)

@Inject
class EndViewModel(
    private val gameRepository: GameRepository,
    private val gamerRepository: GamerRepository,
    @Assisted private val gameId: Long,
    @Assisted private val roundId: Long
) : ViewModel() {
    private val _endUiState = MutableStateFlow(EndUiState())
    fun endUiState() = _endUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _endUiState.update { it.copy(game = gameRepository.getGame(gameId)) }
            gamerRepository.observeRoundGamers(roundId)
                .filterNotNull()
                .onEach { gamers ->
                    _endUiState.update { it.copy(gamers = gamers) }
                }.launchIn(viewModelScope)
        }
    }
}
