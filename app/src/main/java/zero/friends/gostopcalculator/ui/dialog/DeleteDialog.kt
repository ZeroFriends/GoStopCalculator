package zero.friends.gostopcalculator.ui.dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import zero.friends.gostopcalculator.R

@Composable
fun DeleteDialog(
    onDismiss: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.delete_dialog_title)) },
        confirmButton = {
            Button(onClick = onClick) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.gray))
            ) {
                Text(text = stringResource(R.string.cancel), color = colorResource(id = R.color.white))
            }
        }
    )
}