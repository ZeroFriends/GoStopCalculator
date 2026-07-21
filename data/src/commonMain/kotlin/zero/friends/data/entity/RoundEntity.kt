package zero.friends.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import zero.friends.domain.model.Round

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["gameId"], unique = false)]
)
data class RoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameId: Long = 0
) {
    companion object {
        fun RoundEntity.toRound() = Round(id, gameId)
    }
}

data class RoundGamers(
    @Embedded val roundEntity: RoundEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "roundId"
    )
    val gamers: List<GamerEntity>
)
