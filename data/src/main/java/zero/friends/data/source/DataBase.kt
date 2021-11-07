package zero.friends.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import zero.friends.data.entity.GameEntity
import zero.friends.data.entity.PlayerEntity
import zero.friends.data.entity.RuleEntity
import zero.friends.data.source.dao.GameDao
import zero.friends.data.source.dao.PlayerDao
import zero.friends.data.source.dao.RuleDao
import zero.friends.data.util.SerializationConverters

@Database(entities = [PlayerEntity::class, GameEntity::class, RuleEntity::class], version = 1)
@TypeConverters(SerializationConverters::class)
abstract class DataBase : RoomDatabase() {
    abstract fun PlayerDao(): PlayerDao
    abstract fun GameDao(): GameDao
    abstract fun RuleDao(): RuleDao
}