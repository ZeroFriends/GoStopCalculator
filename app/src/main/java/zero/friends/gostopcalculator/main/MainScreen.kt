package zero.friends.gostopcalculator.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import timber.log.Timber
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.model.Game

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    Column(Modifier.padding(16.dp)) {
        NewGame(
            onStartGame = { Timber.tag("zero1").d("onStartGame") },
            onShowGuide = { Timber.tag("zero1").d("onShowGuide") }
        )

        Spacer(modifier = Modifier
            .fillMaxWidth()
            .width(10.dp)
            .background(Color.Red)
        )

        History(listOf())
    }
}

@Composable
fun NewGame(onStartGame: () -> Unit, onShowGuide: () -> Unit) {
    Column {
        SubTitleText("NEW GAME")
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TitleText(text = "오늘의 게임 👊")
            Button(
                onClick = { onShowGuide() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = BorderStroke(1.dp, Color.Red),
                shape = RoundedCornerShape(12.5.dp),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "가이드",
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.padding(18.dp))
        Button(
            onClick = { onStartGame() },
            colors = ButtonDefaults.buttonColors(Color.Red),
            shape = RoundedCornerShape(100.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "시작하기",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
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
fun History(games: List<Game>) {
    Column(
        Modifier.padding(top = 28.dp)
    ) {
        SubTitleText(text = "HISTORY")
        TitleText(text = "진행내역 🤝")

        if (games.isEmpty()) {
            EmptyHistory()
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
fun EmptyHistory() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_onodofu),
                contentDescription = "오노도후",
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop
            )
            Text(text = "게임을 추가한 내역이 없습니다.", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "상단에 시작하기 버튼을 눌러 게임을 생성해주세요.")
        }
    }
}


@Preview("MainPreview")
@Composable
fun MainPreview() {
    MainScreen(MainViewModel(SavedStateHandle()))
}