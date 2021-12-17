package zero.friends.domain.usecase

import zero.friends.domain.model.Rule
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.RuleRepository
import javax.inject.Inject

class AddNewRuleUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val ruleRepository: RuleRepository,
) {
    suspend operator fun invoke(ruleName: String, rules: List<Rule>) {
        val gameId = requireNotNull(gameRepository.getCurrentGameId())
        ruleRepository.addNewRule(gameId, ruleName, rules)
    }
}