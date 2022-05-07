package zero.friends.domain.usecase.round

import zero.friends.domain.model.Player
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class CreateNewRoundUseCase @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val roundRepository: RoundRepository
) {
    suspend operator fun invoke(
        gameId: Long,
        selectedPlayers: List<Player>
    ): Long {
        val roundId = roundRepository.createNewRound(gameId)
        selectedPlayers.forEach { player ->
            gamerRepository.createGamer(gameId, roundId, player)
        }
        return roundId
    }
}