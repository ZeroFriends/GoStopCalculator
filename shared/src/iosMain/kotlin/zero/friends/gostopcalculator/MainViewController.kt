package zero.friends.gostopcalculator

import androidx.compose.ui.window.ComposeUIViewController
import zero.friends.gostopcalculator.ui.splash.SplashScreen

fun MainViewController() = ComposeUIViewController(
    configure = { enforceStrictPlistSanityCheck = false }
) {
    SplashScreen()
}
