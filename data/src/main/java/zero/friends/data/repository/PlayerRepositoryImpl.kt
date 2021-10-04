package zero.friends.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import zero.friends.data.entity.PlayerEntity.Companion.toEntity
import zero.friends.data.entity.PlayerEntity.Companion.toPlayer
import zero.friends.data.source.PlayerDao
import zero.friends.domain.model.Player
import zero.friends.domain.repository.PlayerRepository

class PlayerRepositoryImpl(private val playerDao: PlayerDao) : PlayerRepository {
    override suspend fun addPlayer(newPlayer: Player) {
        CoroutineScope(Dispatchers.IO).launch {
            playerDao.insert(newPlayer.toEntity())
        }
    }

    override suspend fun observePlayer() =
        playerDao.observePlayer().map { playerEntities ->
            playerEntities.map { it.toPlayer() }
        }

    override suspend fun deletePlayer(player: Player) {
        playerDao.delete(player.toEntity())
    }

}