package zero.friends.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import zero.friends.domain.model.Player

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["gameId"], unique = false)]
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val gameId: Long = 0,
) {
    companion object {
        fun Player.toEntity() = PlayerEntity(name = name)
        fun PlayerEntity.toPlayer() = Player(id, name, gameId)
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
