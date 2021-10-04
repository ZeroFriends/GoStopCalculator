package zero.friends.gostopcalculator.ui.precondition

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.model.Player
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


data class PlayerUiState(
    val players: List<Player> = emptyList(),
    val currentTime: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis()),
)

@HiltViewModel
class PlayerViewModel @Inject constructor(application: Application, savedStateHandle: SavedStateHandle) :
    AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val applicationContext = application.applicationContext

    private val _uiState = MutableStateFlow(PlayerUiState())
    fun getUiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
//            _uiState.update {
//
//            }
        }
    }

    fun addPlayer() {
        viewModelScope.launch {
            _uiState.update {
                val id = it.players.size + 1
                val newPlayer = Player(id.toString(),
                    String.format(applicationContext.getString(R.string.new_player), id))

                it.copy(players = it.players + listOf(newPlayer))
            }
        }
    }

}
