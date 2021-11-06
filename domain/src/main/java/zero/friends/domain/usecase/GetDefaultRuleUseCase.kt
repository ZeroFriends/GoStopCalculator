package zero.friends.domain.usecase

import zero.friends.domain.model.Rule
import zero.friends.domain.repository.RuleRepository

class GetDefaultRuleUseCase(private val ruleRepository: RuleRepository) {
    suspend operator fun invoke() :List<Rule>{
        return ruleRepository.getDefaultRule()
    }
}