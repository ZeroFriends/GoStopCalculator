package zero.friends.gostopcalculator.ui.board.rule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import zero.friends.domain.model.Game
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.RuleRepository

data class RuleLogUiState(
    val game: Game = Game(),
    val rule: List<Rule> = emptyList()
)

class RuleLogViewModel @Inject constructor(
    @Assisted private val gameId: Long,
    private val gameRepository: GameRepository,
    private val ruleRepository: RuleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RuleLogUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val game = gameRepository.getGame(gameId)
            val rule = ruleRepository.getRules(game.id)
            _uiState.update { it.copy(game = game, rule = rule) }
        }
    }
}