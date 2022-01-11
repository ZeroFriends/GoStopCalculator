package zero.friends.domain.model

import java.io.Serializable

data class Game(
    val id: Long = 0,
    val name: String = "",
    val createdAt: String = "",
) : Serializable