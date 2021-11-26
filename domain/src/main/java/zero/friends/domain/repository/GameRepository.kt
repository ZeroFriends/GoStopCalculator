package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Game

interface GameRepository{
    suspend fun newGame(name: String, createdAt: String): Long
    fun getCurrentGameId(): Long?
    suspend fun deleteGame(gameId: Long)
    suspend fun editGameName(gameName: String)
    fun observeGameName(): Flow<String>
    fun observeGameList(): Flow<List<Game>>
    suspend fun getCurrentGame(): Game?
    suspend fun getGame(gameId: Long): Game
    fun observeGame(gameId: Long): Flow<Game>
}