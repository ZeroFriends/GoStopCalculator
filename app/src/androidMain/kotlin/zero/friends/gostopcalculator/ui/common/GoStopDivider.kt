package zero.friends.gostopcalculator.ui.common

import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import zero.friends.gostopcalculator.R

@Composable
fun GoStopDivider(modifier: Modifier = Modifier) {
    Divider(
        color = colorResource(id = R.color.light_gray),
        thickness = 10.dp,
        modifier = modifier
    )
}