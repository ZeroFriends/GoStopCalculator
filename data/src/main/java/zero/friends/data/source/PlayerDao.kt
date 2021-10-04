package zero.friends.data.source

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import zero.friends.data.entity.PlayerEntity

@Dao
interface BaseDao<DATA> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: DATA)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: List<DATA>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(data: DATA)

    @Delete
    suspend fun delete(data: DATA)
}

@Dao
interface PlayerDao : BaseDao<PlayerEntity> {
    @Query("SELECT * FROM PlayerEntity")
    fun observePlayer(): Flow<List<PlayerEntity>>
}