package zero.friends.gostopcalculator.ui.common

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

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