package zero.friends.gostopcalculator.ui.precondition

import kotlin.test.Test
import kotlin.test.assertEquals

class RuleStepperLogicTest {

    @Test
    fun `1000원 미만 구간에서는 100원 단위로 증가한다`() {
        assertEquals(100L, nextRuleStepUp(0L))
        assertEquals(200L, nextRuleStepUp(100L))
        assertEquals(900L, nextRuleStepUp(800L))
        assertEquals(1_000L, nextRuleStepUp(900L))
    }

    @Test
    fun `1000원 이상 10000원 미만 구간에서는 1000원 단위로 증가한다`() {
        assertEquals(2_000L, nextRuleStepUp(1_000L))
        assertEquals(3_000L, nextRuleStepUp(2_000L))
        assertEquals(10_000L, nextRuleStepUp(9_000L))
        assertEquals(2_500L, nextRuleStepUp(1_500L))
    }

    @Test
    fun `10000원 이상 구간에서는 10000원 단위로 증가하고 최대값을 넘지 않는다`() {
        assertEquals(20_000L, nextRuleStepUp(10_000L))
        assertEquals(30_000L, nextRuleStepUp(20_000L))
        assertEquals(1_000_000L, nextRuleStepUp(995_000L))
        assertEquals(1_000_000L, nextRuleStepUp(1_000_000L))
    }

    @Test
    fun `감소 스테퍼는 경계값에서 대칭 단위로 감소하고 0 미만으로 내려가지 않는다`() {
        assertEquals(0L, nextRuleStepDown(0L))
        assertEquals(0L, nextRuleStepDown(50L))
        assertEquals(0L, nextRuleStepDown(100L))
        assertEquals(900L, nextRuleStepDown(1_000L))
        assertEquals(1_000L, nextRuleStepDown(2_000L))
        assertEquals(9_000L, nextRuleStepDown(10_000L))
        assertEquals(10_000L, nextRuleStepDown(20_000L))
        assertEquals(990_000L, nextRuleStepDown(1_000_000L))
    }
}
