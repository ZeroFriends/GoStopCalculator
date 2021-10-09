package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import zero.friends.data.entity.GameAndPlayerRelation
import zero.friends.data.entity.GameEntity

@Dao
interface GameDao : BaseDao<GameEntity> {
    @Transaction
    @Query("SELECT * FROM GameEntity WHERE :id = id")
    suspend fun getGameAndPlayer(id:Long): GameAndPlayerRelation
}