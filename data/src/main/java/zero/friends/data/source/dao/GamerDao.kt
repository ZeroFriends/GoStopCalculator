package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import zero.friends.data.entity.GamerEntity

@Dao
interface GamerDao : BaseDao<GamerEntity> {
    @Query("SELECT * FROM GamerEntity where gameId = :gameId and playerId = :playerId")
    suspend fun getAllGamer(gameId: Long, playerId: Long): List<GamerEntity>

    @Query("SELECT * FROM GamerEntity where roundId = :roundId")
    suspend fun getRoundGamers(roundId: Long): List<GamerEntity>

    @Query("DELETE FROM GamerEntity where playerId = :playerId AND roundId = :roundId")
    suspend fun deleteGamer(roundId: Long, playerId: Long)
}