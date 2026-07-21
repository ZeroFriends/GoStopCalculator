package zero.friends.data.source

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import zero.friends.data.entity.GameEntity
import zero.friends.data.entity.GamerEntity
import zero.friends.data.entity.PlayerEntity
import zero.friends.data.entity.RoundEntity
import zero.friends.data.entity.RuleEntity
import zero.friends.data.source.dao.GameDao
import zero.friends.data.source.dao.GamerDao
import zero.friends.data.source.dao.PlayerDao
import zero.friends.data.source.dao.RoundDao
import zero.friends.data.source.dao.RuleDao
import zero.friends.data.util.SerializationConverters

@Database(
    entities = [
        PlayerEntity::class,
        GameEntity::class,
        RuleEntity::class,
        GamerEntity::class,
        RoundEntity::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(SerializationConverters::class)
@ConstructedBy(DataBaseConstructor::class)
abstract class DataBase : RoomDatabase() {
    abstract fun PlayerDao(): PlayerDao
    abstract fun GameDao(): GameDao
    abstract fun RuleDao(): RuleDao
    abstract fun GamerDao(): GamerDao
    abstract fun roundDao(): RoundDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DataBaseConstructor : RoomDatabaseConstructor<DataBase>

fun getRoomDatabase(
    builder: RoomDatabase.Builder<DataBase>
): DataBase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
}

