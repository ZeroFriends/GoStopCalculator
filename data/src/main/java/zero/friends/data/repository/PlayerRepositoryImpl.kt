package zero.friends.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zero.friends.data.entity.PlayerEntity.Companion.toPlayer
import zero.friends.data.source.dao.PlayerDao
import zero.friends.domain.model.Player
import zero.friends.domain.repository.PlayerRepository
import zero.friends.shared.IoDispatcher
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : PlayerRepository {

    override suspend fun isExistPlayer(gameId: Long, name: String): Boolean = playerDao.isExistPlayer(gameId, name)

    override suspend fun editPlayer(gameId: Long, player: Player, editPlayer: Player) {
        playerDao.editPlayerName(gameId, player.name, editPlayer.name)
    }

    override suspend fun deletePlayer(gameId: Long, player: Player) {
        withContext(dispatcher) {
            launch { playerDao.deletePlayer(gameId, player.name) }
        }
    }

    override suspend fun getPlayers(gameId: Long): List<Player> {
        return playerDao.getPlayers(gameId).map { it.toPlayer() }
    }

}

