package zero.friends.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Rule(
    val title: String,
    val isEssential: Boolean,
    val script: String,
    var score: Int = 0,
)