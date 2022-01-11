package zero.friends.gostopcalculator.ui.board.prepare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.usecase.gamer.AddGamerUseCase
import javax.inject.Inject

data class PrepareUiState(
    val game: Game = Game(),
    val players: List<Player> = emptyList(),
    val gamer: List<Gamer> = emptyList()
)

@HiltViewModel
class PrepareViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val roundRepository: RoundRepository,
    private val addGamerUseCase: AddGamerUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrepareUiState())
    fun uiState() = _uiState.asStateFlow()

    private val _roundId = MutableStateFlow(0L)

    init {
        viewModelScope.launch {
            val game = requireNotNull(gameRepository.getCurrentGame())
            _uiState.update {
                it.copy(
                    game = game,
                    players = playerRepository.getPlayers(gameId = game.id)
                )
            }

            _roundId.update {
                roundRepository.createNewRound(game.id)
            }
        }
    }

    fun deleteRound() {
        viewModelScope.launch {
            roundRepository.deleteRound(_roundId.value)
            _roundId.update { 0L }
        }
    }

    fun onClickPlayer(check: Boolean, player: Player, failCallback: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                addGamerUseCase(_roundId.value, check, player)
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(gamer = it)
                }
            }.onFailure {
                failCallback()
            }
        }

    }

}
