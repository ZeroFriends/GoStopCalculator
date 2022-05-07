package zero.friends.domain.usecase

import zero.friends.domain.model.Player
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RuleRepository
import javax.inject.Inject

class StartNewGameUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    private val ruleRepository: RuleRepository,
) {
    suspend operator fun invoke(
        gameName: String,
        createdAt: String,
        players: List<Player>,
        rules: List<Rule>
    ): Long {
        val gameId = gameRepository.newGame(gameName, createdAt = createdAt)
        playerRepository.addPlayers(gameId, players)
        ruleRepository.addNewRule(gameId, rules)
        return gameId
    }
}