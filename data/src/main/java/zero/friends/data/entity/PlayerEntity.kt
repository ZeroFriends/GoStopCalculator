package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import zero.friends.domain.model.Player

@Entity()
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: Int = 0,
    val name: String = "",
) {
    companion object {
        fun Player.toEntity() = PlayerEntity(number = number, name = name)
        fun PlayerEntity.toPlayer() = Player(number, name)
    }
}