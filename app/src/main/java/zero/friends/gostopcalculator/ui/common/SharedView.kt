package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.gostopcalculator.R


@Composable
fun CenterTextTopBar(text: String, onBack: () -> Unit = {}, onAction: (() -> Unit)? = null) {
    val modifier = Modifier
        .defaultMinSize(60.dp, 60.dp)//todo title Center 방법이 있다면 변경해보자...
        .background(Color.Transparent)

    TopAppBar(
        title = {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )
        },
        navigationIcon = {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_topbar_back),
                        contentDescription = "Back",
                        tint = colorResource(id = R.color.white)
                    )
                }
            }
        },
        actions = {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                if (onAction != null) {
                    IconButton(onClick = onAction) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_topbar_close),
                            contentDescription = "Close",
                            tint = colorResource(id = R.color.white)
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = colorResource(id = R.color.orangey_red),
        contentColor = colorResource(id = R.color.white),
        elevation = 0.dp
    )
}

@Preview
@Composable
private fun CenterTextTopBarPreview() {
    CenterTextTopBar("hello", {}, {})
}


