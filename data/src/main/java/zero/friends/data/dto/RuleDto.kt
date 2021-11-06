package zero.friends.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RuleDto(
    val title: String,
    val isEssential: Boolean,
    val script: String,
)