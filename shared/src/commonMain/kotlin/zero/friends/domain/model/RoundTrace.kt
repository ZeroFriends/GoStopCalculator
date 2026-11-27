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
data class CoveredLoserDetail(
    val playerName: String,
    val amount: Int,
    val bakCount: Int = 0,
)

@Serializable
data class CalculationFactors(
    val isSeller: Boolean,
    // (기본점수 + 고) x 점당금액
    val baseScore: Int,
    val goCount: Int,
    val scorePerPoint: Int,
    // 유효 박 지수 (2인전은 고박 포함, 3인 이상 고스톱은 고박 제외)
    val bakCount: Int,
    // 뻑, 따닥, 광팔기
    val fuckAmount: Int,
    val ddadakAmount: Int,
    val sellAmount: Int,
    // 최종 인원수
    val receiverCount: Int,
    // 3인 이상 고박 독박 대납/면제 상세 필드 (기존 직렬화 데이터 호환을 위해 기본값 지정)
    val selfBaseLoss: Int = 0,
    val goBakCoveredAmount: Int = 0,
    val goBakExemptedAmount: Int = 0,
    val goBakPayerName: String = "",
    val isSoloGoBakMode: Boolean = false,
    val coveredLosers: List<CoveredLoserDetail> = emptyList(),
    val shakeCount: Int = 0,
    val bombCount: Int = 0,
)

@Serializable
enum class TraceTag {
    THREE_FUCK,
    WINNER,
    SELLER,
    LOSER,
    PEA_BAK,
    LIGHT_BAK,
    MONG_BAK,
    GO_BAK,
    GO_BAK_EXEMPT,
}

@Serializable
data class RoundTraceTerm(
    val gamerId: Long,
    val label: String,
    val amount: Int,
    val factors: CalculationFactors,
    val playerName: String = "",
    val traceTags: List<TraceTag> = emptyList()
)
