package zero.friends.domain.usecase

import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository


class AddAutoGeneratePlayerUseCase(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke() {
        val gameId = requireNotNull(gameRepository.getCurrentGameId())
        playerRepository.addAutoGeneratePlayer(gameId)
    }

}