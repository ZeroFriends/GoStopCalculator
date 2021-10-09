package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zero.friends.data.entity.PlayerEntity

@Dao
interface PlayerDao : BaseDao<PlayerEntity> {
    @Query("SELECT * FROM PlayerEntity")
    fun observePlayer(): Flow<List<PlayerEntity>>

    @Query("DELETE FROM PlayerEntity WHERE :name == name")
    suspend fun deletePlayer(name: String)
}