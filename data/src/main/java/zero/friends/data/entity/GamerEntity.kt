package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import zero.friends.domain.model.Gamer

@Entity(
    foreignKeys = [
//        ForeignKey(
//            entity = RoundEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["roundId"],
//            onDelete = CASCADE
//        ),
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["roundId"], unique = true)]
)
data class GamerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roundId: Long = 0,
    val playerId: Long = 0,
    val gameId: Long,
    val account: Int = 0,
    val optional: String? = null,
//    val calculate: List<Target>
) {
    companion object {
        fun GamerEntity.toGamer() = Gamer(id, roundId, playerId, gameId, account, optional)
    }
}

data class Target(
    val playerId: Long = 0,
    val account: Int = 0
)