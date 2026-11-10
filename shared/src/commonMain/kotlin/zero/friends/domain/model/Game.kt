package zero.friends.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: Long = 0,
    val name: String = "",
    val createdAt: String = "",
)