package zero.friends.domain.model

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Player(val id: Long = 0, val name: String = "", val gameId: Long = 0) : Serializable