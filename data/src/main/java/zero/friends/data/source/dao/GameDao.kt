package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import zero.friends.data.entity.GameAndPlayerRelation
import zero.friends.data.entity.GameEntity
import zero.friends.domain.model.Game

@Dao
interface GameDao : BaseDao<GameEntity> {
    @Transaction
    @Query("SELECT * FROM GameEntity WHERE :id = id")
    suspend fun getGameAndPlayer(id: Long): GameAndPlayerRelation

    @Query("SELECT * FROM GameEntity")
    fun observeAllGame(): Flow<List<GameEntity>>

    @Query("SELECT * From GameEntity WHERE id == :gameId")
    suspend fun getGame(gameId: Long): GameEntity

    @Query("SELECT * FROM GameEntity WHERE id == :gameId")
    fun observeGame(gameId: Long): Flow<Game>
}