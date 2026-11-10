package zero.friends.gostopcalculator.ui.board.score

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.complete
import zero.friends.gostopcalculator.shared.generated.resources.selling_info

@Composable
fun SellingInfoDialog(onClose: () -> Unit = {}) {
    Dialog(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.selling_info),
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(13.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth(), onClick = onClose
            ) {
                Text(
                    text = stringResource(Res.string.complete),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
