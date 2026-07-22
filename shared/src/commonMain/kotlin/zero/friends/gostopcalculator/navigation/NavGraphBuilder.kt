package zero.friends.gostopcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    val currentRoute = navController.currentRoute
    val destination = graph.destinations[currentRoute::class]
    destination?.invoke(currentRoute)
}
