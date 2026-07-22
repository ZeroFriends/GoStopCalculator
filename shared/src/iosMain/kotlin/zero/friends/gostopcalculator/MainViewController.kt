package zero.friends.gostopcalculator

import androidx.compose.ui.window.ComposeUIViewController
import zero.friends.gostopcalculator.navigation.AppNavigator

fun MainViewController() = ComposeUIViewController(
    configure = { enforceStrictPlistSanityCheck = false }
) {
    AppNavigator()
}

