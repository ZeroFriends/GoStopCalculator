package zero.friends.gostopcalculator.ui.dialog

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun FullScreenDialog(onDismiss: () -> Unit = {}, content: @Composable () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        content = {
            Surface(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    )
}