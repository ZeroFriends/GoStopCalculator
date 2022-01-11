package zero.friends.domain.usecase.gamer

import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class GetRoundGamerUseCase @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val roundRepository: RoundRepository
) {
    suspend operator fun invoke(): List<Gamer> {
        val round = requireNotNull(roundRepository.getCurrentRound())
        return gamerRepository.getRoundGamers(round.id)
    }
}