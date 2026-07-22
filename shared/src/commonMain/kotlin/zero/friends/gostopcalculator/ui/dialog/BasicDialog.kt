package zero.friends.gostopcalculator.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.cancel
import zero.friends.gostopcalculator.ui.common.OrangeyRed

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
        text = text?.let {
            { Text(text = it) }
        },
        confirmButton = {
            Text(
                modifier = Modifier
                    .clickable { onClick() }
                    .padding(10.dp),
                text = confirmText,
                color = OrangeyRed
            )
        },
        dismissButton = {
            Text(
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(10.dp),
                text = stringResource(Res.string.cancel),
                color = OrangeyRed
            )
        }
    )
}