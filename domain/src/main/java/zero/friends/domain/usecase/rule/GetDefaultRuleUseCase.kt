package zero.friends.domain.usecase.rule

import zero.friends.domain.model.Rule
import zero.friends.domain.repository.RuleRepository

class GetDefaultRuleUseCase(
    private val ruleRepository: RuleRepository,
) {
    suspend operator fun invoke(playerCount: Int): List<Rule> {
        val canSellShine = playerCount > 3
        val rules = ruleRepository.getDefaultRule().toMutableList()
        val sellShineRule = rules.find { it.name == "광팔기" }

        if (!canSellShine) rules.remove(sellShineRule)
        return rules.toList()
    }
}