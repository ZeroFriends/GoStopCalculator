package zero.friends.domain.model

data class Gamer(
    val id: Long = 0,
    val name: String = "",
    val roundId: Long = 0,
    val playerId: Long = 0,
    val gameId: Long = 0,
    val account: Int = 0,
    val optional: List<Optional> = listOf(Optional.None)
)