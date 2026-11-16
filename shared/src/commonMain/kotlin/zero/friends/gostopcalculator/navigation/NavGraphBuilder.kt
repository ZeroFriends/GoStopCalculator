package zero.friends.gostopcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import kotlin.reflect.KClass

class NavGraphBuilder {
    val destinations = mutableMapOf<KClass<out Route>, @Composable (Route) -> Unit>()

    inline fun <reified T : Route> composable(noinline content: @Composable (T) -> Unit) {
        destinations[T::class] = { route -> content(route as T) }
    }
}

@Composable
fun NavHost(
    navController: NavController,
    builder: NavGraphBuilder.() -> Unit
) {
    val graph = remember(builder) { NavGraphBuilder().apply(builder) }
    val saveableStateHolder = rememberSaveableStateHolder()
    val knownEntryIds = remember(navController) { mutableSetOf<String>() }

    DisposableEffect(navController) {
        onDispose {
            navController.clear()
        }
    }

    val currentEntry = navController.currentEntry
    val activeIds = navController.backStackIds

    SideEffect {
        val activeSet = activeIds.toSet()
        val removed = knownEntryIds.filter { it !in activeSet }
        removed.forEach { saveableStateHolder.removeState(it) }
        knownEntryIds.retainAll(activeSet)
        knownEntryIds.add(currentEntry.id)
    }

    val destination = graph.destinations[currentEntry.route::class]
    key(currentEntry.id) {
        saveableStateHolder.SaveableStateProvider(currentEntry.id) {
            CompositionLocalProvider(LocalViewModelStoreOwner provides currentEntry) {
                destination?.invoke(currentEntry.route)
            }
        }
    }
}
