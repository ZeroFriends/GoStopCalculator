package zero.friends.gostopcalculator.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.PlayerResult
import zero.friends.domain.model.Round
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.usecase.GetPlayerListUseCase
import zero.friends.domain.usecase.GetRoundListUseCase
import zero.friends.gostopcalculator.di.factory.AssistedFactory
import zero.friends.gostopcalculator.util.viewModelFactory

data class BoardUiState(
    val game: Game = Game(),
    val gameHistory: List<Round> = emptyList(),
    val playerList: List<PlayerResult> = emptyList(),
    val showMoreDropDown: Boolean = false
)

class BoardViewModel @AssistedInject constructor(
    @Assisted private val gameId: Long,
    private val gameRepository: GameRepository,
    private val getPlayerListUseCase: GetPlayerListUseCase,
    private val getRoundListUseCase: GetRoundListUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoardUiState(Game(gameId)))
    fun getUiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val playerList = getPlayerListUseCase.invoke(gameId)
            _uiState.update { it.copy(playerList = playerList) }

            getRoundListUseCase.invoke(gameId)
                .onEach { rounds ->
                    _uiState.update { it.copy(gameHistory = rounds) }
                }.launchIn(this)

        }

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
            assistedFactory: AssistedFactory,
            gameId: Long
        ): ViewModelProvider.Factory = viewModelFactory { assistedFactory.create(gameId) }
    }
}


