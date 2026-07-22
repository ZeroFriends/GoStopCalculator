package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import zero.friends.domain.model.Game

@Entity
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val createdAt: String = "",
) {
    companion object {
        fun Game.toEntity() = GameEntity(name = name, createdAt = createdAt)
        fun GameEntity.toGame() = Game(id, name, createdAt)
    }
}
