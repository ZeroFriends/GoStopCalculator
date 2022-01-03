package zero.friends.gostopcalculator.ui.board.result

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import javax.inject.Inject

data class CalculateUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList()
)

@HiltViewModel
class CalculateViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculateUiState())
    fun uiState() = _uiState.asStateFlow()
}