package zero.friends.data.source.dao

import androidx.room.Dao
import androidx.room.Query
import zero.friends.data.entity.RuleEntity

@Dao
interface RuleDao : BaseDao<RuleEntity> {
    @Query("SELECT * FROM RuleEntity WHERE gameId == :gameId")
    fun getRule(gameId: Long): RuleEntity

}