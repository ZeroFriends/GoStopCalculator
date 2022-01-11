package zero.friends.domain.usecase.round

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject

class ObserveRoundListUseCase @Inject constructor(
    private val gamerRepository: GamerRepository
) {
    operator fun invoke(gameId: Long): Flow<Map<Long, List<Gamer>>> {
        return gamerRepository.observeGamers(gameId).map { gamers ->
            gamers
                .filter { it.gameId == gameId }
                .groupBy { it.roundId }
        }
    }
}
