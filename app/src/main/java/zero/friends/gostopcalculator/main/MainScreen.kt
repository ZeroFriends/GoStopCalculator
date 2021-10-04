package zero.friends.gostopcalculator.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.model.Game

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel(), onStartGame: () -> Unit, onShowGuide: () -> Unit) {
    val modifier = Modifier
    Column {
        NewGame(
            modifier = modifier,
            onStartGame = { onStartGame() },
            onShowGuide = { onShowGuide() }
        )

        Divider(
            color = colorResource(id = R.color.gray),
            thickness = 10.dp,
            modifier = modifier.padding(vertical = 16.dp)
        )

        History(modifier, listOf())
    }
}

@Composable
fun NewGame(modifier: Modifier, onStartGame: () -> Unit, onShowGuide: () -> Unit) {
    Column(modifier.padding(16.dp)) {
        SubTitleText("NEW GAME")
        Spacer(modifier = modifier.height(4.dp))
        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TitleText(text = stringResource(id = R.string.today_game))
            GoStopOutLineButton(stringResource(id = R.string.guide), onShowGuide)
        }
        Spacer(modifier = modifier.padding(18.dp))
        Button(
            onClick = { onStartGame() },
            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.orangey_red)),
            shape = RoundedCornerShape(100.dp),
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.start),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
fun GoStopOutLineButton(text: String, onButtonClicked: () -> Unit) {
    Button(
        onClick = { onButtonClicked() },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
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
fun History(modifier: Modifier, games: List<Game>) {
    Column(
        modifier.padding(16.dp)
    ) {
        SubTitleText(text = stringResource(id = R.string.history))
        TitleText(text = stringResource(id = R.string.progress))
        if (games.isEmpty()) {
            EmptyHistory(modifier)
        } else {
            LazyColumn {
                items(games) { game ->
                    Text(text = game.title)
                }
            }
        }
    }
}

@Composable
fun TitleText(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
}

@Composable
private fun SubTitleText(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun EmptyHistory(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_onodofu),
                contentDescription = stringResource(id = R.string.onodofu),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = modifier.padding(bottom = 9.dp)
            )
            Text(text = stringResource(id = R.string.empty_game),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(bottom = 2.dp))
            Text(text = stringResource(id = R.string.info_game_start))
        }
    }
}


@Preview("MainPreview")
@Composable
fun MainPreview() {
    MainScreen(MainViewModel(SavedStateHandle()), {}, {})
}