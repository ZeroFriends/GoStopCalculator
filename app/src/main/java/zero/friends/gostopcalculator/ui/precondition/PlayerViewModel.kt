package zero.friends.gostopcalculator.ui.precondition

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.usecase.AddAutoGeneratePlayerUseCase
import zero.friends.domain.usecase.DeletePlayerUseCase
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
    private val gameRepository: GameRepository,
    private val addAutoGeneratePlayerUseCase: AddAutoGeneratePlayerUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
    private val playerRepository: PlayerRepository,
) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val applicationContext = application.applicationContext

    private val _uiState = MutableStateFlow(PlayerUiState())
    fun getUiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.newGame(_uiState.value.currentTime, _uiState.value.currentTime)
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
            kotlin.runCatching {
                addAutoGeneratePlayerUseCase()
            }.onFailure {
                Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch {
            deletePlayerUseCase.invoke(player)
        }
    }

    fun clearGame() {
        viewModelScope.launch {
            gameRepository.clearGame()
        }
    }

}
