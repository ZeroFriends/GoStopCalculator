package zero.friends.gostopcalculator.ui.board.prepare

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.usecase.round.CreateNewRoundUseCase
import zero.friends.domain.util.Const
import javax.inject.Inject

data class PrepareUiState(
    val game: Game = Game(),
    val players: List<Player> = emptyList(),
    val selectedPlayers: List<Player> = emptyList()
)

@HiltViewModel
class PrepareViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val createNewRound: CreateNewRoundUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrepareUiState())
    fun uiState() = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            val game = gameRepository.getGame(requireNotNull(savedStateHandle.get(Const.GameId)))
            _uiState.update {
                it.copy(
                    game = game,
                    players = playerRepository.getPlayers(gameId = game.id)
                )
            }
        }
    }

    fun createNewRound(onCreate: (roundId: Long) -> Unit) {
        viewModelScope.launch {
            val roundId = createNewRound.invoke(uiState().value.game.id, uiState().value.selectedPlayers)
            onCreate(roundId)
        }
    }

    fun onClickPlayer(check: Boolean, player: Player, overPlayerCount: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                if (check) {
                    if (uiState().value.selectedPlayers.size < 4) {
                        _uiState.update {
                            it.copy(selectedPlayers = it.selectedPlayers + player)
                        }
                    } else {
                        overPlayerCount()
                    }
                } else {
                    _uiState.update {
                        it.copy(selectedPlayers = it.selectedPlayers - player)
                    }
                }
            }
        }

    }

}
