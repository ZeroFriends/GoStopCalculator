package zero.friends.gostopcalculator.ui.board.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository

data class DetailUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList(),
    val roundId: Long = 0L
)

@Inject
class DetailViewModel(
    private val gamerRepository: GamerRepository,
    private val gameRepository: GameRepository,
    @Assisted private val gameId: Long,
    @Assisted private val roundId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    game = gameRepository.getGame(gameId),
                    gamers = gamerRepository.getRoundGamers(roundId),
                    roundId = roundId
                )
            }
        }
    }
}