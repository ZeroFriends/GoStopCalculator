package zero.friends.gostopcalculator.ui.board.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.Gamer
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
                .fillMaxSize()
                .padding(top = 28.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
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