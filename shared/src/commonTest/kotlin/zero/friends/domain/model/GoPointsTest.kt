package zero.friends.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import zero.friends.domain.usecase.calculate.calculateGoScore
import zero.friends.domain.usecase.calculate.calculateShakeBombMultiplier
import zero.friends.domain.usecase.calculate.calculateWinnerScore

class GoPointsTest {

    @Test
    fun `0고 이하일 경우 기본 점수가 그대로 반환된다`() {
        assertEquals(7, calculateGoScore(baseScore = 7, goCount = 0))
        assertEquals(10, calculateGoScore(baseScore = 10, goCount = -1))
    }

    @Test
    fun `1고와 2고는 기본 점수에 고 횟수만큼 가산된다`() {
        // 7점 + 1고 = 8점
        assertEquals(8, calculateGoScore(baseScore = 7, goCount = 1))
        // 7점 + 2고 = 9점
        assertEquals(9, calculateGoScore(baseScore = 7, goCount = 2))
    }

    @Test
    fun `3고 이상은 기본점수와 고 횟수를 더한 후 2의 배수로 증폭된다`() {
        // 3고: (7 + 3) * 2^(3-2) = 10 * 2 = 20점
        assertEquals(20, calculateGoScore(baseScore = 7, goCount = 3))

        // 4고: (7 + 4) * 2^(4-2) = 11 * 4 = 44점
        assertEquals(44, calculateGoScore(baseScore = 7, goCount = 4))

        // 5고: (7 + 5) * 2^(5-2) = 12 * 8 = 96점
        assertEquals(96, calculateGoScore(baseScore = 7, goCount = 5))
    }

    @Test
    fun `고점수 및 다중 고 입력 시 Int 오버플로우 없이 최대값으로 안전하게 제한된다`() {
        assertEquals(Int.MAX_VALUE, calculateGoScore(baseScore = 8_519_680, goCount = 20))
        assertEquals(Int.MAX_VALUE, calculateGoScore(baseScore = 100_000, goCount = 35))
    }

    @Test
    fun `흔들기와 폭탄 배수는 합산 횟수만큼 2의 거듭제곱으로 누적 적용된다`() {
        assertEquals(1, calculateShakeBombMultiplier(shakeCount = 0, bombCount = 0))
        assertEquals(1, calculateShakeBombMultiplier(shakeCount = -1, bombCount = -2))
        assertEquals(2, calculateShakeBombMultiplier(shakeCount = 1, bombCount = 0))
        assertEquals(2, calculateShakeBombMultiplier(shakeCount = 0, bombCount = 1))
        assertEquals(4, calculateShakeBombMultiplier(shakeCount = 1, bombCount = 1))
        assertEquals(8, calculateShakeBombMultiplier(shakeCount = 2, bombCount = 1))
        assertEquals(64, calculateShakeBombMultiplier(shakeCount = 3, bombCount = 3))
    }

    @Test
    fun `승자 최종 점수는 고 점수와 흔들기·폭탄 배수를 곱해 오버플로우 없이 계산된다`() {
        // 7점 0고 흔들기1 -> 14점
        assertEquals(14, calculateWinnerScore(baseScore = 7, goCount = 0, shakeCount = 1, bombCount = 0))
        // 2점 3고 ((2+3)*2=10) × 흔들기1·폭탄1(×4) = 40점
        assertEquals(40, calculateWinnerScore(baseScore = 2, goCount = 3, shakeCount = 1, bombCount = 1))
        // 0점 0고 -> 배수가 있어도 0점 유지
        assertEquals(0, calculateWinnerScore(baseScore = 0, goCount = 0, shakeCount = 2, bombCount = 1))
        // 오버플로우 가드
        assertEquals(Int.MAX_VALUE, calculateWinnerScore(baseScore = 8_519_680, goCount = 10, shakeCount = 3, bombCount = 3))
    }
}
