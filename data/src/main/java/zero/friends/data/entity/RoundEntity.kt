package zero.friends.data.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import zero.friends.domain.model.Round

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["gameId"], unique = false)]
)
data class RoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameId: Long = 0
) {
    companion object {
        fun RoundEntity.toRound() = Round(id, /*gamers.map { it.toGamer() },*/ gameId)
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