package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.findOptional

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoundEntity::class,
            parentColumns = ["id"],
            childColumns = ["roundId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["roundId"], unique = false)]
)
data class GamerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roundId: Long = 0,
    val playerId: Long = 0,
    val name: String,
    val gameId: Long,
    val account: Int = 0,
    val winnerOption: String? = null,
    val scoreOption: String? = null,
    val loserOption: String? = null
//    val calculate: List<Target> todo 계산결과 집어넣기
) {
    companion object {
        fun GamerEntity.toGamer() = Gamer(
            id,
            name,
            roundId,
            playerId,
            gameId,
            account,
            findOptional(winnerOption),
            findOptional(scoreOption),
            findOptional(loserOption)
        )
    }
}

data class Target(
    val playerId: Long = 0,
    val account: Int = 0
)