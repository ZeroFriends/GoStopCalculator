package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Round

interface RoundRepository {
    suspend fun observeAllRound(gameId: Long): Flow<List<Round>>

}