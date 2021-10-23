package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Player

interface PlayerRepository {
    suspend fun addAutoGeneratePlayer(gameId: Long)
    suspend fun observePlayer(gameId: Long): Flow<List<Player>>
    suspend fun deletePlayer(gameId: Long, player: Player)
    suspend fun getPlayers(gameId: Long): List<Player>
    suspend fun editPlayer(gameId: Long, player: Player, editPlayer: Player)
    suspend fun isExistPlayer(gameId: Long, name: String): Boolean
}