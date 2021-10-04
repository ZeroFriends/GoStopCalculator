package zero.friends.gostopcalculator.precondition

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.gostopcalculator.R

@Composable
fun PlayerScreen(viewModel: PlayerViewModel = hiltViewModel(), onBack: () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val modifier = Modifier
        .defaultMinSize(60.dp, 60.dp)//todo title Center 방법이 있다면 변경해보자...
        .background(Color.Transparent)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = String.format(stringResource(id = R.string.game_setting_title), 1),
                modifier = modifier,
                onBack = onBack
            )
        }
    ) {
        AprilBackground(
            title = stringResource(id = R.string.player_title),
            subTitle = stringResource(id = R.string.player_description)
        ) {
            TitleOutlinedTextField(
                title = stringResource(id = R.string.group_name),
                hint = viewModel.getCurrentDate()
            )

        }
    }
}

@Composable
private fun CenterTextTopBar(text: String, modifier: Modifier, onBack: () -> Unit) {
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
            Box(modifier = modifier)
        },
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = colorResource(id = R.color.orangey_red),
        contentColor = colorResource(id = R.color.white),
        elevation = 0.dp
    )
}

@Composable
fun AprilBackground(title: String, subTitle: String, contentInvoker: @Composable (BoxScope) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.orangey_red))
    ) {
        ConstraintLayout {
            val (subtitle, content, image) = createRefs()

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
        }
    }
}

@Composable
fun TitleOutlinedTextField(title: String, hint: String) {
    Column(Modifier.padding(top = 44.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.padding(4.dp))
        val text = remember { mutableStateOf(TextFieldValue()) }
        OutlinedTextField(
            value = text.value,
            onValueChange = {
                text.value = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Red,
                focusedBorderColor = Color.Red,
                cursorColor = colorResource(id = R.color.nero)
            ),
            placeholder = { Text(text = hint) }
        )

    }
}

@Preview
@Composable
fun PlayerPreview() {
    PlayerScreen {

    }
}