package zero.friends.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import zero.friends.domain.model.PlayerResult
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.PlayerRepository
import javax.inject.Inject

class ObservePlayerResultsUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val gamerRepository: GamerRepository
) {
    operator fun invoke(gameId: Long): Flow<List<PlayerResult>> {
        return gamerRepository.observeGamers(gameId).flatMapLatest { gamers ->
            val playerMap = gamers.groupBy { it.playerId }
            val playerResults = playerRepository.getPlayers(gameId).map { player ->
                val totalAccount = playerMap[player.id]?.sumOf { it.account } ?: 0
                PlayerResult(player.name, totalAccount)
            }
            flowOf(playerResults)
        }
    }
}