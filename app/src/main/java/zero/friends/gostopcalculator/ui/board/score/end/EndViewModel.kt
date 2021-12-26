package zero.friends.gostopcalculator.ui.board.score.end

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import zero.friends.domain.model.PlayerResult
import zero.friends.domain.usecase.ObservePlayerResultsUseCase
import javax.inject.Inject

data class EndUiState(
    val players: List<PlayerResult> = emptyList()
)

@HiltViewModel
class EndViewModel @Inject constructor(
    private val observePlayerResultsUseCase: ObservePlayerResultsUseCase
) : ViewModel() {
    private val _endUiState = MutableStateFlow(EndUiState())
    fun endUiState() = _endUiState.asStateFlow()

    init {
        observePlayerResultsUseCase()
            .onEach { playerResults ->
                //todo 이전결과 + 총결과 combine 을 잘 해보자...
                _endUiState.update { it.copy(players = playerResults) }
            }.launchIn(viewModelScope)
    }

}
