package zero.friends.data.source

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun getDatabaseBuilder(): RoomDatabase.Builder<DataBase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    val dbFile = requireNotNull(documentDirectory?.path) + "/gostop_calculator.db"
    return Room.databaseBuilder<DataBase>(
        name = dbFile,
        factory = { DataBaseConstructor.initialize() }
    )
}
