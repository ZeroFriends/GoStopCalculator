package zero.friends.gostopcalculator.ui.board.rule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.RuleRepository
import javax.inject.Inject

data class RuleLogUiState(
    val game: Game = Game(),
    val rule: List<Rule> = emptyList()
)

@HiltViewModel
class RuleLogViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val ruleRepository: RuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RuleLogUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val game = requireNotNull(gameRepository.getCurrentGame())
            val rule = ruleRepository.getRules(game.id)
            _uiState.update { it.copy(game = game, rule = rule) }
        }
    }
}