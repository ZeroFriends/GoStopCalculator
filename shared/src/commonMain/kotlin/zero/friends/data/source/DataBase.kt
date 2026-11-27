package zero.friends.data.source

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
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
    version = 4,
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
expect object DataBaseConstructor : RoomDatabaseConstructor<DataBase> {
    override fun initialize(): DataBase
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE GamerEntity ADD COLUMN shakeCount INTEGER NOT NULL DEFAULT 0"
        )
        connection.execSQL(
            "ALTER TABLE GamerEntity ADD COLUMN bombCount INTEGER NOT NULL DEFAULT 0"
        )
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE RoundEntity ADD COLUMN name TEXT NOT NULL DEFAULT ''"
        )
        connection.execSQL(
            "ALTER TABLE GameEntity ADD COLUMN sortOrder INTEGER NOT NULL DEFAULT 0"
        )
        connection.execSQL(
            "UPDATE GameEntity SET sortOrder = (SELECT COUNT(*) FROM GameEntity AS g2 WHERE g2.id < GameEntity.id)"
        )
    }
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<DataBase>
): DataBase {
    return builder
        .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

