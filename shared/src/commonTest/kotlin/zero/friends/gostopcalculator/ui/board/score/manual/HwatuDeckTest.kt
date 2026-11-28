package zero.friends.gostopcalculator.ui.board.score.manual

import zero.friends.gostopcalculator.ui.board.score.Loser
import zero.friends.gostopcalculator.ui.board.score.Scoring
import zero.friends.gostopcalculator.ui.board.score.Selling
import zero.friends.gostopcalculator.ui.board.score.Winner
import zero.friends.gostopcalculator.ui.board.score.defaultManualTabForPhase
import kotlin.test.Test
import kotlin.test.assertEquals

class HwatuDeckTest {

    @Test
    fun `monthlySections covers all 12 months with 48 distinct cards`() {
        val sections = HwatuDeck.monthlySections
        assertEquals(12, sections.size)
        assertEquals((1..12).toList(), sections.map { it.month })
        sections.forEach { section ->
            assertEquals(4, section.cards.size)
        }

        val allCards = sections.flatMap { it.cards }
        assertEquals(48, allCards.size)
        assertEquals(48, allCards.map { it.id }.toSet().size)
        assertEquals(48, allCards.map { it.drawable }.toSet().size)

        val roleCounts = allCards.groupingBy { it.role }.eachCount()
        assertEquals(4, roleCounts[HwatuCardRole.GWANG])
        assertEquals(1, roleCounts[HwatuCardRole.BI_GWANG])
        assertEquals(3, roleCounts[HwatuCardRole.GODORI])
        assertEquals(5, roleCounts[HwatuCardRole.YEOL])
        assertEquals(1, roleCounts[HwatuCardRole.YEOL_SSANGPI])
        assertEquals(3, roleCounts[HwatuCardRole.HONGDAN])
        assertEquals(3, roleCounts[HwatuCardRole.CHEONGDAN])
        assertEquals(3, roleCounts[HwatuCardRole.CHODAN])
        assertEquals(1, roleCounts[HwatuCardRole.TTI])
        assertEquals(2, roleCounts[HwatuCardRole.SSANGPI])
        assertEquals(22, roleCounts[HwatuCardRole.PI])
        assertEquals(
            8,
            allCards.count {
                it.role in setOf(
                    HwatuCardRole.GWANG,
                    HwatuCardRole.BI_GWANG,
                    HwatuCardRole.YEOL_SSANGPI,
                    HwatuCardRole.SSANGPI,
                )
            }
        )
    }

    @Test
    fun `score combo pairs preserve full combo lists`() {
        assertEquals(HwatuDeck.gwangScoreCombos, HwatuDeck.gwangScoreComboPairs.flatten())
        assertEquals(HwatuDeck.otherScoreCombos, HwatuDeck.otherScoreComboPairs.flatten())
    }

    @Test
    fun `selling filters contain 5 gwang cards and 4 ssangpi or bonus cards`() {
        assertEquals(5, HwatuDeck.sellingGwangEntries.size)
        assertEquals(1, HwatuDeck.sellingGwangEntries.count { it.isBiGwang })
        assertEquals(4, HwatuDeck.sellingSsangpiEntries.size)
    }

    @Test
    fun `ManualTab fromIndex clamps invalid indices to GameFlow`() {
        assertEquals(ManualTab.GameFlow, ManualTab.fromIndex(-1))
        assertEquals(ManualTab.GameFlow, ManualTab.fromIndex(0))
        assertEquals(ManualTab.CardScores, ManualTab.fromIndex(1))
        assertEquals(ManualTab.GoAndBonus, ManualTab.fromIndex(2))
        assertEquals(ManualTab.Penalties, ManualTab.fromIndex(3))
        assertEquals(ManualTab.MonthlyCards, ManualTab.fromIndex(4))
        assertEquals(ManualTab.GameFlow, ManualTab.fromIndex(99))
    }

    @Test
    fun `score phases map to expected default manual tabs`() {
        assertEquals(ManualTab.MonthlyCards, defaultManualTabForPhase(Selling(enable = false)))
        assertEquals(ManualTab.GoAndBonus, defaultManualTabForPhase(Scoring))
        assertEquals(ManualTab.GoAndBonus, defaultManualTabForPhase(Winner(enable = false)))
        assertEquals(ManualTab.Penalties, defaultManualTabForPhase(Loser))
    }
}
