package zero.friends.domain.model

data class Gamer(
    val id: Long = 0,
    val roundId: Long = 0,
    val playerId: Long = 0,
    val gameId: Long,
    val account: Int = 0,
    val optional: String? = null,
)
