package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import zero.friends.domain.model.Player

@Entity
data class PlayerEntity(@PrimaryKey val id: String, val name: String) {
    companion object {
        fun Player.toEntity() = PlayerEntity(id, name)
        fun PlayerEntity.toPlayer() = Player(id, name)
    }
}