package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.gostopcalculator.R

@Composable
fun TitleText(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
}

@Composable
fun SubTitleText(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = colorResource(id = R.color.dark_gray)
    )
}

@Preview
@Composable
fun TitleTextPreview() {
    TitleText(text = "ZeroWorld!!")
}

@Preview
@Composable
fun SubTitleTextPreview() {
    SubTitleText(text = "ZeroWorld!!")
}

@Composable
fun RoundedCornerText(
    text: String,
    color: Color = colorResource(id = R.color.orangey_red),
    fontSize: TextUnit = 14.sp,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape((12.5).dp),
        border = BorderStroke(
            width = 1.dp,
            color = color
        ),
        modifier = Modifier.clickable(onClick = onClick ?: {}, enabled = onClick != null),
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
            text = text,
            fontSize = fontSize,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
@Preview
private fun RoundedCornerTextPreview() {
    RoundedCornerText(text = "text", color = colorResource(id = R.color.orangey_red), 14.sp)
}