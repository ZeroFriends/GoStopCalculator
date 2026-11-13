package zero.friends.gostopcalculator.ui.common

import androidx.compose.runtime.Composable

@Composable
actual fun SystemBarAppearance(
    isLightStatusBar: Boolean,
    isLightNavigationBar: Boolean,
) {
    // No-op on iOS for now; Android edge-to-edge system bar icon contrast is handled in androidMain.
}
