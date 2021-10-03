package zero.friends.gostopcalculator.precondition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
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
        .defaultMinSize(60.dp, 60.dp)
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
                contentColor = colorResource(id = R.color.white)
            )
        }
    ) {

    }
}

@Preview
@Composable
fun PlayerPreview() {
    Player {

    }
}