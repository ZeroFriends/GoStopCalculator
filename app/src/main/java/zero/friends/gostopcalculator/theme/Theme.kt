package zero.friends.gostopcalculator.theme

import android.app.Activity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ColorPalette = lightColors(
    surface = Color.White,
    primary = GoStopColor,
)

@Composable
fun GoStopTheme(content: @Composable () -> Unit) {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window
        // 상태바 아이콘 색상만 설정 (색상은 테마에서 처리)
        WindowCompat.getInsetsController(window, view)?.apply {
            isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colors = ColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
