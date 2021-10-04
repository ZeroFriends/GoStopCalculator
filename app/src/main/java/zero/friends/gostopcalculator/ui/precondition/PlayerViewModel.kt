package zero.friends.gostopcalculator.ui.precondition

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Player
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.usecase.PlayerUseCase
import zero.friends.gostopcalculator.R
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


data class PlayerUiState(
    val players: List<Player> = emptyList(),
    val currentTime: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis()),
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val playerUseCase: PlayerUseCase,//do not use...(일단은)
    private val playerRepository: PlayerRepository,
) :
    AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val applicationContext = application.applicationContext

    private val _uiState = MutableStateFlow(PlayerUiState())
    fun getUiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            playerRepository.observePlayer()
                .collect { players ->
                    _uiState.update {
                        it.copy(players = players)
                    }
                }

        }

    }

    fun addPlayer() {
        viewModelScope.launch {
            val id = getUiState().value.players.size + 1
            val newPlayer = Player(id.toString(),
                String.format(applicationContext.getString(R.string.new_player), id))
            playerRepository.addPlayer(newPlayer)
        }
    }

}
