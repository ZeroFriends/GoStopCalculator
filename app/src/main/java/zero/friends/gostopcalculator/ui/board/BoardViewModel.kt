package zero.friends.gostopcalculator.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.PlayerResult
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.usecase.ObservePlayerResultsUseCase
import zero.friends.domain.usecase.ObserveRoundListUseCase
import zero.friends.gostopcalculator.di.factory.BoardViewModelFactory
import zero.friends.gostopcalculator.util.viewModelFactory

data class BoardUiState(
    val game: Game = Game(),
    val gameHistories: Map<Long, List<Gamer>> = emptyMap(),
    val playerResults: List<PlayerResult> = emptyList(),
    val showMoreDropDown: Boolean = false
)

class BoardViewModel @AssistedInject constructor(
    @Assisted private val gameId: Long,
    private val gameRepository: GameRepository,
    private val observePlayerResultsUseCase: ObservePlayerResultsUseCase,
    private val observeRoundListUseCase: ObserveRoundListUseCase,
    private val roundRepository: RoundRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoardUiState(Game(gameId)))
    fun getUiState() = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow<Long?>(null)
    fun dialogState() = _dialogState.asStateFlow()

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
            .onEach { game ->
                _uiState.update {
                    it.copy(game = game)
                }
            }.launchIn(viewModelScope)
    }

    fun deleteRound() {
        viewModelScope.launch {
            roundRepository.deleteRound(requireNotNull(_dialogState.value))
        }
    }

    fun closeDropDown() {
        _uiState.update { it.copy(showMoreDropDown = false) }
    }

    fun openDropDown() {
        _uiState.update { it.copy(showMoreDropDown = true) }
    }

    fun closeDialog() {
        _dialogState.update { null }
    }

    fun openDialog(roundId: Long) {
        _dialogState.update { roundId }
    }

    companion object {
        fun provideFactory(
            BoardViewModelFactory: BoardViewModelFactory,
            gameId: Long
        ): ViewModelProvider.Factory = viewModelFactory { BoardViewModelFactory.createBoardViewModel(gameId) }
    }
}


