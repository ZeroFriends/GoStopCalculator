package zero.friends.gostopcalculator.ui.board

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import zero.friends.domain.model.Game
import javax.inject.Inject

data class PrepareUiState(
    val game: Game = Game()
)

@HiltViewModel
class PrepareViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PrepareUiState())
    fun uiState() = _uiState.asStateFlow()
}
