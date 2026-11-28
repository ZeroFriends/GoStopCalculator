package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Game

interface GameRepository{
    suspend fun newGame(name: String, createdAt: String): Long
    suspend fun deleteGame(gameId: Long)
    fun observeGameList(): Flow<List<Game>>
    suspend fun getGame(gameId: Long): Game
    fun observeGame(gameId: Long): Flow<Game>
    suspend fun updateGameName(gameId: Long, name: String)
    suspend fun updateGameOrder(orderedGameIds: List<Long>)
}
