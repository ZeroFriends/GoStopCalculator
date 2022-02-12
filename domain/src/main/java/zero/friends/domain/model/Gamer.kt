package zero.friends.domain.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

data class Gamer(
    val id: Long = 0,
    val name: String = "",
    val roundId: Long = 0,
    val playerId: Long = 0,
    val gameId: Long = 0,
    val score: Int = 0,
    val account: Int = 0,
    val winnerOption: WinnerOption? = null,
    val sellerOption: SellerOption? = null,
    val scoreOption: List<ScoreOption> = emptyList(),
    val loserOption: List<LoserOption> = emptyList(),
    val calculate: List<Target> = emptyList()
) {
    fun isWinner() = winnerOption != null || sellerOption != null
}

@Serializable
@Keep
data class Target(
    val playerId: Long = 0,
    val name: String = "",
    var account: Int = 0
)