package zero.friends.gostopcalculator.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import zero.friends.gostopcalculator.navigation.LocalUseNativeNavigation

@Composable
actual fun SystemBarAppearance(
    isLightStatusBar: Boolean,
    isLightNavigationBar: Boolean,
) {
    if (LocalUseNativeNavigation.current) return

    SideEffect {
        val targetStyle = if (isLightStatusBar) {
            UIUserInterfaceStyle.UIUserInterfaceStyleLight
        } else {
            UIUserInterfaceStyle.UIUserInterfaceStyleDark
        }

        val windows = UIApplication.sharedApplication.connectedScenes
            .mapNotNull { it as? UIWindowScene }
            .flatMap { scene -> scene.windows.mapNotNull { it as? UIWindow } }

        if (windows.isNotEmpty()) {
            windows.forEach { window ->
                window.overrideUserInterfaceStyle = targetStyle
                window.rootViewController?.setNeedsStatusBarAppearanceUpdate()
            }
        } else {
            UIApplication.sharedApplication.keyWindow?.let { keyWindow ->
                keyWindow.overrideUserInterfaceStyle = targetStyle
                keyWindow.rootViewController?.setNeedsStatusBarAppearanceUpdate()
            }
        }
    }
}
