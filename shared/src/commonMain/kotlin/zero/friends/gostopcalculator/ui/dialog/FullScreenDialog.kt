package zero.friends.gostopcalculator.ui.dialog

import androidx.compose.runtime.Composable

@Composable
expect fun FullScreenDialog(onDismiss: () -> Unit = {}, content: @Composable () -> Unit)
