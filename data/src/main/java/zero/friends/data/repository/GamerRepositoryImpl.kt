package zero.friends.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import zero.friends.data.entity.GamerEntity
import zero.friends.data.entity.GamerEntity.Companion.toGamer
import zero.friends.data.source.dao.GamerDao
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Optional
import zero.friends.domain.model.Player
import zero.friends.domain.repository.GamerRepository
import zero.friends.shared.IoDispatcher
import javax.inject.Inject

class GamerRepositoryImpl @Inject constructor(
    private val gamerDao: GamerDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : GamerRepository {
    override suspend fun getAllGamer(gameId: Long, playerId: Long): List<Gamer> {
        return gamerDao.getAllGamer(gameId, playerId).map { it.toGamer() }
    }

    override suspend fun getRoundGamers(roundId: Long): List<Gamer> {
        return gamerDao.getRoundGamers(roundId).map { it.toGamer() }
    }

    override fun observeGamers(gameId: Long): Flow<List<Gamer>> {
        return gamerDao.observeGamers(gameId).map { entities ->
            entities.map { it.toGamer() }
        }
    }

    override suspend fun createGamer(gameId: Long, roundId: Long, player: Player) {
        gamerDao.insert(
            GamerEntity(
                roundId = roundId,
                name = player.name,
                playerId = player.id,
                gameId = gameId
            )
        )
    }

    override suspend fun deleteGamer(roundId: Long, player: Player) {
        gamerDao.deleteGamer(roundId, player.id)
    }

    override suspend fun updateOption(id: Long, optional: List<Optional>) {
        withContext(dispatcher) {
            gamerDao.updateOption(id, optional.joinToString(","))
        }
    }

}
