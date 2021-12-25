package zero.friends.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import zero.friends.data.entity.GamerEntity
import zero.friends.data.entity.GamerEntity.Companion.toGamer
import zero.friends.data.source.dao.GamerDao
import zero.friends.domain.model.*
import zero.friends.domain.repository.GamerRepository
import zero.friends.shared.IoDispatcher
import javax.inject.Inject
import kotlin.reflect.KClass

class GamerRepositoryImpl @Inject constructor(
    private val gamerDao: GamerDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : GamerRepository {
    override suspend fun getGamer(gamerId: Long): Gamer {
        return gamerDao.getGamer(gamerId).toGamer()
    }

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

    override suspend fun updateOption(id: Long, options: List<Option>) {
        withContext(dispatcher) {
            options.groupBy { it::class }
                .forEach { map ->
                    when (map.key) {
                        WinnerOption::class -> {
                            gamerDao.updateWinnerOption(id, map.value.joinToString(","))
                        }
                        SellerOption::class -> {
                            gamerDao.updateSellerOption(id, map.value.joinToString(","))
                        }
                        ScoreOption::class -> {
                            gamerDao.updateScoreOption(id, map.value.joinToString(","))
                        }
                        LoserOption::class -> {
                            gamerDao.updateLoserOption(id, map.value.joinToString(","))
                        }
                    }
                }
        }
    }

    override suspend fun clearOption(id: Long, optionClass: KClass<out ScoreOption>) {
        withContext(dispatcher) {
            when (optionClass) {
                ScoreOption::class -> gamerDao.updateScoreOption(id, null)
                LoserOption::class -> gamerDao.updateLoserOption(id, null)
                WinnerOption::class -> gamerDao.updateWinnerOption(id, null)
                SellerOption::class -> gamerDao.updateSellerOption(id, null)
            }
        }
    }

    override suspend fun updateAccount(gamer: Gamer, account: Int) {
        withContext(dispatcher) {
            gamerDao.updateAccount(gamer.id, account)
        }
    }

}
