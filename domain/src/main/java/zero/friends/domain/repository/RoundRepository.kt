package zero.friends.domain.repository

import zero.friends.domain.model.Round

interface RoundRepository {
    suspend fun getAllRound(gameId: Long): List<Round>
    suspend fun createNewRound(gameId: Long): Long

}