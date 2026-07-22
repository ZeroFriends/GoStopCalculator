package zero.friends.data.source

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<DataBase>
