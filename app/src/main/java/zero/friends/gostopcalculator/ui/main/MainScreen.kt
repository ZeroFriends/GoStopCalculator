package zero.friends.gostopcalculator.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.model.Game
import zero.friends.gostopcalculator.ui.common.GoStopButton
import zero.friends.gostopcalculator.ui.common.SubActionOutLineButton
import zero.friends.gostopcalculator.ui.common.SubTitleText
import zero.friends.gostopcalculator.ui.common.TitleText

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

        History(modifier, listOf(Game("hello", "2021.11.21"), Game("world", "2021.11.21")))
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
            SubActionOutLineButton(
                stringResource(id = R.string.guide),
                colorResource(id = R.color.orangey_red),
                14.sp,
                onShowGuide
            )
        }
        Spacer(modifier = modifier.padding(18.dp))
        GoStopButton(stringResource(id = R.string.start), modifier, onClick = onStartGame)
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(games) { game ->
                    GameLog(game) {}
                }
            }
        }
    }
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
            Text(
                text = stringResource(id = R.string.empty_game),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(bottom = 2.dp)
            )
            Text(text = stringResource(id = R.string.info_game_start))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameLogPreview() {
    GameLog(Game("gameTitle", "2021.11.21"))
}

@Composable
fun GameLog(game: Game, onClick: () -> Unit = {}) {
    Card(
        elevation = 6.dp,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 15.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Row {
                    Text(
                        text = stringResource(R.string.created_at),
                        color = colorResource(id = R.color.gray),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(text = game.createdAt, color = colorResource(id = R.color.gray), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = game.title,
                        color = colorResource(id = R.color.nero),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    SubActionOutLineButton(
                        stringResource(R.string.playing),
                        colorResource(id = R.color.gray38),
                        12.sp,
                        onClick
                    )
                }

            }
            Icon(
                painter = painterResource(id = R.drawable.ic_more_black),
                contentDescription = null
            )
        }
    }
}


@Preview("MainPreview", showBackground = true)
@Composable
fun MainPreview() {
    MainScreen(MainViewModel(), {}, {})
}