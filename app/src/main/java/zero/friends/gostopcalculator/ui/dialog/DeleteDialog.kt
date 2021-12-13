package zero.friends.gostopcalculator.ui.dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun DeleteDialog(
    onDismiss: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "삭제하시겠습니까?") },
        confirmButton = {
            Button(onClick = onClick) {
                Text(text = "삭제")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "취소")
            }
        }
    )
}