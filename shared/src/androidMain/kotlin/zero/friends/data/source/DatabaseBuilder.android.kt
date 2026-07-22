package zero.friends.data.source

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

private lateinit var appContext: Context

fun initDatabaseContext(context: Context) {
    appContext = context.applicationContext
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<DataBase> {
    val dbFile = appContext.getDatabasePath("gostop_calculator.db")
    return Room.databaseBuilder<DataBase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
