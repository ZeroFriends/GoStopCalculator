package zero.friends.domain.usecase.player

import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository

class DeletePlayerUseCase(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(player: Player) {
        val gameId = requireNotNull(gameRepository.getCurrentGameId())
        playerRepository.deletePlayer(gameId, player)
    }
}