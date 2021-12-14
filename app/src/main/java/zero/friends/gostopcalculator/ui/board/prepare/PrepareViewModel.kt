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
import timber.log.Timber
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.usecase.AddGamerUseCase
import zero.friends.gostopcalculator.di.factory.PrePareViewModelFactory
import zero.friends.gostopcalculator.util.viewModelFactory

data class PrepareUiState(
    val game: Game = Game(),
    val players: List<Player> = emptyList(),
    val gamer: List<Gamer> = emptyList()
)

class PrepareViewModel @AssistedInject constructor(
    @Assisted private val gameId: Long,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val roundRepository: RoundRepository,
    private val addGamerUseCase: AddGamerUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrepareUiState())
    fun uiState() = _uiState.asStateFlow()

    private var roundId = 0L

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    game = gameRepository.getGame(gameId = gameId),
                    players = playerRepository.getPlayers(gameId)
                )
            }

            roundId = roundRepository.createNewRound(gameId)
        }
    }

    fun deleteRound() {
        viewModelScope.launch {
            roundRepository.deleteRound(roundId)
            roundId = 0L
        }
    }

    fun onClickPlayer(check: Boolean, player: Player, failCallback: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                addGamerUseCase(roundId, check, player)
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(gamer = it)
                }
            }.onFailure {
                failCallback()
                Timber.tag("🔥zero:onClickPlayer").e("$it")
            }
        }

    }

    companion object {
        fun provideFactory(
            prePareViewModelFactory: PrePareViewModelFactory,
            gameId: Long
        ): ViewModelProvider.Factory = viewModelFactory { prePareViewModelFactory.createPrepareViewModel(gameId) }
    }
}
