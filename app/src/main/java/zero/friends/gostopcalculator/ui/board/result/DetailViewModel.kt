package zero.friends.gostopcalculator.ui.board.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository

data class DetailUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList()
)

class DetailViewModel @AssistedInject constructor(
    @Assisted private val roundId: Long,
    private val gamerRepository: GamerRepository,
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    fun uiState() = _uiState.asStateFlow()

    @dagger.assisted.AssistedFactory
    fun interface Factory {
        fun create(roundId: Long): DetailViewModel
    }

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    game = requireNotNull(gameRepository.getCurrentGame()),
                    gamers = gamerRepository.getRoundGamers(roundId = roundId)
                )
            }
        }
    }
}