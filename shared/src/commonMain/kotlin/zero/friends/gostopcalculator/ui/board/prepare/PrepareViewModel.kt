package zero.friends.gostopcalculator.ui.board.prepare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import zero.friends.domain.model.Game
import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.usecase.round.CreateNewRoundUseCase

data class PrepareUiState(
    val game: Game = Game(),
    val players: List<Player> = emptyList(),
    val selectedPlayers: List<Player> = emptyList(),
    val isSubmitting: Boolean = false,
)

class PrepareViewModel @Inject constructor(
    @Assisted private val gameId: Long,
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val createNewRound: CreateNewRoundUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrepareUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val game = gameRepository.getGame(gameId)
                val players = playerRepository.getPlayers(gameId = game.id)
                _uiState.update {
                    it.copy(
                        game = game,
                        players = players,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Keep default empty state if initial load fails cleanly instead of crashing scope.
            }
        }
    }

    fun createNewRound(
        onCreate: (gameId: Long, roundId: Long) -> Unit,
        onError: (Throwable) -> Unit = {},
    ) {
        val currentState = _uiState.value
        if (currentState.isSubmitting || currentState.selectedPlayers.size < 2) return

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                val roundId = createNewRound.invoke(currentState.game.id, currentState.selectedPlayers)
                ensureActive()
                onCreate(currentState.game.id, roundId)
            } catch (e: CancellationException) {
                _uiState.update { it.copy(isSubmitting = false) }
                throw e
            } catch (e: Throwable) {
                _uiState.update { it.copy(isSubmitting = false) }
                onError(e)
            }
        }
    }

    fun onClickPlayer(check: Boolean, player: Player, overPlayerCount: () -> Unit = {}) {
        var reachedPlayerLimit = false
        _uiState.update { state ->
            if (state.isSubmitting) return@update state
            if (check) {
                if (state.selectedPlayers.any { it.id == player.id }) {
                    state
                } else if (state.selectedPlayers.size < 4) {
                    state.copy(selectedPlayers = state.selectedPlayers + player)
                } else {
                    reachedPlayerLimit = true
                    state
                }
            } else {
                state.copy(selectedPlayers = state.selectedPlayers.filterNot { it.id == player.id })
            }
        }
        if (reachedPlayerLimit) {
            overPlayerCount()
        }
    }
}
