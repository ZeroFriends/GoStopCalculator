package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Game
import zero.friends.domain.model.GameAndPlayer

interface GameRepository{
    suspend fun newGame(name: String, createdAt: String)
    fun getCurrentGameId(): Long
    suspend fun getCurrentGameUser(): GameAndPlayer
    suspend fun deleteGame(gameId: Long)
    suspend fun editGameName(gameName: String)
    fun observeGameName(): Flow<String>
    fun observeGameList(): Flow<List<Game>>
    suspend fun getCurrentGame(): Game
}