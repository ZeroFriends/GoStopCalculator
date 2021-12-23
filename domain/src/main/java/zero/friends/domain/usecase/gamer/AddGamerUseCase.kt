package zero.friends.domain.usecase.gamer

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Player
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject

class AddGamerUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val gamerRepository: GamerRepository
) {
    suspend operator fun invoke(roundId: Long, check: Boolean, player: Player): List<Gamer> {
        val currentGameId = requireNotNull(gameRepository.getCurrentGameId())
        val currentGamer = gamerRepository.getRoundGamers(roundId)
        if (check) {
            if (currentGamer.size < 4) {
                gamerRepository.createGamer(currentGameId, roundId, player)
            } else {
                throw IllegalStateException("5인이상 게임을 진행할 수 없습니다.")
            }
        } else {
            gamerRepository.deleteGamer(roundId, player)
        }

        return gamerRepository.getRoundGamers(roundId)
    }
}