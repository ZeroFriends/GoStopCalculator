package zero.friends.gostopcalculator.ui.precondition

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RuleHelpExampleTest {

    @Test
    fun `zero score falls back to default 100 sample`() {
        val example = buildRuleHelpScoreExample(0)
        assertTrue(example.isDefaultFallback)
        assertEquals(5, example.samplePoints)
        assertEquals(100L, example.unitAmount)
        assertEquals(500L, example.totalAmount)
    }

    @Test
    fun `negative score falls back to default 100 sample`() {
        val example = buildRuleHelpScoreExample(-50)
        assertTrue(example.isDefaultFallback)
        assertEquals(100L, example.unitAmount)
        assertEquals(500L, example.totalAmount)
    }

    @Test
    fun `positive score uses entered unit amount cleanly`() {
        val example = buildRuleHelpScoreExample(200)
        assertFalse(example.isDefaultFallback)
        assertEquals(200L, example.unitAmount)
        assertEquals(1_000L, example.totalAmount)
    }

    @Test
    fun `max rule score computes in Long without overflow`() {
        val example = buildRuleHelpScoreExample(RULE_MAX_SCORE.toInt())
        assertFalse(example.isDefaultFallback)
        assertEquals(1_000_000L, example.unitAmount)
        assertEquals(5_000_000L, example.totalAmount)
    }
}
