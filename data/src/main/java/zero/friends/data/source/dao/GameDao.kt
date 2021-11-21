package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import zero.friends.data.entity.GameAndPlayerRelation
import zero.friends.data.entity.GameEntity

@Dao
interface GameDao : BaseDao<GameEntity> {
    @Transaction
    @Query("SELECT * FROM GameEntity WHERE :id = id")
    suspend fun getGameAndPlayer(id: Long): GameAndPlayerRelation

    @Query("UPDATE GameEntity SET name = :editName WHERE id == :gameId")
    suspend fun editGameName(gameId: Long, editName: String)

    @Query("SELECT name From GameEntity WHERE id == :gameId")
    fun observeGameName(gameId: Long): Flow<String>

    @Query("SELECT * FROM GameEntity")
    suspend fun getAllGame(): List<GameEntity>
}