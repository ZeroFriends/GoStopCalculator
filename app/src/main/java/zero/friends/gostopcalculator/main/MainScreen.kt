package zero.friends.gostopcalculator.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import timber.log.Timber

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

        History()
    }
}

@Composable
fun NewGame(onStartGame: () -> Unit, onShowGuide: () -> Unit) {
    Column {
        Text(
            text = "NEW GAME",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "오늘의 게임 👊",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
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
fun History() {

}

@Preview("MainPreview")
@Composable
fun MainPreview() {
    MainScreen(MainViewModel(SavedStateHandle()))
}