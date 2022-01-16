package zero.friends.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Rule(
    val name: String = "",
    val isEssential: Boolean = false,
    val script: String = "",
    val score: Int = 0,
)