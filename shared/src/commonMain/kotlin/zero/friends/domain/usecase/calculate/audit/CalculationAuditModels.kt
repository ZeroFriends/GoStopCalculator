package zero.friends.domain.usecase.calculate.audit

enum class AuditPhase(val key: String) {
    ROUND_CALCULATE("round_calculate"),
    ROUND_DETAIL("round_detail"),
    GAME_SETTLEMENT("game_settlement"),
}

enum class CalculationViolationCode(val code: String) {
    ZERO_SUM_BROKEN("ZERO_SUM_BROKEN"),
    TARGET_SUM_MISMATCH("TARGET_SUM_MISMATCH"),
    PAIR_ASYMMETRY("PAIR_ASYMMETRY"),
    TRACE_TOTAL_MISMATCH("TRACE_TOTAL_MISMATCH"),
    SELLER_ROLE_CONTAMINATION("SELLER_ROLE_CONTAMINATION"),
    SELLER_PAYOUT_MISMATCH("SELLER_PAYOUT_MISMATCH"),
    GO_BAK_SOLO_BREACH("GO_BAK_SOLO_BREACH"),
    THREE_FUCK_PAYOUT_MISMATCH("THREE_FUCK_PAYOUT_MISMATCH"),
    BASE_PAYOUT_SIGN_INVALID("BASE_PAYOUT_SIGN_INVALID"),
    AGGREGATION_TOTAL_DRIFT("AGGREGATION_TOTAL_DRIFT"),
}

data class CalculationViolation(
    val code: CalculationViolationCode,
    val message: String,
    val expected: Long? = null,
    val actual: Long? = null,
)

class CalculationInvariantException(
    val issueKey: String,
    message: String,
) : RuntimeException(message)

data class CalculationAuditReport(
    val phase: AuditPhase,
    val gameId: Long,
    val roundId: Long?,
    val playerCount: Int,
    val roundCount: Int?,
    val violations: List<CalculationViolation>,
    val customKeys: Map<String, String>,
    val diagnosticSummary: String,
    val diagnosticPayload: String,
) {
    val violationSignature: String
        get() = violations.map { it.code.code }.distinct().sorted().joinToString(",")

    val issueKey: String
        get() = "${phase.key}:$violationSignature"

    val dedupeKey: String
        get() = "${phase.key}:$gameId:${roundId ?: -1L}:$violationSignature"

    fun toException(): CalculationInvariantException =
        CalculationInvariantException(
            issueKey = issueKey,
            message = diagnosticSummary,
        )
}
