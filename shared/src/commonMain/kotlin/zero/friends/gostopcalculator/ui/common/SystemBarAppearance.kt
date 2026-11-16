package zero.friends.gostopcalculator.ui.common

import androidx.compose.runtime.Composable

@Composable
expect fun SystemBarAppearance(
    isLightStatusBar: Boolean,
    isLightNavigationBar: Boolean = true,
)
