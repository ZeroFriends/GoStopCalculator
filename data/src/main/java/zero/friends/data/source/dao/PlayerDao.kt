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

    @Query("SELECT * FROM PlayerEntity WHERE :gameId == gameId")
    suspend fun getPlayers(gameId: Long): List<PlayerEntity>

    @Query("SELECT * FROM PlayerEntity")
    suspend fun getPlayers(): List<PlayerEntity>

    @Query("SELECT EXISTS (SELECT * FROM PlayerEntity WHERE :name == name) as isChk")
    suspend fun isExistPlayer(name: String): Boolean

    @Query("SELECT MAX(id) FROM PlayerEntity ")
    suspend fun getMaxId(): Int?
}