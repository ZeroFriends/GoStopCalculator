package zero.friends.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.RuleRepository
import javax.inject.Inject

class AddNewRuleUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val ruleRepository: RuleRepository,
) {
    operator fun invoke(ruleName: String, rules: List<Rule>) {
        CoroutineScope(Dispatchers.IO).launch {
            val gameId = gameRepository.getCurrentGameId()
            ruleRepository.addNewRule(gameId, ruleName, rules)
        }
    }
}