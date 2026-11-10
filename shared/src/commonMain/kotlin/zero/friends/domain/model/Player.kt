package zero.friends.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(val id: Long = 0, val name: String = "", val gameId: Long = 0)