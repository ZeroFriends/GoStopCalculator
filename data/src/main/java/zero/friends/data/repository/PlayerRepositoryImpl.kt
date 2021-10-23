package zero.friends.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import zero.friends.data.entity.PlayerEntity
import zero.friends.data.entity.PlayerEntity.Companion.toPlayer
import zero.friends.data.source.dao.PlayerDao
import zero.friends.domain.model.Player
import zero.friends.domain.repository.PlayerRepository
import java.util.*
class PlayerRepositoryImpl(
    private val playerDao: PlayerDao,
) : PlayerRepository {
    private val playerIdCache = WeakHashMap<Long, Int>()

    override suspend fun addAutoGeneratePlayer(gameId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val previousId = playerIdCache[gameId] ?: 0
            val nextId = previousId + 1
            playerIdCache[gameId] = nextId

            val playerName = String.format(PLAYER_FORMAT, nextId)
            val player = PlayerEntity(name = playerName, gameId = gameId)
            playerDao.insert(player)
        }
    }

    override suspend fun observePlayer(gameId: Long) =
        playerDao.observePlayer(gameId).map { playerEntities ->
            playerEntities.map { it.toPlayer() }
        }


    override suspend fun isExistPlayer(gameId: Long, name: String): Boolean = playerDao.isExistPlayer(gameId, name)

    override suspend fun editPlayer(gameId: Long, player: Player, editPlayer: Player) {
        playerDao.editPlayerName(gameId, player.name, editPlayer.name)
    }

    override suspend fun deletePlayer(gameId: Long, player: Player) {
        CoroutineScope(Dispatchers.IO).launch {
            playerDao.deletePlayer(gameId, player.name)
        }
    }

    override suspend fun getPlayers(gameId: Long): List<Player> {
        return playerDao.getPlayers(gameId).map { it.toPlayer() }
    }

    companion object {
        const val PLAYER_FORMAT = "새 플레이어 %d"
    }
}

