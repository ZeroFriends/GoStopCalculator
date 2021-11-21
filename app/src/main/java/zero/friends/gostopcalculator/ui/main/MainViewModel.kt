package zero.friends.gostopcalculator.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.repository.GameRepository
import javax.inject.Inject

data class MainUiState(
    val games: List<Game> = emptyList()
)

@HiltViewModel
class MainViewModel @Inject constructor(private val gameRepository: GameRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    fun getUiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                val gameList = gameRepository.getGameList()
                it.copy(games = gameList)
            }
        }
    }

}