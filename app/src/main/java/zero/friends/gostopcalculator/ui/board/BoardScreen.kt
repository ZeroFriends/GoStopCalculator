package zero.friends.gostopcalculator.ui.board

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BoardScreen(onBack: () -> Unit = {}) {
    BackHandler(true) {
        onBack()
    }
}

@Preview
@Composable
fun BoardScreenPreview() {
    BoardScreen()
}