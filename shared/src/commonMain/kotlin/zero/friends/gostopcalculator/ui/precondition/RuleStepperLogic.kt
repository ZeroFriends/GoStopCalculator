package zero.friends.gostopcalculator.ui.precondition

internal const val RULE_MAX_SCORE = 1_000_000L

/**
 * 룰 금액 설정 '+' 스테퍼 다음 값 계산
 * - 1,000원 미만: +100원
 * - 1,000원 이상 ~ 10,000원 미만: +1,000원
 * - 10,000원 이상: +10,000원
 */
fun nextRuleStepUp(current: Long, max: Long = RULE_MAX_SCORE): Long {
    val sanitized = current.coerceAtLeast(0L)
    val delta = when {
        sanitized < 1_000L -> 100L
        sanitized < 10_000L -> 1_000L
        else -> 10_000L
    }
    return (sanitized + delta).coerceAtMost(max)
}

/**
 * 룰 금액 설정 '-' 스테퍼 이전 값 계산 (증감 경계에서 대칭 동작)
 * - 1,000원 이하: -100원
 * - 1,001원 ~ 10,000원 이하: -1,000원
 * - 10,001원 이상: -10,000원
 */
fun nextRuleStepDown(current: Long): Long {
    val sanitized = current.coerceAtLeast(0L)
    if (sanitized == 0L) return 0L
    val delta = when {
        sanitized <= 1_000L -> 100L
        sanitized <= 10_000L -> 1_000L
        else -> 10_000L
    }
    return (sanitized - delta).coerceAtLeast(0L)
}
