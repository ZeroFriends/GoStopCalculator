package zero.friends.domain.usecase.calculate

/**
 * 고스톱 고(Go) 적용 후 최종 점수 계산
 *
 * 규칙:
 * - 0고: 원 점수 그대로
 * - 1~2고: 고할 때마다 +1점씩 가산
 * - 3고 이상: (기본점수 + 고 횟수) × 2^(고-2)
 */
internal fun calculateGoScore(baseScore: Int, goCount: Int): Int {
    if (goCount <= 0) return baseScore
    if (goCount <= 2) return baseScore + goCount

    val added = baseScore + goCount
    val multiplier = 1 shl (goCount - 2)
    return added * multiplier
}
