package zero.friends.gostopcalculator.ui.board.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.PlayerResult
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.usecase.ObservePlayerResultsUseCase
import zero.friends.domain.usecase.round.ObserveRoundListUseCase
import zero.friends.domain.util.Const
import javax.inject.Inject

data class BoardUiState(
    val game: Game = Game(),
    val gameHistories: Map<Long, List<Gamer>> = emptyMap(),
    val playerResults: List<PlayerResult> = emptyList(),
)

@HiltViewModel
class BoardViewModel @Inject constructor(
    gameRepository: GameRepository,
    private val observePlayerResultsUseCase: ObservePlayerResultsUseCase,
    private val observeRoundListUseCase: ObserveRoundListUseCase,
    private val roundRepository: RoundRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val gameId: Long = requireNotNull(savedStateHandle.get(Const.GameId))
    private val _uiState = MutableStateFlow(BoardUiState(Game(gameId)))
    fun getUiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observePlayerResultsUseCase(gameId).onEach { playerList ->
                _uiState.update { it.copy(playerResults = playerList) }
            }.launchIn(this)

            observeRoundListUseCase.invoke(gameId)
                .onEach { roundLists ->
                    _uiState.update { it.copy(gameHistories = roundLists) }
                }
                .launchIn(this)

        }

        gameRepository.observeGame(gameId)
            .filterNotNull()
            .onEach { game ->
                _uiState.update {
                    it.copy(game = game)
                }
            }.launchIn(viewModelScope)
    }

    fun deleteRound(roundId: Long) {
        viewModelScope.launch {
            roundRepository.deleteRound(roundId)
        }
    }

}


