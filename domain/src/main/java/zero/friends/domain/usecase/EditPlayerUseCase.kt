package zero.friends.domain.usecase

import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository

class EditPlayerUseCase(private val gameRepository: GameRepository, private val playerRepository: PlayerRepository) {
    suspend operator fun invoke(originalPlayer: Player, editPlayer: Player) {
        val gameId = requireNotNull(gameRepository.getCurrentGameId())
        val isExist = playerRepository.isExistPlayer(gameId, editPlayer.name)
        val isSame = originalPlayer == editPlayer
        if (isExist && !isSame) throw IllegalStateException("중복된 이름이 존재합니다.")
        playerRepository.editPlayer(gameId, originalPlayer, editPlayer)
    }
}