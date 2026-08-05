package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.detail
import zero.friends.gostopcalculator.shared.generated.resources.ic_more_black
import zero.friends.gostopcalculator.shared.generated.resources.round
import zero.friends.gostopcalculator.theme.Gray

@Composable
fun RoundBox(
    modifier: Modifier = Modifier,
    index: Int? = null,
    gamers: List<Gamer> = emptyList(),
    onClickDetail: (() -> Unit)? = null,
    onClickMore: (() -> Unit)? = null
) {
    ContentsCard(modifier = modifier.padding(vertical = 3.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (index != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(Res.string.round, index + 1))

                    Surface(modifier = Modifier.clickable { onClickMore?.invoke() }) {
                        Image(
                            painter = painterResource(Res.drawable.ic_more_black),
                            contentDescription = null
                        )
                    }
                }
            }

            GridItems(data = gamers, nColumns = 2) { idx, gamer ->
                GamerItem(index = idx, gamer = gamer)
            }
            Spacer(modifier = Modifier.padding(4.dp))

            if (onClickDetail != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Gray.copy(alpha = 0.12f))
                        .clickable { onClickDetail.invoke() }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.detail),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}