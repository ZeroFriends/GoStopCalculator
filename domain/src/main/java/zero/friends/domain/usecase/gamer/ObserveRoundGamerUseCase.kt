package zero.friends.domain.usecase.gamer

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class ObserveRoundGamerUseCase @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val roundRepository: RoundRepository
) {
    suspend operator fun invoke(): Flow<List<Gamer>> {
        val round = requireNotNull(roundRepository.getCurrentRound())
        return gamerRepository.observeRoundGamers(round.id)
    }
}