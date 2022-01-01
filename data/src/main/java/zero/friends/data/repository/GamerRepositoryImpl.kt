package zero.friends.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import zero.friends.data.entity.GamerEntity
import zero.friends.data.entity.GamerEntity.Companion.toGamer
import zero.friends.data.source.dao.GamerDao
import zero.friends.domain.model.*
import zero.friends.domain.model.Target
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

    override suspend fun clearOption(id: Long, optionClass: KClass<out Option>) {
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

    override suspend fun addAccount(gamer: Gamer, account: Int) {
        withContext(dispatcher) {
            val currentGamer = gamerDao.getGamer(gamer.id)
            gamerDao.updateAccount(currentGamer.id, currentGamer.account + account)
        }
    }

    override suspend fun updateScore(gamer: Gamer, score: Int) {
        withContext(dispatcher) {
            gamerDao.updateScore(gamer.id, score)
        }
    }

    override suspend fun findWinner(roundId: Long): Gamer =
        withContext(dispatcher) {
            gamerDao.findWinner(roundId).toGamer()
        }

    override suspend fun findSeller(roundId: Long): Gamer? = withContext(dispatcher) {
        gamerDao.findSeller(roundId)?.toGamer()
    }

    override suspend fun updateTarget(gamer: Gamer, account: Int, target: Gamer) {
        withContext(dispatcher) {
            val targetMap = gamerDao.getGamer(gamer.id).target.associateBy { it.playerId }.toMutableMap()

            val targetGamer = targetMap[target.id]
            if (targetGamer == null) {
                targetMap[target.id] = Target(target.id, account)
            } else {
                targetGamer.account += account
            }

            gamerDao.updateTarget(gamer.id, targetMap.map { it.value })
        }
    }

    override fun observeRoundGamers(roundId: Long): Flow<List<Gamer>> {
        return gamerDao.observeRoundGamers(roundId).map { gamerEntities ->
            gamerEntities.map { it.toGamer() }
        }
    }

}
