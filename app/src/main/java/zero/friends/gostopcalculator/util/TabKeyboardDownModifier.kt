package zero.friends.gostopcalculator.util

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun TabKeyboardDownModifier(): Modifier {
    val keyboardController = LocalSoftwareKeyboardController.current
    val tabKeyboardDownModifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {
            keyboardController?.hide()
        })
    }
    return tabKeyboardDownModifier
}