package zero.friends.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import zero.friends.data.entity.GameEntity
import zero.friends.data.entity.GameEntity.Companion.toGame
import zero.friends.data.entity.PlayerEntity.Companion.toPlayer
import zero.friends.data.source.dao.GameDao
import zero.friends.domain.model.Game
import zero.friends.domain.model.GameAndPlayer
import zero.friends.domain.repository.GameRepository

class GameRepositoryImpl(private val gameDao: GameDao) : GameRepository {
    private val cacheGameId = MutableStateFlow(0L)
    override suspend fun newGame(name: String, createdAt: String) {
        cacheGameId.value = gameDao.insert(GameEntity(name = name, createdAt = createdAt))
    }

    override fun getCurrentGameId(): Long {
        return cacheGameId.value
    }

    override suspend fun getCurrentGameUser(): GameAndPlayer {
        val relation = gameDao.getGameAndPlayer(cacheGameId.value)
        return GameAndPlayer(relation.game.toGame(), relation.players.map { it.toPlayer() })
    }

    override suspend fun deleteGame(gameId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            gameDao.delete(GameEntity(id = gameId))
        }
    }

    override suspend fun editGameName(gameName: String) {
        gameDao.editGameName(cacheGameId.value, gameName)
    }

    override fun observeGameName(): Flow<String> {
        return gameDao.observeGameName(cacheGameId.value).filterNotNull()
    }

    override fun observeGameList(): Flow<List<Game>> {
        return gameDao.observeAllGame().map { entity ->
            entity.map { it.toGame() }
        }
    }

}