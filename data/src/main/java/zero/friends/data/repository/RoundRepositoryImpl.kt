package zero.friends.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import zero.friends.data.entity.GamerEntity.Companion.toGamer
import zero.friends.data.entity.RoundEntity
import zero.friends.data.entity.RoundEntity.Companion.toRound
import zero.friends.data.source.dao.RoundDao
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Round
import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class RoundRepositoryImpl @Inject constructor(private val roundDao: RoundDao) : RoundRepository {

    override suspend fun createNewRound(gameId: Long): Long {
        return roundDao.insert(RoundEntity(gameId = gameId))
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
        return roundDao.observeRoundGamers(roundId).mapNotNull { roundGamers ->
            roundGamers?.gamers?.map { it.toGamer() }
        }
    }

    override suspend fun deleteRound(roundId: Long) {
        roundDao.deleteRound(roundId)
    }
}