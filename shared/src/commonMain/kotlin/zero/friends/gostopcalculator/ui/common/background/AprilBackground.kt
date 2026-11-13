package zero.friends.gostopcalculator.ui.common.background

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.ic_april
import zero.friends.gostopcalculator.theme.OrangeyRed
import zero.friends.gostopcalculator.ui.common.GoStopButton

@Composable
fun AprilBackground(
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier,
    buttonText: String,
    buttonEnabled: Boolean = false,
    onClick: () -> Unit,
    contents: @Composable () -> Unit,
) {
    Surface(modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(Res.drawable.ic_april),
                contentDescription = null,
                alignment = Alignment.TopEnd,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .fillMaxSize()
                    .background(OrangeyRed)
            )

            Column(Modifier.padding(top = 30.dp)) {
                val textModifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                Text(
                    title,
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = textModifier
                )
                Text(
                    subTitle,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = textModifier.padding(bottom = 26.dp)
                )
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                            .padding(vertical = 20.dp, horizontal = 16.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            contents()
                        }
                        GoStopButton(
                            text = buttonText,
                            buttonEnabled = buttonEnabled,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            onClick = onClick
                        )
                    }
                }
            }
        }
    }
}
