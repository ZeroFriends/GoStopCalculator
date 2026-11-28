package zero.friends.gostopcalculator.ui.dialog

import android.graphics.Color
import android.os.Build
import android.view.ViewParent
import android.view.WindowManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import zero.friends.gostopcalculator.ui.common.enableTestTagsAsResourceId

@Composable
actual fun FullScreenDialog(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
        content = {
            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    var parent: ViewParent? = view.parent
                    while (parent != null && parent !is DialogWindowProvider) {
                        parent = parent.parent
                    }
                    val window = (parent as? DialogWindowProvider)?.window ?: return@SideEffect
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    window.attributes = window.attributes.apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            layoutInDisplayCutoutMode =
                                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            layoutInDisplayCutoutMode =
                                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                        }
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                        @Suppress("DEPRECATION")
                        window.statusBarColor = Color.TRANSPARENT
                        @Suppress("DEPRECATION")
                        window.navigationBarColor = Color.TRANSPARENT
                    }
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .enableTestTagsAsResourceId()
            ) {
                content()
            }
        }
    )
}
