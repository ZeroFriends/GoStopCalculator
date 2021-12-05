package zero.friends.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import zero.friends.data.entity.*
import zero.friends.data.source.dao.*
import zero.friends.data.util.SerializationConverters

@Database(
    entities = [
        PlayerEntity::class,
        GameEntity::class,
        RuleEntity::class,
        GamerEntity::class,
        RoundEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(SerializationConverters::class)
abstract class DataBase : RoomDatabase() {
    abstract fun PlayerDao(): PlayerDao
    abstract fun GameDao(): GameDao
    abstract fun RuleDao(): RuleDao
    abstract fun GamerDao(): GamerDao
    abstract fun roundDao(): RoundDao
}