package zero.friends.domain.usecase.calculate

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
