package zero.friends.gostopcalculator.ui.board.score

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
fun ScoreScreen(onBack: () -> Unit) {

    BackHandler(true) {
        onBack()
    }
}