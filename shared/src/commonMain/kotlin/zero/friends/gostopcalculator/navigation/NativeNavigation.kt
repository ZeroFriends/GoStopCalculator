package zero.friends.gostopcalculator.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

val LocalUseNativeNavigation = staticCompositionLocalOf { false }

val LocalNativeTopBarHeight = compositionLocalOf { 0.dp }

val LocalNativeTopBarUpdater = staticCompositionLocalOf<(NativeTopBarConfig) -> Unit> { {} }

enum class NativePopTarget {
    NONE,
    HISTORY,
    BOARD_MAIN,
}

enum class NativeTopBarActionType {
    NONE,
    CLOSE_ICON,
    TEXT,
}

data class NativeNavCommand(
    val route: Route,
    val popTarget: NativePopTarget = NativePopTarget.NONE,
    val popInclusive: Boolean = false,
)

data class NativeTopBarConfig(
    val title: String = "",
    val isRedBackground: Boolean = false,
    val showBack: Boolean = true,
    val swipeBackEnabled: Boolean = true,
    val actionType: NativeTopBarActionType = NativeTopBarActionType.NONE,
    val actionText: String? = null,
    val onBack: (() -> Unit)? = null,
    val onAction: (() -> Unit)? = null,
) {
    fun invokeBack() {
        onBack?.invoke()
    }

    fun invokeAction() {
        onAction?.invoke()
    }
}

fun defaultNativeTopBarConfig(route: Route): NativeTopBarConfig = when (route) {
    is Route.Precondition.Player,
    is Route.Precondition.Rule -> NativeTopBarConfig(
        isRedBackground = true,
        showBack = true,
        swipeBackEnabled = true,
    )

    is Route.Board.Score -> NativeTopBarConfig(
        isRedBackground = false,
        showBack = true,
        swipeBackEnabled = false,
        actionType = NativeTopBarActionType.CLOSE_ICON,
    )

    is Route.Board.End -> NativeTopBarConfig(
        isRedBackground = false,
        showBack = false,
        swipeBackEnabled = true,
    )

    is Route.Board.Detail,
    is Route.Board.Calculate -> NativeTopBarConfig(
        isRedBackground = false,
        showBack = true,
        swipeBackEnabled = true,
        actionType = NativeTopBarActionType.TEXT,
    )

    else -> NativeTopBarConfig(
        isRedBackground = false,
        showBack = true,
        swipeBackEnabled = true,
    )
}

fun isHistoryRoute(route: Route): Boolean = route is Route.History

fun isBoardMainRoute(route: Route): Boolean = route is Route.Board.Main

fun isSameBoardMainRoute(first: Route, second: Route): Boolean =
    first is Route.Board.Main && second is Route.Board.Main && first.gameId == second.gameId
