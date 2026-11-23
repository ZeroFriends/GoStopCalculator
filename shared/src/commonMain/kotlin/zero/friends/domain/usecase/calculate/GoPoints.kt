package zero.friends.domain.usecase.calculate

/**
 * 고스톱 고(Go) 적용 후 최종 점수 계산
 *
 * 규칙:
 * - 0고: 원 점수 그대로
 * - 1~2고: 고할 때마다 +1점씩 가산
 * - 3고 이상: (기본점수 + 고 횟수) × 2^(고-2)
 */
fun calculateGoMultiplier(goCount: Int): Int {
    if (goCount <= 2) return 1
    return 1 shl (goCount - 2).coerceAtMost(30)
}

fun calculateGoScore(baseScore: Int, goCount: Int): Int {
    if (goCount <= 0) return baseScore
    if (goCount <= 2) {
        return (baseScore.toLong() + goCount)
            .coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong())
            .toInt()
    }

    val added = baseScore.toLong() + goCount
    val multiplier = calculateGoMultiplier(goCount).toLong()
    return (added * multiplier)
        .coerceIn(0L, Int.MAX_VALUE.toLong())
        .toInt()
}
