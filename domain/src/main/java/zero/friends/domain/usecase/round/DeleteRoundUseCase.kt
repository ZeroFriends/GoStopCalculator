package zero.friends.domain.usecase.round

import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class DeleteRoundUseCase @Inject constructor(
    private val roundRepository: RoundRepository
) {
    suspend operator fun invoke(roundId: Long? = null) {
        val roundId = roundId ?: requireNotNull(roundRepository.getCurrentRound()).id
        roundRepository.deleteRound(roundId = roundId)
    }
}