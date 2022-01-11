package zero.friends.gostopcalculator.ui.board.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.*
import zero.friends.domain.model.Target
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CalculatedBox
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar

@Composable
fun ResultScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    gameName: String = "",
    gamers: List<Gamer> = emptyList(),
    onBack: () -> Unit = {},
    title: String = ""
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = gameName,
                onBack = onBack,
                isRed = false
            )
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(18.dp)
        ) {
            item {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    color = colorResource(id = R.color.nero),
                    fontWeight = FontWeight.Bold
                )
            }
            itemsIndexed(gamers) { index, gamer ->
                CalculatedBox(index, gamer)
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ResultScreen(
        gameName = "helloWorld",
        gamers = listOf(
            Gamer(
                name = "송준영", calculate = listOf(
                    Target(name = "조재영", account = 1000),
                    Target(name = "김경민", account = -1000),
                ),
                account = 2000,
                winnerOption = WinnerOption.Winner
            ),
            Gamer(
                name = "김경민", calculate = listOf(
                    Target(name = "송준영", account = -1000),
                    Target(name = "조재영", account = -500),
                ),
                account = -2000,
                scoreOption = listOf(ScoreOption.SecondFuck),
                loserOption = listOf(LoserOption.LightBak)
            )
        ),
        title = stringResource(id = R.string.calculate_history_text)
    )
}