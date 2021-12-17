package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Optional
import zero.friends.domain.model.Player

interface GamerRepository {
    suspend fun getAllGamer(gameId: Long, playerId: Long): List<Gamer>
    suspend fun getRoundGamers(roundId: Long): List<Gamer>
    fun observeGamers(gameId: Long): Flow<List<Gamer>>
    suspend fun createGamer(gameId: Long, roundId: Long, player: Player)
    suspend fun deleteGamer(roundId: Long, player: Player)
    suspend fun updateOption(id: Long, optional: List<Optional>)
}