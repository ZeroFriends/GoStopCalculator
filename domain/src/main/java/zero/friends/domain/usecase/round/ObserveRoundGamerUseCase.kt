package zero.friends.domain.usecase.round

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class ObserveRoundGamerUseCase @Inject constructor(
    private val roundRepository: RoundRepository,

    ) {
    suspend operator fun invoke(): Flow<List<Gamer>> {
        val round = requireNotNull(roundRepository.getCurrentRound())
        return roundRepository.observeRound(roundId = round.id)
    }
}