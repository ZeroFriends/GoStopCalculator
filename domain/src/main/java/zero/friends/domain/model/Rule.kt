package zero.friends.domain.model

import kotlinx.serialization.Serializable
import zero.friends.domain.util.Const.Rule as RuleName

@Serializable
data class Rule(
    val name: RuleName = RuleName.Score,
    val isEssential: Boolean = false,
    val script: String = "",
    val score: Int = 0,
)
