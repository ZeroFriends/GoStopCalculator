package zero.friends.gostopcalculator.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class NavControllerTest {

    @Test
    fun `navigate and popBackStack manage backStack entries cleanly`() {
        val navController = NavController(Route.History)
        val rootId = navController.currentEntry.id

        assertFalse(navController.popBackStack())
        assertEquals(listOf(rootId), navController.backStackIds)

        navController.navigate(Route.Precondition.Player)
        assertEquals(Route.Precondition.Player, navController.currentRoute)
        assertEquals(2, navController.backStackIds.size)

        assertTrue(navController.popBackStack())
        assertEquals(Route.History, navController.currentRoute)
        assertEquals(listOf(rootId), navController.backStackIds)
    }

    @Test
    fun `popUpTo inclusive true replaces target and clears intermediate entries`() {
        val navController = NavController(Route.History)
        navController.navigate(Route.Board.Main(1L))
        val firstBoardEntryId = navController.currentEntry.id

        navController.navigate(Route.Board.Prepare(1L))
        navController.navigate(Route.Board.Score(1L, 10L))
        assertEquals(4, navController.backStackIds.size)

        navController.navigate(Route.Board.Main(1L)) {
            popUpTo<Route.Board.Main> { inclusive = true }
        }

        assertEquals(2, navController.backStackIds.size)
        assertEquals(Route.Board.Main(1L), navController.currentRoute)
        assertNotEquals(firstBoardEntryId, navController.currentEntry.id)
    }

    @Test
    fun `popUpTo inclusive false keeps target entry and drops entries above it`() {
        val navController = NavController(Route.History)
        val historyId = navController.currentEntry.id

        navController.navigate(Route.Precondition.Player)
        navController.navigate(Route.Precondition.Rule("[]", "Test"))

        navController.navigate(Route.Board.Main(7L)) {
            popUpTo<Route.History> { inclusive = false }
        }

        assertEquals(2, navController.backStackIds.size)
        assertEquals(historyId, navController.backStackIds.first())
        assertEquals(Route.Board.Main(7L), navController.currentRoute)
    }

    @Test
    fun `launchSingleTop reuses equal route and replaces distinct route`() {
        val navController = NavController(Route.History)
        navController.navigate(Route.Board.Main(1L))
        val mainOneId = navController.currentEntry.id

        navController.navigate(Route.Board.Main(1L)) {
            launchSingleTop = true
        }
        assertEquals(mainOneId, navController.currentEntry.id)

        navController.navigate(Route.Board.Main(2L)) {
            launchSingleTop = true
        }
        assertNotEquals(mainOneId, navController.currentEntry.id)
        assertEquals(Route.Board.Main(2L), navController.currentRoute)
    }

    @Test
    fun `native delegate handles non-splash navigation and popBackStack`() {
        val commands = mutableListOf<NativeNavCommand>()
        var popCount = 0
        val navController = NavController(
            startDestination = Route.Splash,
            onNativeNavigate = { commands += it },
            onNativePop = { popCount++ },
        )

        // Splash -> History stays in Compose backStack so RootHostViewController transitions cleanly
        navController.navigate(Route.History) {
            popUpTo<Route.Splash> { inclusive = true }
        }
        assertTrue(commands.isEmpty())
        assertEquals(Route.History, navController.currentRoute)

        navController.navigate(Route.Board.Main(5L)) {
            popUpTo<Route.History> { inclusive = false }
        }
        assertEquals(
            listOf(
                NativeNavCommand(
                    route = Route.Board.Main(5L),
                    popTarget = NativePopTarget.HISTORY,
                    popInclusive = false,
                )
            ),
            commands,
        )

        navController.navigate(Route.Board.Score(5L, 9L)) {
            popUpTo<Route.Board.Main> { inclusive = false }
        }
        assertEquals(
            NativeNavCommand(
                route = Route.Board.Score(5L, 9L),
                popTarget = NativePopTarget.BOARD_MAIN,
                popInclusive = false,
            ),
            commands.last(),
        )

        assertTrue(navController.popBackStack())
        assertEquals(1, popCount)
    }

    @Test
    fun `clear empties internal backStack`() {
        val navController = NavController(Route.History)
        navController.navigate(Route.Precondition.Player)
        assertEquals(2, navController.backStackIds.size)

        navController.clear()
        assertTrue(navController.backStackIds.isEmpty())
    }
}
