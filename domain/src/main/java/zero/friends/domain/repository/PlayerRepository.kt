package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Player

interface PlayerRepository {
    suspend fun addPlayer(gameId: Long, newPlayer: Player)
    suspend fun observePlayer(): Flow<List<Player>>
    suspend fun deletePlayer(player: Player)
}