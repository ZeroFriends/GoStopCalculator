package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Round

interface RoundRepository {
    fun observeAllRound(gameId: Long): Flow<List<Round>>
    suspend fun createNewRound(gameId: Long): Long
    suspend fun getCurrentRound(): Round?
    suspend fun deleteRound(roundId: Long)

}