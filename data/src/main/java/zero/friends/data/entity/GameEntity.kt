package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GameEntity(
    @PrimaryKey val time: String,
    val name: String,
)
