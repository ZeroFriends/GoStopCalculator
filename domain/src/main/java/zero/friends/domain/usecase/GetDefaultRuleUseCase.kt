package zero.friends.domain.usecase

import zero.friends.domain.model.Rule
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RuleRepository

class GetDefaultRuleUseCase(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val ruleRepository: RuleRepository,
) {
    suspend operator fun invoke(): List<Rule> {
        val gameId = gameRepository.getCurrentGameId()

        val canSellShine = playerRepository.getPlayers(gameId).size > 3
        val rules = ruleRepository.getDefaultRule().toMutableList()
        val sellShineRule = rules.find { it.title == "광팔기" }

        if (!canSellShine) rules.remove(sellShineRule)
        return rules.toList()
    }
}