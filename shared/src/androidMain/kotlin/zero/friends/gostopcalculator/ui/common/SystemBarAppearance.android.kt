package zero.friends.gostopcalculator.ui.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewParent
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

@Composable
actual fun SystemBarAppearance(
    isLightStatusBar: Boolean,
    isLightNavigationBar: Boolean,
) {
    val view = LocalView.current
    if (view.isInEditMode) return

    SideEffect {
        val window = view.findWindow() ?: return@SideEffect
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = isLightStatusBar
        insetsController.isAppearanceLightNavigationBars = isLightNavigationBar
    }
}

private fun View.findWindow(): Window? {
    var currentParent: ViewParent? = parent
    while (currentParent != null) {
        if (currentParent is DialogWindowProvider) {
            return currentParent.window
        }
        currentParent = currentParent.parent
    }
    return context.findActivity()?.window
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
