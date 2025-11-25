package zero.friends.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        RoundEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(SerializationConverters::class)
abstract class DataBase : RoomDatabase() {
    abstract fun PlayerDao(): PlayerDao
    abstract fun GameDao(): GameDao
    abstract fun RuleDao(): RuleDao
    abstract fun GamerDao(): GamerDao
    abstract fun roundDao(): RoundDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    ALTER TABLE `GamerEntity`
                    ADD COLUMN `go` INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )
            }
        }
    }
}
