package zero.friends.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
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
    private var cacheGameId: Long? = null

    override suspend fun newGame(name: String, createdAt: String): Long {
        return gameDao.insert(GameEntity(name = name, createdAt = createdAt)).also { cacheGameId = it }
    }

    override fun getCurrentGameId(): Long? {
        return cacheGameId
    }

    override suspend fun deleteGame(gameId: Long) {
        withContext(dispatcher) {
            launch {
                gameDao.delete(GameEntity(id = gameId))
                cacheGameId = null
            }
        }
    }

    override suspend fun editGameName(gameName: String) {
        gameDao.editGameName(requireNotNull(cacheGameId), gameName)
    }

    override fun observeGameName(): Flow<String> {
        return gameDao.observeGameName(requireNotNull(cacheGameId)).filterNotNull()
    }

    override fun observeGameList(): Flow<List<Game>> {
        return gameDao.observeAllGame().map { entity ->
            entity.map { it.toGame() }
        }
    }

    override suspend fun getCurrentGame(): Game? {
        return cacheGameId?.let { gameId ->
            gameDao.getGame(gameId).toGame()
        }
    }

    override suspend fun getGame(gameId: Long): Game {
        cacheGameId = gameId
        return gameDao.getGame(gameId).toGame()
    }

    override fun observeGame(gameId: Long): Flow<Game> {
        cacheGameId = gameId
        return gameDao.observeGame(gameId)
    }
}