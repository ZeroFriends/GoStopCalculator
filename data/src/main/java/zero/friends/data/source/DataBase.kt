package zero.friends.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import zero.friends.data.entity.PlayerEntity

@Database(entities = [PlayerEntity::class], version = 1)
abstract class DataBase : RoomDatabase() {
    abstract fun PlayerDao(): PlayerDao
}