package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zero.friends.data.entity.RoundEntity

@Dao
interface RoundDao : BaseDao<RoundEntity> {

    @Query("SELECT * FROM RoundEntity WHERE gameId = :gameId")
    fun observeAllRound(gameId: Long): Flow<List<RoundEntity>>

    @Query("DELETE FROM RoundEntity WHERE id = :roundId")
    suspend fun deleteRound(roundId: Long)
}