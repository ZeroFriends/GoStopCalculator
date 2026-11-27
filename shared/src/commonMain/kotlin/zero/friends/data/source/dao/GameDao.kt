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

    @Query("SELECT * FROM GameEntity ORDER BY sortOrder ASC, id ASC")
    fun observeAllGame(): Flow<List<GameEntity>>

    @Query("SELECT * From GameEntity WHERE id == :gameId")
    suspend fun getGame(gameId: Long): GameEntity

    @Query("SELECT * FROM GameEntity WHERE id == :gameId")
    fun observeGame(gameId: Long): Flow<GameEntity?>

    @Query("UPDATE GameEntity SET name = :name WHERE id = :gameId")
    suspend fun updateGameName(gameId: Long, name: String)

    @Query("SELECT COALESCE(MAX(sortOrder), -1) FROM GameEntity")
    suspend fun getMaxSortOrder(): Int

    @Query("UPDATE GameEntity SET sortOrder = :sortOrder WHERE id = :gameId")
    suspend fun updateGameSortOrder(gameId: Long, sortOrder: Int)

    @Transaction
    suspend fun insertAtEnd(name: String, createdAt: String): Long {
        val nextOrder = getMaxSortOrder() + 1
        return insert(GameEntity(name = name, createdAt = createdAt, sortOrder = nextOrder))
    }

    @Transaction
    suspend fun updateGameSortOrders(orderedGameIds: List<Long>) {
        orderedGameIds.forEachIndexed { index, gameId ->
            updateGameSortOrder(gameId, index)
        }
    }
}
