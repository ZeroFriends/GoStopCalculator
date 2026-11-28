package zero.friends.gostopcalculator.ui.board.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.PlayerResult
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.usecase.ObservePlayerResultsUseCase
import zero.friends.domain.usecase.round.ObserveRoundListUseCase

data class BoardUiState(
    val game: Game = Game(),
    val gameHistories: Map<Long, List<Gamer>> = emptyMap(),
    val roundNames: Map<Long, String> = emptyMap(),
    val playerResults: List<PlayerResult> = emptyList(),
)

class BoardViewModel @Inject constructor(
    @Assisted private val gameId: Long,
    private val gameRepository: GameRepository,
    private val observePlayerResultsUseCase: ObservePlayerResultsUseCase,
    private val observeRoundListUseCase: ObserveRoundListUseCase,
    private val roundRepository: RoundRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoardUiState(Game(gameId)))
    fun getUiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observePlayerResultsUseCase(gameId).onEach { playerList ->
                _uiState.update { it.copy(playerResults = playerList) }
            }.launchIn(this)

            combine(
                observeRoundListUseCase.invoke(gameId),
                roundRepository.observeAllRound(gameId),
            ) { roundLists, rounds ->
                roundLists to rounds.associate { it.id to it.name }
            }
                .onEach { (roundLists, names) ->
                    _uiState.update { state ->
                        state.copy(
                            gameHistories = roundLists,
                            roundNames = names,
                        )
                    }
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

    fun deleteRound(roundId: Long) {
        viewModelScope.launch {
            roundRepository.deleteRound(roundId)
        }
    }

    fun renameRound(roundId: Long, name: String) {
        val normalized = name
            .replace("\r", "")
            .replace("\n", " ")
            .trim()
            .take(MAX_ROUND_NAME_LENGTH)
            .trimEnd()
        viewModelScope.launch {
            roundRepository.updateRoundName(roundId, normalized)
        }
    }

    companion object {
        const val MAX_ROUND_NAME_LENGTH = 20
    }
}
