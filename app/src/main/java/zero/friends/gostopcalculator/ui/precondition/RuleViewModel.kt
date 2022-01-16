package zero.friends.gostopcalculator.ui.precondition

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.usecase.rule.AddNewRuleUseCase
import zero.friends.domain.usecase.rule.GetDefaultRuleUseCase
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.util.TimeUtil
import zero.friends.gostopcalculator.util.separateComma
import javax.inject.Inject

data class RuleUiState(
    val currentTime: String = TimeUtil.getCurrentTime(),
    val ruleName: String = "",
    val rules: List<Rule> = emptyList(),
    val enableComplete: Boolean = false,
)

@HiltViewModel
class RuleViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val getDefaultRuleUseCase: GetDefaultRuleUseCase,
    private val addNewRuleUseCase: AddNewRuleUseCase,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RuleUiState())
    fun getUiState() = _uiState.asStateFlow()

    private val _toast = MutableSharedFlow<String?>()
    fun toast() = _toast.asSharedFlow()

    suspend fun updateRule() {
        _uiState.update {
            val rules = getDefaultRuleUseCase.invoke()
            it.copy(rules = rules)
        }
    }

    fun updateRuleScore(targetRule: Rule, score: Long) {
        viewModelScope.launch {
            runCatching {
                require(score <= MAXIMUM_SCORE) {
                    context.getString(R.string.over_score_alert, MAXIMUM_SCORE.separateComma())
                }
            }.onFailure {
                _toast.emit(it.message)
            }.onSuccess {
                _uiState.update { state ->
                    val newRule = state.rules.map {
                        if (it.name == targetRule.name) {
                            it.copy(score = score.toInt())
                        } else {
                            it
                        }
                    }
                    state.copy(rules = newRule)
                }
            }
        }
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

    companion object {
        private const val MAXIMUM_SCORE = 1_000_000
    }

}
