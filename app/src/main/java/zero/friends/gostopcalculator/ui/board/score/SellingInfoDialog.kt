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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import zero.friends.gostopcalculator.R

@Composable
fun SellingInfoDialog(onClose: () -> Unit = {}) {
    Dialog(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.white))
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.selling_info),
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(13.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth(), onClick = onClose
            ) {
                Text(
                    text = stringResource(id = android.R.string.ok),
                    color = colorResource(id = R.color.white),
                    fontSize = 16.sp
                )
            }
        }
    }
}
