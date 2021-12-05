package zero.friends.domain.usecase

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Round
import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class GetRoundListUseCase @Inject constructor(
    private val roundRepository: RoundRepository
) {
    suspend operator fun invoke(gameId: Long): Flow<List<Round>> {
        return roundRepository.observeAllRound(gameId)
    }
}
