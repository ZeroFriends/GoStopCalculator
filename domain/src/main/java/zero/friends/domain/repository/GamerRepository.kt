package zero.friends.domain.repository

import zero.friends.domain.model.Gamer

interface GamerRepository {
    suspend fun getAllGamer(gameId: Long, playerId: Long): List<Gamer>
    suspend fun getRoundGamers(roundId: Long): List<Gamer>
}