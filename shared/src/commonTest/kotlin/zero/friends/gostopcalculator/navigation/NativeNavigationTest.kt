package zero.friends.gostopcalculator.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NativeNavigationTest {

    @Test
    fun `defaultNativeTopBarConfig sets red header for precondition routes`() {
        val playerConfig = defaultNativeTopBarConfig(Route.Precondition.Player)
        assertTrue(playerConfig.isRedBackground)
        assertTrue(playerConfig.showBack)
        assertTrue(playerConfig.swipeBackEnabled)
        assertEquals(NativeTopBarActionType.NONE, playerConfig.actionType)

        val ruleConfig = defaultNativeTopBarConfig(Route.Precondition.Rule("[]", "Game"))
        assertTrue(ruleConfig.isRedBackground)
        assertTrue(ruleConfig.showBack)
        assertTrue(ruleConfig.swipeBackEnabled)
    }

    @Test
    fun `defaultNativeTopBarConfig disables swipeBack and adds close icon on Score`() {
        val scoreConfig = defaultNativeTopBarConfig(Route.Board.Score(1L, 2L))
        assertFalse(scoreConfig.isRedBackground)
        assertTrue(scoreConfig.showBack)
        assertFalse(scoreConfig.swipeBackEnabled)
        assertEquals(NativeTopBarActionType.CLOSE_ICON, scoreConfig.actionType)
    }

    @Test
    fun `defaultNativeTopBarConfig sets text action for Detail and Calculate`() {
        assertEquals(
            NativeTopBarActionType.TEXT,
            defaultNativeTopBarConfig(Route.Board.Detail(1L, 2L)).actionType,
        )
        assertEquals(
            NativeTopBarActionType.TEXT,
            defaultNativeTopBarConfig(Route.Board.Calculate(1L)).actionType,
        )
    }

    @Test
    fun `route helper predicates match expected routes`() {
        assertTrue(isHistoryRoute(Route.History))
        assertFalse(isHistoryRoute(Route.Precondition.Player))

        assertTrue(isBoardMainRoute(Route.Board.Main(1L)))
        assertFalse(isBoardMainRoute(Route.Board.Prepare(1L)))

        assertTrue(isSameBoardMainRoute(Route.Board.Main(42L), Route.Board.Main(42L)))
        assertFalse(isSameBoardMainRoute(Route.Board.Main(42L), Route.Board.Main(99L)))
        assertFalse(isSameBoardMainRoute(Route.Board.Main(42L), Route.Board.Prepare(42L)))

        assertTrue(isSameRoute(Route.Board.Prepare(42L), Route.Board.Prepare(42L)))
        assertFalse(isSameRoute(Route.Board.Prepare(42L), Route.Board.Score(42L, 1L)))
        assertEquals(
            Route.Board.Prepare(42L),
            NativeNavCommand(
                route = Route.Board.Score(42L, 1L),
                expectedRoute = Route.Board.Prepare(42L),
            ).expectedRoute,
        )
    }
}
