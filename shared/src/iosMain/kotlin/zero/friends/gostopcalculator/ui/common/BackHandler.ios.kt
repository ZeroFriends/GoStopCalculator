package zero.friends.gostopcalculator.ui.common

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS Compose Multiplatform has native swipe-back handling or platform back navigation.
    // If enabled, onBack can be bound to custom iOS back events if needed.
}
