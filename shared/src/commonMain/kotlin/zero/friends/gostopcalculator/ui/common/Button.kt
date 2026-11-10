package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val OrangeyRed = Color(0xFFFF5252)
val Gray = Color(0xFF9E9E9E)
val Nero = Color(0xFF212121)

@Composable
fun GoStopButton(text: String, modifier: Modifier = Modifier, buttonEnabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = if (buttonEnabled) OrangeyRed else Gray
        ),
        shape = RoundedCornerShape(100.dp),
        enabled = buttonEnabled,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}

@Composable
fun GoStopExtraButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, Nero),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Nero
        )
    }
}