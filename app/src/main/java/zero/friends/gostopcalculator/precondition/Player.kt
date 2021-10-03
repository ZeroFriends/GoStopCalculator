package zero.friends.gostopcalculator.precondition

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.gostopcalculator.R

@Composable
fun Player(onBack: () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val modifier = Modifier
        .defaultMinSize(60.dp, 60.dp)//todo title Center 방법이 있다면 변경해보자...
        .background(Color.Transparent)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = String.format(stringResource(id = R.string.game_setting_title), 1),
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
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.orangey_red))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_april),
                contentDescription = null,
                alignment = Alignment.CenterEnd,
                modifier = Modifier.align(Alignment.TopEnd)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(id = R.string.player_title),
                    fontSize = 24.sp,
                    color = colorResource(id = R.color.white))
                Spacer(modifier = Modifier.padding(8.dp))
                Text(stringResource(id = R.string.player_description),
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.white))
            }

        }
    }
}

@Preview
@Composable
fun PlayerPreview() {
    Player {

    }
}