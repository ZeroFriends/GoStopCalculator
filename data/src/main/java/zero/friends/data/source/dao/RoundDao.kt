package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import zero.friends.data.entity.RoundEntity

@Dao
interface RoundDao : BaseDao<RoundEntity> {

    @Query("SELECT * FROM RoundEntity WHERE gameId = :gameId")
    suspend fun getAllRound(gameId: Long): List<RoundEntity>

    @Query("DELETE FROM RoundEntity WHERE id = :roundId")
    suspend fun deleteRound(roundId: Long)
}