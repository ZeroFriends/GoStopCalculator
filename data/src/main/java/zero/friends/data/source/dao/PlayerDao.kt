package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zero.friends.data.entity.PlayerEntity

@Dao
interface PlayerDao : BaseDao<PlayerEntity> {
    @Query("SELECT * FROM PlayerEntity WHERE :gameId == gameId")
    fun observePlayer(gameId: Long): Flow<List<PlayerEntity>>

    @Query("DELETE FROM PlayerEntity WHERE :name == name and :gameId == gameId")
    suspend fun deletePlayer(gameId: Long, name: String)

    @Query("SELECT * FROM PlayerEntity WHERE :gameId == gameId")
    suspend fun getPlayers(gameId: Long): List<PlayerEntity>

    @Query("SELECT * FROM PlayerEntity")
    suspend fun getPlayers(): List<PlayerEntity>

    @Query("SELECT EXISTS (SELECT * FROM PlayerEntity WHERE :name == name and :gameId = gameId) as isChk")
    suspend fun isExistPlayer(gameId: Long, name: String): Boolean

    @Query("SELECT MAX(id) FROM PlayerEntity ")
    suspend fun getMaxId(): Int?

    @Query("UPDATE PlayerEntity SET name = :editName WHERE :originalName == name AND gameId == :gameId")
    suspend fun editPlayerName(gameId: Long, originalName: String, editName: String)
}