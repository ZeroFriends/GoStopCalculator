package zero.friends.data.source

import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(): RoomDatabase.Builder<DataBase> {
    val dbFile = platform.Foundation.NSHomeDirectory() + "/Documents/gostop_calculator.db"
    return Room.databaseBuilder<DataBase>(
        name = dbFile
    )
}
