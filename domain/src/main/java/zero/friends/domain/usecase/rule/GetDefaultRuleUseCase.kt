package zero.friends.domain.usecase.rule

import zero.friends.domain.model.Rule
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.util.Const
import javax.inject.Inject

class GetDefaultRuleUseCase @Inject constructor(
    private val ruleRepository: RuleRepository,
) {
    suspend operator fun invoke(playerCount: Int): List<Rule> {
        val canSellShine = playerCount > 3
        val rules = ruleRepository.getDefaultRule().toMutableList()
        val sellShineRule = rules.find { it.name == Const.Rule.Sell }

        if (!canSellShine) rules.remove(sellShineRule)
        return rules.toList()
    }
}
