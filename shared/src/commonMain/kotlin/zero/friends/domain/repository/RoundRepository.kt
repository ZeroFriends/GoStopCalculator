package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Round

interface RoundRepository {
    fun observeAllRound(gameId: Long): Flow<List<Round>>
    fun observeRound(roundId: Long): Flow<List<Gamer>>
    suspend fun createNewRound(gameId: Long): Long
    suspend fun deleteRound(roundId: Long)
    suspend fun getRound(roundId: Long): Round?
    fun observeRoundMeta(roundId: Long): Flow<Round?>
    suspend fun updateRoundName(roundId: Long, name: String)

}