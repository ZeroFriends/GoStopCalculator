package zero.friends.gostopcalculator.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.preference.AppPreference
import zero.friends.domain.repository.GameRepository

import me.tatarka.inject.annotations.Inject

data class HistoryUiState(
    val games: List<Game> = emptyList(),
    val isFirstStart: Boolean = false
)

@Inject
class HistoryViewModel(
    private val gameRepository: GameRepository,
    appPreference: AppPreference
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        HistoryUiState(
            isFirstStart = appPreference.isFistStart()
        )
    )

    fun getUiState() = _uiState.asStateFlow()

    init {
        appPreference.setFirstStart()
        gameRepository.observeGameList().onEach { gameList ->
            _uiState.update {
                it.copy(games = gameList)
            }
        }.launchIn(viewModelScope)
    }

    fun deleteGame(gameId: Long) {
        viewModelScope.launch {
            gameRepository.deleteGame(gameId)
        }
    }
}