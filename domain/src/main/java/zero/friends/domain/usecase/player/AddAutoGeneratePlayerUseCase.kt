package zero.friends.domain.usecase.player

import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository


class AddAutoGeneratePlayerUseCase(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke() {
        val gameId = requireNotNull(gameRepository.getCurrentGameId())
        val players = playerRepository.getPlayers(gameId)
        val currentCount = players.groupBy { it.gameId }[gameId]?.size ?: 0
        require(currentCount < LIMIT_PLAYER) {
            "플레이어는 10명까지 선택 가능합니다."
        }
        playerRepository.addAutoGeneratePlayer(gameId)
    }

    companion object {
        const val LIMIT_PLAYER = 10
    }

}