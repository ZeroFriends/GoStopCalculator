package zero.friends.data.repository

import zero.friends.data.entity.GamerEntity.Companion.toGamer
import zero.friends.data.source.dao.GamerDao
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GamerRepository

class GamerRepositoryImpl(
    private val gamerDao: GamerDao
) : GamerRepository {
    override suspend fun getAllGamer(gameId: Long, playerId: Long): List<Gamer> {
        return gamerDao.getAllGamer(gameId, playerId).map { it.toGamer() }
    }

}
