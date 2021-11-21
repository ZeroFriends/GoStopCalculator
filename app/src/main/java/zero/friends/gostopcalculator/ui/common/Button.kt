package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.gostopcalculator.R

@Composable
fun SubActionOutLineButton(
    text: String,
    color: Color = colorResource(id = R.color.orangey_red),
    fontSize: TextUnit = 14.sp,
    onButtonClicked: () -> Unit
) {
    Button(
        onClick = { onButtonClicked() },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        border = BorderStroke(1.dp, color),
        shape = RoundedCornerShape(15.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun GoStopButton(text: String, modifier: Modifier = Modifier, buttonEnabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = colorResource(id = if (buttonEnabled) R.color.orangey_red else R.color.gray)
        ),
        shape = RoundedCornerShape(100.dp),
        enabled = buttonEnabled,
        modifier = modifier
            .fillMaxWidth()
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
fun GoStopExtraButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, colorResource(id = R.color.nero)),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = colorResource(id = R.color.nero)
        )
    }
}

@Composable
@Preview
fun SubActionOutLineButtonPreView() {
    SubActionOutLineButton(text = "text", color = colorResource(id = R.color.orangey_red), 14.sp) {

    }
}

@Composable
@Preview
fun GoStopButtonPreView() {
    GoStopButton(text = "ZeroWorld!!") {

    }
}

@Composable
@Preview
fun GoStopExtraButtonPreView() {
    GoStopExtraButton(text = "ZeroWorld!!") {

    }
}