package zero.friends.domain.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class Rule(
    val name: String = "",
    val isEssential: Boolean = false,
    val script: String = "",
    val score: Int = 0,
)