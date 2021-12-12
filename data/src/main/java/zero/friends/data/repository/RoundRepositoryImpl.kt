package zero.friends.data.repository

import zero.friends.data.entity.RoundEntity
import zero.friends.data.entity.RoundEntity.Companion.toRound
import zero.friends.data.source.dao.RoundDao
import zero.friends.domain.model.Round
import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class RoundRepositoryImpl @Inject constructor(private val roundDao: RoundDao) : RoundRepository {
    override suspend fun getAllRound(gameId: Long): List<Round> {
        return roundDao.getAllRound(gameId).map { it.toRound() }
    }

    override suspend fun createNewRound(gameId: Long): Long {
        return roundDao.insert(RoundEntity(gameId = gameId))
    }
}