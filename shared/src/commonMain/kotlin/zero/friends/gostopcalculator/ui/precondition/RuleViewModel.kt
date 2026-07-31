package zero.friends.gostopcalculator.ui.precondition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import zero.friends.domain.model.Player
import zero.friends.domain.model.Rule
import zero.friends.domain.usecase.StartNewGameUseCase
import zero.friends.domain.usecase.rule.GetDefaultRuleUseCase
import zero.friends.domain.util.Const
import zero.friends.gostopcalculator.util.TimeUtil
import zero.friends.gostopcalculator.util.separateComma

data class RuleUiState(
    val currentTime: String = TimeUtil.getCurrentTime(),
    val ruleName: String = "",
    val rules: List<Rule> = emptyList(),
    val enableComplete: Boolean = false,
)

class RuleViewModel @Inject constructor(
    @Assisted private val players: List<Player>,
    @Assisted private val gameName: String,
    private val getDefaultRuleUseCase: GetDefaultRuleUseCase,
    private val startNewGame: StartNewGameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RuleUiState())
    fun getUiState() = _uiState.asStateFlow()

    private val _toast = MutableSharedFlow<String?>()
    fun toast() = _toast.asSharedFlow()

    init {
        viewModelScope.launch {
            val rules = getDefaultRuleUseCase(playerCount = players.size)
            val verify = rules.filter { it.isEssential }.any { it.score > 0 }
            _uiState.update {
                it.copy(rules = rules, enableComplete = verify)
            }
        }
    }

    fun updateRuleScore(targetRule: Rule, score: Long) {
        viewModelScope.launch {
            runCatching {
                require(score <= MAXIMUM_SCORE) {
                    "${MAXIMUM_SCORE.separateComma()}점을 초과할 수 없습니다."
                }
            }.onFailure {
                _toast.emit(it.message)
            }.onSuccess {
                _uiState.update { state ->
                    val updatedRules = state.rules.map { rule ->
                        when {
                            rule.name == targetRule.name -> rule.copy(score = score.toInt())
                            targetRule.name == Const.Rule.Score && rule.name == Const.Rule.FirstDdadak -> rule.copy(score = (score * 3).toInt())
                            else -> rule
                        }
                    }
                    val verify = updatedRules.filter { it.isEssential }.any { it.score > 0 }
                    state.copy(rules = updatedRules, enableComplete = verify)
                }
            }
        }
    }

    suspend fun startGame(): Long {
        return startNewGame.invoke(
            gameName = gameName,
            createdAt = TimeUtil.getCurrentTime(),
            players = players,
            rules = getUiState().value.rules
        )
    }

    companion object {
        private const val MAXIMUM_SCORE = 1_000_000
    }
}
