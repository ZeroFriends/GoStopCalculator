package zero.friends.gostopcalculator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import zero.friends.gostopcalculator.di.AppComponent
import zero.friends.gostopcalculator.di.createAppComponent
import zero.friends.gostopcalculator.navigation.LocalNativeTopBarHeight
import zero.friends.gostopcalculator.navigation.LocalNativeTopBarUpdater
import zero.friends.gostopcalculator.navigation.LocalUseNativeNavigation
import zero.friends.gostopcalculator.navigation.NativeNavCommand
import zero.friends.gostopcalculator.navigation.NativePopTarget
import zero.friends.gostopcalculator.navigation.NativeTopBarConfig
import zero.friends.gostopcalculator.navigation.Route
import zero.friends.gostopcalculator.navigation.RouteScreenContent
import zero.friends.gostopcalculator.theme.GoStopTheme
import zero.friends.gostopcalculator.navigation.AppNavigator

private val sharedAppComponent: AppComponent by lazy { createAppComponent() }

class NativeInsetController {
    internal var topInsetPt by mutableStateOf(0f)

    fun updateTopInset(points: Float) {
        if (topInsetPt != points) {
            topInsetPt = points
        }
    }
}

@Suppress("unused")
fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = {
        enforceStrictPlistSanityCheck = false
        onFocusBehavior = OnFocusBehavior.DoNothing
    }
) {
    AppNavigator(appComponent = sharedAppComponent)
}

@Suppress("unused")
fun RootViewController(
    onNavigate: (NativeNavCommand) -> Unit,
): UIViewController = ComposeUIViewController(
    configure = {
        enforceStrictPlistSanityCheck = false
        onFocusBehavior = OnFocusBehavior.DoNothing
    }
) {
    AppNavigator(
        appComponent = sharedAppComponent,
        onNativeNavigate = onNavigate,
    )
}

@Suppress("unused")
fun ScreenViewController(
    route: Route,
    insetController: NativeInsetController,
    onNavigate: (NativeNavCommand) -> Unit,
    onGoBack: () -> Unit,
    onUpdateTopBar: (NativeTopBarConfig) -> Unit,
): UIViewController = ComposeUIViewController(
    configure = {
        enforceStrictPlistSanityCheck = false
        onFocusBehavior = OnFocusBehavior.DoNothing
    }
) {
    SingleScreenApp(
        route = route,
        appComponent = sharedAppComponent,
        insetController = insetController,
        onNavigate = { targetRoute, popTarget, inclusive ->
            onNavigate(
                NativeNavCommand(
                    route = targetRoute,
                    popTarget = popTarget,
                    popInclusive = inclusive,
                )
            )
        },
        onGoBack = onGoBack,
        onUpdateTopBar = onUpdateTopBar,
    )
}

@Composable
private fun SingleScreenApp(
    route: Route,
    appComponent: AppComponent,
    insetController: NativeInsetController,
    onNavigate: (Route, NativePopTarget, Boolean) -> Unit,
    onGoBack: () -> Unit,
    onUpdateTopBar: (NativeTopBarConfig) -> Unit,
) {
    CompositionLocalProvider(
        LocalUseNativeNavigation provides true,
        LocalNativeTopBarHeight provides insetController.topInsetPt.dp,
        LocalNativeTopBarUpdater provides onUpdateTopBar,
    ) {
        GoStopTheme {
            RouteScreenContent(
                route = route,
                appComponent = appComponent,
                onNavigate = onNavigate,
                onBack = onGoBack,
            )
        }
    }
}
