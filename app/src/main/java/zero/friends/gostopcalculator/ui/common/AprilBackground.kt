package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import zero.friends.gostopcalculator.R

@Composable
@Preview
private fun AprilBackPreview() {
    AprilBackground("MainTitle", "SubTitle", buttonText = "다음", onClick = {}) {}
}

@Composable
fun AprilBackground(
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier,
    buttonText: String,
    buttonEnabled: Boolean = false,
    onClick: () -> Unit,
    contents: @Composable () -> Unit,
) {
    Surface(modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.ic_april),
            contentDescription = null,
            alignment = Alignment.TopEnd,
            contentScale = ContentScale.Inside,
            modifier = Modifier.background(colorResource(id = R.color.orangey_red))
        )

        Column(Modifier.padding(top = 30.dp)) {
            val textModifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
            Text(
                title,
                fontSize = 24.sp,
                color = colorResource(id = R.color.white),
                modifier = textModifier
            )
            Text(
                subTitle,
                fontSize = 14.sp,
                color = colorResource(id = R.color.white),
                modifier = textModifier.padding(bottom = 26.dp)
            )
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 40.dp, horizontal = 16.dp),
                ) {
                    val (upper, button) = createRefs()
                    Surface(
                        modifier = Modifier
                            .constrainAs(upper) {
                                bottom.linkTo(button.top)
                                top.linkTo(parent.top)
                            }
                            .fillMaxHeight()
                    ) {
                        contents()
                    }
                    GoStopButton(
                        text = buttonText,
                        buttonEnabled = buttonEnabled,
                        modifier = Modifier
                            .constrainAs(button) {
                                top.linkTo(upper.bottom)
                                bottom.linkTo(parent.bottom)
                            },
                        onClick = onClick
                    )
                }
            }
        }
    }
}