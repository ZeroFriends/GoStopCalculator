package zero.friends.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import zero.friends.data.entity.GameEntity
import zero.friends.data.entity.GameEntity.Companion.toGame
import zero.friends.data.entity.PlayerEntity.Companion.toPlayer
import zero.friends.data.source.dao.GameDao
import zero.friends.domain.model.GameAndPlayer
import zero.friends.domain.repository.GameRepository

class GameRepositoryImpl(private val gameDao: GameDao) : GameRepository {
    private val gameId = MutableStateFlow(0L)
    override suspend fun newGame(name: String, time: String) {
        gameId.value = gameDao.insert(GameEntity(name = name, time = time))
    }

    override fun getCurrentGameId(): Long {
        return gameId.value
    }

    override suspend fun getCurrentGameUser(): GameAndPlayer {
        val relation = gameDao.getGameAndPlayer(gameId.value)
        return GameAndPlayer(relation.game.toGame(), relation.players.map { it.toPlayer() })
    }

    override suspend fun clearGame() {
        CoroutineScope(Dispatchers.IO).launch {
            gameDao.delete(GameEntity(id = gameId.value))
        }
    }

    override suspend fun editGameName(gameName: String) {
        gameDao.editGameName(gameId.value, gameName)
    }

    override fun observeGameName(): Flow<String> {
        return gameDao.observeGameName(gameId.value)
    }

}