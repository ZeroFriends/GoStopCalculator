package zero.friends.gostopcalculator.ui.precondition

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import zero.friends.domain.model.Game
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.usecase.rule.AddNewRuleUseCase
import zero.friends.domain.usecase.rule.GetDefaultRuleUseCase
import zero.friends.gostopcalculator.util.TimeUtil
import javax.inject.Inject

data class RuleUiState(
    val currentTime: String = TimeUtil.getCurrentTime(),
    val ruleName: String = "",
    val rules: List<Rule> = emptyList(),
    val enableComplete: Boolean = false,
)

@HiltViewModel
class RuleViewModel @Inject constructor(
    private val getDefaultRuleUseCase: GetDefaultRuleUseCase,
    private val addNewRuleUseCase: AddNewRuleUseCase,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RuleUiState())
    fun getUiState() = _uiState.asStateFlow()

    suspend fun updateRule() {
        _uiState.update {
            val rules = getDefaultRuleUseCase.invoke()
            it.copy(rules = rules)
        }
    }

    fun updateRuleScore(targetRule: Rule) {
        getUiState().value.rules.find { it.name == targetRule.name }?.score = targetRule.score
    }

    suspend fun startGame(): Game {
        addNewRuleUseCase(rules = getUiState().value.rules)
        return requireNotNull(gameRepository.getCurrentGame())
    }

    fun checkButtonState() {
        val verify = getUiState().value.rules.filter { it.isEssential }.any { it.score > 0 }
        _uiState.update {
            it.copy(enableComplete = verify)
        }
    }

}
