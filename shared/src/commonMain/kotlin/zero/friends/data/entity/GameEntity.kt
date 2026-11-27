package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import zero.friends.domain.model.Game

@Entity
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val createdAt: String = "",
    val sortOrder: Int = 0,
) {
    companion object {
        fun Game.toEntity() = GameEntity(id = id, name = name, createdAt = createdAt, sortOrder = sortOrder)
        fun GameEntity.toGame() = Game(id = id, name = name, createdAt = createdAt, sortOrder = sortOrder)
    }
}
