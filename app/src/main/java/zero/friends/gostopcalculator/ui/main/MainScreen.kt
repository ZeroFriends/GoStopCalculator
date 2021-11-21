package zero.friends.gostopcalculator.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.ui.common.SubTitleText
import zero.friends.gostopcalculator.ui.common.TitleText

private sealed class MainEvent {
    object StartGame : MainEvent()
    object ShowGuide : MainEvent()
    class ShowGame(val game: Game) : MainEvent()
}

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel(), onStartGame: () -> Unit) {
    MainScreen(viewModel.getGameHistory()) { event ->
        when (event) {
            is MainEvent.ShowGame -> {
                //TODO show game dialog
            }
            MainEvent.ShowGuide -> {
                //TODO on Show guide
            }
            MainEvent.StartGame -> {
                onStartGame()
            }
        }
    }
}

@Composable
private fun MainScreen(games: List<Game>, event: (MainEvent) -> Unit = {}) {
    Column {
        NewGame(event)

        Divider(
            color = colorResource(id = R.color.gray),
            thickness = 10.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        History(games, onClick = { event(MainEvent.ShowGame(it)) })
    }
}

@Composable
private fun NewGame(event: (MainEvent) -> Unit) {
    Column(Modifier.padding(16.dp)) {
        SubTitleText("NEW GAME")
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleText(text = stringResource(id = R.string.today_game))
            RoundedCornerText(
                text = stringResource(id = R.string.guide),
                color = colorResource(id = R.color.orangey_red),
                fontSize = 14.sp,
                onButtonClicked = { event(MainEvent.ShowGuide) }
            )
        }
        Spacer(modifier = Modifier.padding(18.dp))
        GoStopButton(stringResource(id = R.string.start), onClick = { event(MainEvent.StartGame) })
    }
}


@Composable
private fun History(games: List<Game>, onClick: (Game) -> Unit) {
    Column(
        Modifier.padding(16.dp)
    ) {
        SubTitleText(text = stringResource(id = R.string.history))
        TitleText(text = stringResource(id = R.string.progress))
        if (games.isEmpty()) {
            EmptyHistory()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(games) { game ->
                    GameLog(game, onClick = { onClick(game) })
                }
            }
        }
    }
}

@Composable
private fun EmptyHistory() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_onodofu),
                contentDescription = stringResource(id = R.string.onodofu),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier.padding(bottom = 9.dp)
            )
            Text(
                text = stringResource(id = R.string.empty_game),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(text = stringResource(id = R.string.info_game_start))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameLogPreview() {
    GameLog(Game("gameTitle", "2021.11.21"))
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GameLog(game: Game, onClick: () -> Unit = {}) {
    Card(
        elevation = 6.dp,
        shape = RoundedCornerShape(18.dp),
        onClick = onClick
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
                    RoundedCornerText(
                        stringResource(R.string.playing),
                        colorResource(id = R.color.gray38),
                        12.sp,
                        null
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
private fun MainPreview() {
    MainScreen(emptyList())
}