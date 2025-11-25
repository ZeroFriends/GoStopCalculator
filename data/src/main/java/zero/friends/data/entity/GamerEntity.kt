package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.Target
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.model.findOptional

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoundEntity::class,
            parentColumns = ["id"],
            childColumns = ["roundId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
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
    val score: Int = 0,
    val go: Int = 0,
    val account: Int = 0,
    val winnerOption: String? = null,
    val sellerOption: String? = null,
    val scoreOption: String? = null,
    val loserOption: String? = null,
    val target: List<Target> = emptyList()
) {
    companion object {
        fun GamerEntity.toGamer() = Gamer(
            id = id,
            name = name,
            roundId = roundId,
            playerId = playerId,
            gameId = gameId,
            score = score,
            go = go,
            account = account,
            winnerOption = findOptional<WinnerOption>(winnerOption).firstOrNull(),
            sellerOption = findOptional<SellerOption>(sellerOption).firstOrNull(),
            scoreOption = findOptional(scoreOption),
            loserOption = findOptional(loserOption),
            calculate = target
        )

        fun Gamer.toGamerEntity() = GamerEntity(
            id = id,
            roundId = roundId,
            playerId = playerId,
            name = name,
            gameId = gameId,
            score = score,
            go = go,
            winnerOption = this.winnerOption?.name,
            sellerOption = this.sellerOption?.name,
            scoreOption = if (this.scoreOption.isEmpty()) null else this.scoreOption.joinToString(","),
            loserOption = if (this.loserOption.isEmpty()) null else this.loserOption.joinToString(",")
        )
    }
}
