package zero.friends.gostopcalculator.ui.precondition

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import zero.friends.gostopcalculator.model.Player
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


data class PlayerUiState(
    val players: List<Player> = emptyList(),
    val currentTime: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis()),
)

@HiltViewModel
class PlayerViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerUiState())
    fun getUiState() = _uiState.asStateFlow()

    init {
//        _uiState.update {
//            it.copy(players = listOf(Player("zero"), Player("khan")))
//        }
    }

}
