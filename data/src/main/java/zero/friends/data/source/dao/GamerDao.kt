package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zero.friends.data.entity.GamerEntity
import zero.friends.domain.model.Gamer

@Dao
interface GamerDao : BaseDao<GamerEntity> {
    @Query("SELECT * FROM GamerEntity where gameId = :gameId and playerId = :playerId")
    suspend fun getAllGamer(gameId: Long, playerId: Long): List<GamerEntity>

    @Query("SELECT * FROM GamerEntity where roundId = :roundId")
    suspend fun getRoundGamers(roundId: Long): List<GamerEntity>

    @Query("DELETE FROM GamerEntity where playerId = :playerId AND roundId = :roundId")
    suspend fun deleteGamer(roundId: Long, playerId: Long)

    @Query("SELECT * FROM GamerEntity where gameId = :gameId")
    fun observeGamers(gameId: Long): Flow<List<Gamer>>
}