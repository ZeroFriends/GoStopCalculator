package zero.friends.domain.usecase

import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class GetRoundListUseCase @Inject constructor(
    private val roundRepository: RoundRepository,
    private val gamerRepository: GamerRepository
) {
    private val roundMap = mutableMapOf<Long, List<Gamer>>()
    suspend operator fun invoke(gameId: Long): Map<Long, List<Gamer>> {
        val allRound = roundRepository.getAllRound(gameId)
        allRound.forEach {
            val gamers = gamerRepository.getRoundGamers(it.id)
            roundMap[it.id] = gamers
        }
        return roundMap
    }
}
