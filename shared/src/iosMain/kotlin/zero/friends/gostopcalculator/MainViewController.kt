package zero.friends.gostopcalculator

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import zero.friends.gostopcalculator.navigation.AppNavigator

fun MainViewController(): UIViewController = ComposeUIViewController {
    AppNavigator()
}
