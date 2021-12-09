package zero.friends.gostopcalculator.ui.board

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import zero.friends.gostopcalculator.ui.common.ContentsCard
import zero.friends.gostopcalculator.ui.common.GoStopButton

@Composable
@Preview
private fun BoardBackgroundPreView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp, bottom = 20.dp)
    ) {

        Column {
            ContentsCard(
                modifier = Modifier
                    .fillMaxWidth(),
                boxContents = {}
            )

            Spacer(modifier = Modifier.padding(9.dp))

            Surface(
                modifier = Modifier
                    .fillMaxSize()
            ) {
            }
        }


        GoStopButton(
            text = "",
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = {}
        )
    }
}