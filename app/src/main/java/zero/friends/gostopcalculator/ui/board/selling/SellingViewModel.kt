package zero.friends.gostopcalculator.ui.board.selling

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import javax.inject.Inject

data class SellingUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList()
)


@HiltViewModel
class SellingViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow(SellingUiState())
    fun uiState() = _uiState.asStateFlow()
}
