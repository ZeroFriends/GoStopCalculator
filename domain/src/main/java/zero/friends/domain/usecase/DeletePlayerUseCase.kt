package zero.friends.domain.usecase

import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository

class DeletePlayerUseCase(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(player: Player) {
        val gameId = gameRepository.getCurrentGameId()
        playerRepository.deletePlayer(gameId, player)
    }
}