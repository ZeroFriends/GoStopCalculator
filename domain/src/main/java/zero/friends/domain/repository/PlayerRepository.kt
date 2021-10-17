package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Player

interface PlayerRepository {
    suspend fun addAutoGeneratePlayer(gameId: Long, playerStringFormat: String)
    suspend fun observePlayer(): Flow<List<Player>>
    suspend fun deletePlayer(player: Player)
    suspend fun getPlayers(gameId: Long): List<Player>
    suspend fun isExistPlayer(name: String): Boolean
}