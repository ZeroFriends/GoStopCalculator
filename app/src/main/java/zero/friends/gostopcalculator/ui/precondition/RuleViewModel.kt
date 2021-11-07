package zero.friends.gostopcalculator.ui.precondition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Rule
import zero.friends.domain.usecase.AddNewRuleUseCase
import zero.friends.domain.usecase.GetDefaultRuleUseCase
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class RuleUiState(
    val currentTime: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis()),
    val ruleName: String = "",
    val rules: List<Rule> = emptyList(),
    val enableComplete: Boolean = false,
)

@HiltViewModel
class RuleViewModel @Inject constructor(
    private val getDefaultRuleUseCase: GetDefaultRuleUseCase,
    private val addNewRuleUseCase: AddNewRuleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RuleUiState())
    fun getUiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                val rules = getDefaultRuleUseCase.invoke()
                it.copy(rules = rules)
            }
        }
    }

    fun updateRuleScore(targetRule: Rule) {
        getUiState().value.rules.find { it.title == targetRule.title }?.score = targetRule.score
    }

    fun startGame(ruleName: String) {
        addNewRuleUseCase(ruleName = ruleName, rules = getUiState().value.rules)
    }

    fun checkButtonState() {
        val verify = getUiState().value.rules.filter { it.isEssential }.any { it.score > 0 }
        _uiState.update {
            it.copy(enableComplete = verify)
        }
    }

}
