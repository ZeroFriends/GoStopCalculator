package zero.friends.gostopcalculator.ui.common.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.GoStopButton

@Composable
fun GoStopButtonBackground(
    modifier: Modifier = Modifier.padding(horizontal = 16.dp),
    buttonString: String,
    buttonEnabled: Boolean = true,
    showGradient: Boolean = false,
    onClick: () -> Unit = {},
    contents: @Composable BoxScope.() -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp, bottom = 20.dp),
    ) {
        Box(
            modifier = modifier
                .weight(1f),
        ) {
            contents()
            if (showGradient) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    LightGray
                                )
                            )
                        )
                )
            }
        }
        
        GoStopButton(
            modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp),
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
