package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import timber.log.Timber
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.WinnerOption
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
    val score: Int = 0,
    val account: Int = 0,
    val winnerOption: String? = null,
    val sellerOption: String? = null,
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
            score,
            account,
            findOptional<WinnerOption>(winnerOption).firstOrNull(),
            findOptional<SellerOption>(sellerOption).firstOrNull(),
            findOptional(scoreOption),
            findOptional(loserOption)
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
        ).also { Timber.tag("🔥zero:toGamerEntity").d("$it") }
    }
}

data class Target(
    val playerId: Long = 0,
    val account: Int = 0
)