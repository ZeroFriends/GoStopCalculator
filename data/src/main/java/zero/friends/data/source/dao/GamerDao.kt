package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zero.friends.data.entity.GamerEntity
import zero.friends.domain.model.Target

@Dao
interface GamerDao : BaseDao<GamerEntity> {
    @Query("SELECT * FROM GamerEntity where gameId = :gameId and playerId = :playerId")
    suspend fun getAllGamer(gameId: Long, playerId: Long): List<GamerEntity>

    @Query("SELECT * FROM GamerEntity where roundId = :roundId")
    suspend fun getRoundGamers(roundId: Long): List<GamerEntity>

    @Query("SELECT * FROM GamerEntity where roundId = :roundId")
    fun observeRoundGamers(roundId: Long): Flow<List<GamerEntity>>

    @Query("DELETE FROM GamerEntity where playerId = :playerId AND roundId = :roundId")
    suspend fun deleteGamer(roundId: Long, playerId: Long)

    @Query("SELECT * FROM GamerEntity where gameId = :gameId")
    fun observeGamers(gameId: Long): Flow<List<GamerEntity>>

    @Query("UPDATE GamerEntity SET winnerOption = :options WHERE id = :gamerId")
    fun updateWinnerOption(gamerId: Long, options: String?)

    @Query("UPDATE GamerEntity SET scoreOption = :options WHERE id = :gamerId")
    fun updateScoreOption(gamerId: Long, options: String?)

    @Query("UPDATE GamerEntity SET loserOption = :options WHERE id = :gamerId")
    fun updateLoserOption(gamerId: Long, options: String?)

    @Query("UPDATE GamerEntity SET sellerOption = :options WHERE id = :gamerId")
    fun updateSellerOption(gamerId: Long, options: String?)

    @Query("SELECT * FROM GamerEntity WHERE id = :gamerId")
    suspend fun getGamer(gamerId: Long): GamerEntity

    @Query("UPDATE GamerEntity SET account = :account WHERE id = :gamerId")
    suspend fun updateAccount(gamerId: Long, account: Int)

    @Query("SELECT * FROM GamerEntity WHERE winnerOption NOT NULL and roundId = :roundId")
    suspend fun findWinner(roundId: Long): GamerEntity

    @Query("SELECT * FROM GamerEntity WHERE sellerOption NOT NULL and roundId = :roundId")
    suspend fun findSeller(roundId: Long): GamerEntity?

    @Query("UPDATE GamerEntity SET score = :score, go = :go WHERE id = :gamerId")
    fun updateScore(gamerId: Long, score: Int, go: Int)

    @Query("SELECT * FROM GamerEntity WHERE gameId = :gameId")
    fun getGamers(gameId: Long): List<GamerEntity>

    @Query("UPDATE GamerEntity SET target = :target WHERE id = :gamerId ")
    suspend fun updateTarget(gamerId: Long, target: List<Target>)
}
