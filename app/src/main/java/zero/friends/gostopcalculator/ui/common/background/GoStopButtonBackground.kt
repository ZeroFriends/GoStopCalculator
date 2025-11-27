package zero.friends.gostopcalculator.ui.common.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.GoStopButton

@Composable
fun GoStopButtonBackground(
    modifier: Modifier = Modifier.padding(top = 12.dp, bottom = 20.dp, start = 16.dp, end = 16.dp),
    buttonString: String,
    buttonEnabled: Boolean = true,
    onClick: () -> Unit = {},
    contents: @Composable BoxScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.weight(1f),
            content = contents
        )
        GoStopButton(
            text = buttonString,
            buttonEnabled = buttonEnabled,
            onClick = onClick
        )
    }
}

@Preview
@Composable
private fun GoStopButtonBackGroundPreview() {
    GoStopButtonBackground(buttonString = stringResource(id = R.string.start_game))
}
