package zero.friends.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OptionalTest {

    @Test
    fun `null 입력 시 빈 리스트 반환`() {
        val options = findOptional<ScoreOption>(null)
        assertTrue(options.isEmpty())
    }

    @Test
    fun `정상적인 CSV 옵션 문자열 파싱`() {
        val options = findOptional<ScoreOption>("FirstFuck, FirstDdadak")
        assertEquals(2, options.size)
        assertEquals(ScoreOption.FirstFuck, options[0])
        assertEquals(ScoreOption.FirstDdadak, options[1])
    }

    @Test
    fun `잘못된 옵션명이 포함되어 있어도 정상 옵션만 걸러내어 파싱`() {
        val options = findOptional<LoserOption>("PeaBak, InvalidOption, GoBak")
        assertEquals(2, options.size)
        assertEquals(LoserOption.PeaBak, options[0])
        assertEquals(LoserOption.GoBak, options[1])
    }
}
