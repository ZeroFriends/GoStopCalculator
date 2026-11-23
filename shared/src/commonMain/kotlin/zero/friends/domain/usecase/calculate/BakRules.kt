package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.LoserOption

/**
 * 나무위키 고스톱·맞고 표준 박(Bak) 배수 규칙 (SSOT)
 *
 * - 2인전(맞고, [loserCount] <= 1): 대납할 다른 패자가 없으므로 고박([LoserOption.GoBak])도 2배 배수에 포함된다.
 * - 3인 이상 고스톱([loserCount] >= 2): 고박은 본인 2배 배수가 아니라 '다른 패자의 판 점수 전액 대납(독박)' 책임이므로,
 *   개별 패자의 판 점수 박 배수는 고박을 제외한 순수 박(피박·광박·멍박) 개수로만 계산한다.
 */
fun isGoBakSoloCoverageMode(loserCount: Int): Boolean = loserCount >= 2

fun effectiveBakOptions(
    loserOptions: List<LoserOption>,
    loserCount: Int,
): List<LoserOption> {
    val distinctOptions = loserOptions.distinct()
    return if (isGoBakSoloCoverageMode(loserCount)) {
        distinctOptions.filter { it != LoserOption.GoBak }
    } else {
        distinctOptions
    }
}

fun effectiveBakCount(
    loserOptions: List<LoserOption>,
    loserCount: Int,
): Int = effectiveBakOptions(loserOptions, loserCount).size

fun calculateBakMultiplier(
    loserOptions: List<LoserOption>,
    loserCount: Int,
): Int = 1 shl effectiveBakCount(loserOptions, loserCount).coerceAtMost(30)
