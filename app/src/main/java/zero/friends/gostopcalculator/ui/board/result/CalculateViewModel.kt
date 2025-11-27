package zero.friends.gostopcalculator.ui.board.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.usecase.calculate.AggregateGamerResultsUseCase
import zero.friends.domain.util.Const
import zero.friends.gostopcalculator.util.mapAsync
import javax.inject.Inject

data class CalculateUiState(
    val game: Game = Game(),
    val gamer: Gamer = Gamer(),
    val gamers: List<Gamer> = emptyList()
)

@HiltViewModel
class CalculateViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val gamerRepository: GamerRepository,
    private val aggregateGamerResultsUseCase: AggregateGamerResultsUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculateUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val game = gameRepository.getGame(requireNotNull(savedStateHandle[Const.GameId]))
            gamerRepository.observeGamers(gameId = game.id)
                .mapAsync(aggregateGamerResultsUseCase::invoke)
                .onEach { result ->
                    _uiState.update { it.copy(gamers = result) }
                }
                .launchIn(this)

            _uiState.update {
                it.copy(game = game)
            }
        }
    }
}
