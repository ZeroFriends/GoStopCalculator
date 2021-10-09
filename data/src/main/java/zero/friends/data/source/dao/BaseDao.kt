package zero.friends.data.source.dao

import androidx.room.*

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