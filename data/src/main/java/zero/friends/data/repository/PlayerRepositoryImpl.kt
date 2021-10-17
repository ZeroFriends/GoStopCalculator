package zero.friends.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import zero.friends.data.entity.PlayerEntity
import zero.friends.data.entity.PlayerEntity.Companion.toPlayer
import zero.friends.data.source.dao.PlayerDao
import zero.friends.domain.model.Player
import zero.friends.domain.preference.AppPreference
import zero.friends.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    private val appPreference: AppPreference,
    private val playerDao: PlayerDao,
) : PlayerRepository {

    override suspend fun addAutoGeneratePlayer(gameId: Long, playerStringFormat: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val lastInsertedId = appPreference.getLastInsertedPlayerId()
            val playerName = String.format(playerStringFormat, lastInsertedId + 1)
            val player = PlayerEntity(name = playerName, gameId = gameId)
            val id = playerDao.insert(player)
            appPreference.insertLastPlayerId(id)
        }
    }

    override suspend fun observePlayer() =
        playerDao.observePlayer().map { playerEntities ->
            playerEntities.map { it.toPlayer() }
        }


    override suspend fun isExistPlayer(name: String): Boolean = playerDao.isExistPlayer(name)

    override suspend fun deletePlayer(player: Player) {
        CoroutineScope(Dispatchers.IO).launch {
            playerDao.deletePlayer(player.name)
        }
    }

    override suspend fun getPlayers(gameId: Long): List<Player> {
        return playerDao.getPlayers(gameId).map { it.toPlayer() }
    }
}