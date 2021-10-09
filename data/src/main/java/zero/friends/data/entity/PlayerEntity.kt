package zero.friends.data.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import zero.friends.domain.model.Player

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = CASCADE,
        )
    ]
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: Int = 0,
    val name: String = "",
    val gameId: Long = 0,
) {
    companion object {
        fun Player.toEntity() = PlayerEntity(number = number, name = name)
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