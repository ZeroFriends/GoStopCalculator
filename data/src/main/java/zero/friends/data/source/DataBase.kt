package zero.friends.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import zero.friends.data.entity.GameEntity
import zero.friends.data.entity.PlayerEntity
import zero.friends.data.source.dao.GameDao
import zero.friends.data.source.dao.PlayerDao

@Database(entities = [PlayerEntity::class, GameEntity::class], version = 1)
abstract class DataBase : RoomDatabase() {
    abstract fun PlayerDao(): PlayerDao
    abstract fun GameDao(): GameDao
}