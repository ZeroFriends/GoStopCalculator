package zero.friends.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import zero.friends.data.entity.RoundEntity.Companion.toRound
import zero.friends.data.source.dao.RoundDao
import zero.friends.domain.model.Round
import zero.friends.domain.repository.RoundRepository
import javax.inject.Inject

class RoundRepositoryImpl @Inject constructor(private val roundDao: RoundDao) : RoundRepository {
    override suspend fun observeAllRound(gameId: Long): Flow<List<Round>> {
        return roundDao.observeAllRound(gameId).map { roundEntity ->
            roundEntity.map { it.toRound() }
        }
    }
}