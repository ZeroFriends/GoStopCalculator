package zero.friends.domain.usecase.rule

import zero.friends.domain.model.Rule
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.RuleRepository
import javax.inject.Inject

class AddNewRuleUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val ruleRepository: RuleRepository,
) {
    suspend operator fun invoke(rules: List<Rule>) {
        val gameId = requireNotNull(gameRepository.getCurrentGameId())
        ruleRepository.addNewRule(gameId, rules)
    }
}