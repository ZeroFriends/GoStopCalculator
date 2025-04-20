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
    val account: Int = 0,
    val winnerOption: String? = null,
    val sellerOption: String? = null,
    val scoreOption: String? = null,
    val loserOption: String? = null,
    val target: List<Target> = emptyList()
) {
    companion object {
        fun GamerEntity.toGamer() = Gamer(
            id,
            name,
            roundId,
            playerId,
            gameId,
            score,
            account,
            findOptional<WinnerOption>(winnerOption).firstOrNull(),
            findOptional<SellerOption>(sellerOption).firstOrNull(),
            findOptional(scoreOption),
            findOptional(loserOption),
            target
        )

        fun Gamer.toGamerEntity() = GamerEntity(
            id = id,
            roundId = roundId,
            playerId = playerId,
            name = name,
            gameId = gameId,
            score = score,
            winnerOption = this.winnerOption?.name,
            sellerOption = this.sellerOption?.name,
            scoreOption = if (this.scoreOption.isEmpty()) null else this.scoreOption.joinToString(","),
            loserOption = if (this.loserOption.isEmpty()) null else this.loserOption.joinToString(",")
        )
    }
}
