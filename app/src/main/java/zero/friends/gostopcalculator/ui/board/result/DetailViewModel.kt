package zero.friends.gostopcalculator.ui.board.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.util.Const
import javax.inject.Inject

data class DetailUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList(),
    val roundId: Long = 0L
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val gameRepository: GameRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val gameId = requireNotNull(savedStateHandle.get<Long>(Const.GameId))
            val roundId = requireNotNull(savedStateHandle.get<Long>(Const.RoundId))
            
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