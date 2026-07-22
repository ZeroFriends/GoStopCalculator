package zero.friends.gostopcalculator.ui.common

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val LightGray = Color(0xFFF5F5F5)

@Composable
fun GoStopDivider(modifier: Modifier = Modifier) {
    Divider(
        color = LightGray,
        thickness = 10.dp,
        modifier = modifier
    )
}