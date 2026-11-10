package zero.friends.gostopcalculator.ui.board.score.end

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.congratulation
import zero.friends.gostopcalculator.shared.generated.resources.end_description
import zero.friends.gostopcalculator.shared.generated.resources.end_game
import zero.friends.gostopcalculator.shared.generated.resources.ic_confetti
import zero.friends.gostopcalculator.shared.generated.resources.save
import zero.friends.gostopcalculator.theme.Gray38
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.RoundBox
import zero.friends.gostopcalculator.ui.common.background.GoStopButtonBackground

@Composable
fun EndScreen(
    endViewModel: EndViewModel,
    onComplete: (gameId: Long) -> Unit = {}
) {
    val uiState by endViewModel.endUiState().collectAsState()
    val scaffoldState = rememberScaffoldState()
    EndScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        onClick = { onComplete(uiState.game.id) },
    )
}

@Composable
private fun EndScreen(
    scaffoldState: ScaffoldState,
    uiState: EndUiState,
    onClick: () -> Unit = {},
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = stringResource(Res.string.end_game),
                isRed = false,
            )
        }
    ) { innerPadding ->
        GoStopButtonBackground(
            modifier = Modifier.padding(innerPadding),
            buttonString = stringResource(Res.string.save),
            onClick = { onClick() }
        ) {
            FinishScreen(Modifier.align(Alignment.Center))
            RoundBox(modifier = Modifier.align(Alignment.BottomCenter), gamers = uiState.gamers)
        }
    }
}

@Composable
private fun FinishScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(Res.drawable.ic_confetti), contentDescription = null)
        Spacer(modifier = Modifier.padding(6.dp))
        Text(
            text = stringResource(Res.string.congratulation),
            fontSize = 24.sp,
            color = Nero,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Text(
            text = stringResource(Res.string.end_description),
            fontSize = 14.sp,
            color = Gray38
        )
    }
}
