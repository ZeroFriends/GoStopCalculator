package zero.friends.domain.usecase

import zero.friends.domain.model.PlayerResult
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.PlayerRepository
import javax.inject.Inject

class GetPlayerListUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val gamerRepository: GamerRepository
) {
    suspend operator fun invoke(gameId: Long): List<PlayerResult> {
        val allPlayer = playerRepository.getPlayers(gameId)
        return allPlayer.map { player ->
            val gamers = gamerRepository.getAllGamer(gameId, player.id)
            val totalAccount = gamers.sumOf { it.account }
            PlayerResult(player.name, totalAccount)
        }
    }
}