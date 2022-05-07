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
    val gamers: List<Gamer> = emptyList()
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
            _uiState.update {
                it.copy(
                    game = gameRepository.getGame(requireNotNull(savedStateHandle[Const.GameId])),
                    gamers = gamerRepository.getRoundGamers(roundId = requireNotNull(savedStateHandle.get(Const.RoundId)))
                )
            }
        }
    }
}