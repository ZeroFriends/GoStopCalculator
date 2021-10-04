package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Player

interface PlayerRepository {
    suspend fun addPlayer(newPlayer: Player)
    suspend fun observePlayer(): Flow<List<Player>>
    suspend fun deletePlayer(player: Player)
}