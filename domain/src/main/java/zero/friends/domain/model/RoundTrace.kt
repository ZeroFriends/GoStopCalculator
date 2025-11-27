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
data class CalculationFactors(
    val isSeller: Boolean,
    // (기본점수 + 고) x 점당금액
    val baseScore: Int,
    val goCount: Int,
    val scorePerPoint: Int,
    // 박
    val bakCount: Int,
    // 뻑, 따닥, 광팔기
    val fuckAmount: Int,
    val ddadakAmount: Int,
    val sellAmount: Int,
    // 최종 인원수
    val receiverCount: Int
)

@Serializable
data class RoundTraceTerm(
    val gamerId: Long,
    val label: String,
    val amount: Int,
    val factors: CalculationFactors
)
