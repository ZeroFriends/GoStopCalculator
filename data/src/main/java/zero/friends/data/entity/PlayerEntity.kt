package zero.friends.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import zero.friends.domain.model.Player

@Entity
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: Int = 0,
    val name: String = "",
    val gameId: String = "",
) {
    companion object {
        fun Player.toEntity() = PlayerEntity(number = number, name = name)
        fun PlayerEntity.toPlayer() = Player(number, name)
    }
}

data class GameAndPlayer(
    @Embedded val game: GameEntity,
    @Relation(
        parentColumn = "time",
        entityColumn = "gameId"
    )
    val playerList: List<PlayerEntity>,
)