package zero.friends.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import zero.friends.data.entity.GamerEntity.Companion.toGamer
import zero.friends.data.entity.RoundEntity
import zero.friends.data.entity.RoundEntity.Companion.toRound
import zero.friends.data.source.dao.RoundDao
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Round
import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class RoundRepositoryImpl @Inject constructor(private val roundDao: RoundDao) : RoundRepository {

    private var cacheRoundId: Long? = null

    override suspend fun createNewRound(gameId: Long): Long {
        return roundDao.insert(RoundEntity(gameId = gameId)).also { cacheRoundId = it }
    }

    override suspend fun getCurrentRound(): Round? {
        return cacheRoundId?.let { roundId ->
            roundDao.getRound(roundId).toRound()
        }
    }

    override fun observeAllRound(gameId: Long): Flow<List<Round>> {
        return roundDao.observeAllRound(gameId)
            .map { roundEntities ->
                roundEntities.map {
                    it.toRound()
                }
            }
    }

    override fun observeRound(roundId: Long): Flow<List<Gamer>> {
        return roundDao.observeRoundGamers(roundId).map { roundGamers ->
            roundGamers.gamers.map { it.toGamer() }
        }
    }

    override suspend fun deleteRound(roundId: Long) {
        roundDao.deleteRound(roundId)
        cacheRoundId = null
    }
}