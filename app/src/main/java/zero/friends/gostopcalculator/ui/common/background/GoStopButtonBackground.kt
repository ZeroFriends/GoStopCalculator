package zero.friends.gostopcalculator.ui.common.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.GoStopButton

@Composable
fun GoStopButtonBackground(
    buttonString: String,
    buttonEnabled: Boolean = true,
    onClick: () -> Unit = {},
    contents: @Composable BoxScope.() -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp, bottom = 20.dp, start = 16.dp, end = 16.dp),
    ) {
        val (content, button) = createRefs()
        Box(modifier = Modifier
            .constrainAs(content) {
                top.linkTo(parent.top)
                bottom.linkTo(button.top)
                height = Dimension.fillToConstraints
            }) {
            contents(this)
        }

        GoStopButton(
            text = buttonString,
            modifier = Modifier
                .padding(top = 10.dp)
                .constrainAs(button) {
                    bottom.linkTo(parent.bottom)
                },
            buttonEnabled = buttonEnabled,
            onClick = onClick
        )
    }
}

@Preview
@Composable
private fun GoStopButtonBackGroundPreview() {
    GoStopButtonBackground(stringResource(id = R.string.start_game))
}