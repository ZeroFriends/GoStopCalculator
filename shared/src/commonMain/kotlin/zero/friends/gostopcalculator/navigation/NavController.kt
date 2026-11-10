package zero.friends.gostopcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.reflect.KClass

class NavController(startDestination: Route) {
    private val _backStack = mutableListOf<Route>(startDestination)
    var currentRoute by mutableStateOf<Route>(startDestination)
        private set

    fun navigate(route: Route, builder: (NavOptionsBuilder.() -> Unit)? = null) {
        val options = NavOptionsBuilder().apply { builder?.invoke(this) }
        if (options.popUpToRoute != null) {
            val popIndex = _backStack.indexOfLast { it::class == options.popUpToRoute }
            if (popIndex != -1) {
                val removeTo = if (options.inclusive) popIndex else popIndex + 1
                while (_backStack.size > removeTo) {
                    _backStack.removeAt(_backStack.lastIndex)
                }
            }
        }
        if (options.launchSingleTop && _backStack.lastOrNull()?.let { it::class == route::class } == true) {
            _backStack[_backStack.lastIndex] = route
        } else {
            _backStack.add(route)
        }
        currentRoute = _backStack.last()
    }

    fun popBackStack(): Boolean {
        if (_backStack.size > 1) {
            _backStack.removeAt(_backStack.lastIndex)
            currentRoute = _backStack.last()
            return true
        }
        return false
    }
}

class NavOptionsBuilder {
    var launchSingleTop: Boolean = false
    var popUpToRoute: KClass<out Route>? = null
    var inclusive: Boolean = false

    inline fun <reified T : Route> popUpTo(noinline builder: (PopUpToBuilder.() -> Unit)? = null) {
        popUpToRoute = T::class
        val p = PopUpToBuilder().apply { builder?.invoke(this) }
        inclusive = p.inclusive
    }
}

class PopUpToBuilder {
    var inclusive: Boolean = false
}

@Composable
fun rememberNavController(startDestination: Route): NavController {
    return remember(startDestination) { NavController(startDestination) }
}
