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
    val gameId: Long = 0L,
) {
    companion object {
        fun Player.toEntity(gameId: Long) = PlayerEntity(number = number, name = name, gameId = gameId)
        fun PlayerEntity.toPlayer() = Player(number, name)
    }
}

data class GameAndPlayerRelation(
    @Embedded val game: GameEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "gameId"
    )
    val players: List<PlayerEntity>,
)