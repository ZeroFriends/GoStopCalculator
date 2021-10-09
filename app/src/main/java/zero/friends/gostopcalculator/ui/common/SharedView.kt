package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
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
    modifier: Modifier = Modifier,
    contentInvoker: @Composable () -> Unit,
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
                    .background(Color.White)
            ) {
                contentInvoker()
            }
        }
    }
}

@Composable
fun GoStopButton(text: String, modifier: Modifier=Modifier, buttonEnabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = colorResource(id = if (buttonEnabled) R.color.orangey_red else R.color.gray)
        ),
        shape = RoundedCornerShape(22.dp),
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
fun GoStopOutLinedTextField(
    inputText: MutableState<TextFieldValue>,
    onValueChange: (TextFieldValue) -> Unit,
    hint: String,
    color: Color = colorResource(id = R.color.gray),
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = inputText.value,
        onValueChange = {
            onValueChange.invoke(it)
        },
        modifier = modifier
            .fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = color,
            focusedBorderColor = color,
            cursorColor = colorResource(id = R.color.nero)
        ),
        placeholder = { Text(text = hint, color = color) },
        textStyle = TextStyle(fontSize = 16.sp)
    )
}

@Composable
fun NameEditDialog(openDialog: MutableState<Boolean>) {
    val inputText = remember {
        mutableStateOf(TextFieldValue("새 플레이어 %d"))
    }

    AlertDialog(
        onDismissRequest = { openDialog.value = false },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "이름 수정하기",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.nero),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(bottom = 40.dp))
                GoStopOutLinedTextField(
                    inputText = inputText,
                    onValueChange = { inputText.value = it },
                    hint = "",
                    color = colorResource(id = R.color.nero)
                )
            }

        },
        confirmButton = {
            GoStopButton("수정하기", modifier = Modifier) {/*todo confirm*/ }
        },
        shape = RoundedCornerShape(16.dp),
    )
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