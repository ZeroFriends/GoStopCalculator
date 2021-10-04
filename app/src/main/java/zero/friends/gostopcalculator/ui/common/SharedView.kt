package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
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
    )
}

@Composable
fun SubActionOutLineButton(text: String, onButtonClicked: () -> Unit) {
    Button(
        onClick = { onButtonClicked() },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(id = R.color.orangey_red)),
        border = BorderStroke(1.dp, colorResource(id = R.color.orangey_red)),
        shape = RoundedCornerShape((12.5).dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = colorResource(id = R.color.orangey_red),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun CenterTextTopBar(text: String, onBack: () -> Unit, onAction: (() -> Unit)?) {
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

@Composable
fun AprilBackground(
    title: String,
    subTitle: String,
    onClickNextButton: () -> Unit,
    contentInvoker: @Composable (BoxScope) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.orangey_red))
    ) {
        ConstraintLayout {
            val (subtitle, content, image, button) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.ic_april),
                contentDescription = null,
                alignment = Alignment.CenterEnd,
                modifier = Modifier.constrainAs(image) { end.linkTo(parent.end) }
            )

            Column(
                modifier = Modifier
                    .constrainAs(subtitle) { top.linkTo(parent.top) }
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    title,
                    fontSize = 24.sp,
                    color = colorResource(id = R.color.white)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    subTitle,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.white),
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .constrainAs(content) { top.linkTo(subtitle.bottom, margin = 10.dp) }
                    .background(Color.White)
                    .fillMaxSize(),
            ) {
                contentInvoker(this)
            }

            OutlinedButton(
                onClick = { onClickNextButton() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(button) { bottom.linkTo(parent.bottom) },
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = colorResource(id = R.color.gray)
                ),
                shape = RoundedCornerShape(22.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.next),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}