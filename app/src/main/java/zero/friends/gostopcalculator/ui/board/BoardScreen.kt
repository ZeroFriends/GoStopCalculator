package zero.friends.gostopcalculator.ui.board

import androidx.activity.compose.BackHandler
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import zero.friends.domain.model.Game

@Composable
fun BoardScreen(game: Game, onBack: () -> Unit = {}) {
    BackHandler(true) {
        onBack()
    }
    Surface {
        Text(text = game.toString())
    }
}

@Preview
@Composable
private fun BoardScreenPreview() {
    BoardScreen(Game())
}