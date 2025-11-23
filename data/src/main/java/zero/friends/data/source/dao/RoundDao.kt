package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zero.friends.data.entity.RoundEntity
import zero.friends.data.entity.RoundGamers

@Dao
interface RoundDao : BaseDao<RoundEntity> {

    @Query("SELECT * FROM RoundEntity WHERE gameId = :gameId")
    fun observeAllRound(gameId: Long): Flow<List<RoundEntity>>

    @Query("SELECT * FROM RoundEntity")
    suspend fun getAllRounds(): List<RoundEntity>

    @Query("DELETE FROM RoundEntity WHERE id = :roundId")
    suspend fun deleteRound(roundId: Long)

    @Query("SELECT * FROM RoundEntity WHERE id = :roundId")
    suspend fun getRound(roundId: Long): RoundEntity

    @Query("SELECT * FROM RoundEntity WHERE id = :roundId")
    fun observeRoundGamers(roundId: Long): Flow<RoundGamers?>
}
