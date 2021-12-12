package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
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
//    val gamers: List<GamerEntity> = emptyList(), todo 나중에 필요하면 여기 하자
    val gameId: Long = 0
) {
    companion object {
        fun RoundEntity.toRound() = Round(id, /*gamers.map { it.toGamer() },*/ gameId)
    }
}