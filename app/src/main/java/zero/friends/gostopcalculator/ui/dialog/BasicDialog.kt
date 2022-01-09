package zero.friends.gostopcalculator.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import zero.friends.gostopcalculator.R

@Composable
fun BasicDialog(
    onDismiss: () -> Unit = {},
    onClick: () -> Unit = {},
    confirmText: String,
    titleText: String,
    text: String? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = titleText) },
        text = { if (text != null) Text(text = text) },
        confirmButton = {
            Text(
                modifier = Modifier
                    .clickable { onClick() }
                    .padding(10.dp),
                text = confirmText,
                color = colorResource(id = R.color.orangey_red)
            )
        },
        dismissButton = {
            Text(
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(10.dp),
                text = stringResource(R.string.cancel),
                color = colorResource(id = R.color.orangey_red)
            )
        }
    )
}