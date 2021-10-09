package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import zero.friends.domain.model.Game

@Entity
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val time: String = "",
) {
    companion object {
        fun Game.toEntity() = GameEntity(name = name, time = time)
        fun GameEntity.toGame() = Game(name, time)
    }
}
