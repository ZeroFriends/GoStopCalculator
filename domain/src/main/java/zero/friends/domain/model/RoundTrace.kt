package zero.friends.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RoundTrace(
    val roundId: Long,
    val gameId: Long,
    val winnerId: Long,
    val terms: List<RoundTraceTerm>,
    val totalAmount: Int
)

@Serializable
data class RoundTraceTerm(
    val label: String,
    val detail: String? = null,
    val amount: Int,
    val wrapWithParenthesis: Boolean = true
)
