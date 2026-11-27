package zero.friends.domain.usecase.calculate

/**
 * 고스톱 고(Go) 적용 후 최종 점수 계산
 *
 * 규칙:
 * - 0고: 원 점수 그대로
 * - 1~2고: 고할 때마다 +1점씩 가산
 * - 3고 이상: (기본점수 + 고 횟수) × 2^(고-2)
 */
const val MAX_WINNER_SPECIAL_MULTIPLIER_COUNT = 3
const val MAX_ROUND_AUXILIARY_RESERVE = 60_000_000L

/**
 * 동일 라운드 보조 정산 가감 시 Int 오버플로우로 Zero-Sum이 깨지지 않도록
 * 패자 1인당 판돈 상한을 계산한다.
 */
fun calculateMaxPerLoser(loserCount: Int): Long {
    val maxTotalPayout = (Int.MAX_VALUE.toLong() - MAX_ROUND_AUXILIARY_RESERVE).coerceAtLeast(1L)
    return maxTotalPayout / loserCount.coerceAtLeast(1)
}

/**
 * 승자 특수 배수 (흔들기·폭탄) 합산 배율 계산
 * - 1회당 ×2배, 복수 적용 시 곱연산: 2^(shakeCount + bombCount)
 */
fun calculateShakeBombMultiplier(shakeCount: Int, bombCount: Int): Int {
    val totalCount = (shakeCount.coerceAtLeast(0).toLong() + bombCount.coerceAtLeast(0).toLong())
        .coerceAtMost(30L)
        .toInt()
    if (totalCount <= 0) return 1
    return 1 shl totalCount
}

/**
 * 고(Go) + 흔들기·폭탄 배수까지 모두 반영한 승자 최종 점수 계산
 */
fun calculateWinnerScore(
    baseScore: Int,
    goCount: Int,
    shakeCount: Int = 0,
    bombCount: Int = 0,
): Int {
    val goScore = calculateGoScore(baseScore, goCount)
    val specialMult = calculateShakeBombMultiplier(shakeCount, bombCount).toLong()
    return (goScore.toLong() * specialMult)
        .coerceIn(0L, Int.MAX_VALUE.toLong())
        .toInt()
}

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
