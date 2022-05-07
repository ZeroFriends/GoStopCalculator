package zero.friends.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zero.friends.data.entity.GameEntity
import zero.friends.data.entity.GameEntity.Companion.toGame
import zero.friends.data.source.dao.GameDao
import zero.friends.domain.model.Game
import zero.friends.domain.repository.GameRepository
import zero.friends.shared.IoDispatcher
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : GameRepository {

    override suspend fun newGame(name: String, createdAt: String): Long {
        return gameDao.insert(GameEntity(name = name, createdAt = createdAt))
    }

    override suspend fun deleteGame(gameId: Long) {
        withContext(dispatcher) {
            launch {
                gameDao.delete(GameEntity(id = gameId))
            }
        }
    }

    override fun observeGameList(): Flow<List<Game>> {
        return gameDao.observeAllGame().map { entity ->
            entity.map { it.toGame() }
        }
    }

    override suspend fun getGame(gameId: Long): Game {
        return gameDao.getGame(gameId).toGame()
    }

    override fun observeGame(gameId: Long): Flow<Game> {
        return gameDao.observeGame(gameId)
    }
}