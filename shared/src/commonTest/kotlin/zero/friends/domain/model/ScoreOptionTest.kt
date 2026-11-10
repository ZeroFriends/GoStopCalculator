package zero.friends.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreOptionTest {

    private val fuckScore = 500
    private val scorePerPoint = 100

    @Test
    fun `첫뻑 옵션 점수 계산`() {
        val result = ScoreOption.FirstFuck.calculateScore(fuckScore, scorePerPoint)
        assertEquals(500, result)
    }

    @Test
    fun `2연뻑 옵션 점수 계산은 2배 적용`() {
        val result = ScoreOption.SecondFuck.calculateScore(fuckScore, scorePerPoint)
        assertEquals(1000, result)
    }

    @Test
    fun `3연뻑 옵션 점수 계산은 4배 적용`() {
        val result = ScoreOption.ThreeFuck.calculateScore(fuckScore, scorePerPoint)
        assertEquals(2000, result)
    }

    @Test
    fun `첫따닥 옵션 점수 계산은 1점당 금액의 3배 적용`() {
        val result = ScoreOption.FirstDdadak.calculateScore(fuckScore, scorePerPoint)
        assertEquals(300, result)
    }
}
