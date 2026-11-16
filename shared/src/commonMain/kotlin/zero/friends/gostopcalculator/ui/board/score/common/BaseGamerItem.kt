package zero.friends.gostopcalculator.ui.board.score.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.theme.Gray
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.theme.OrangeyRed

@Composable
fun BaseGamerItem(
    index: Int,
    gamer: Gamer,
    isEnabled: Boolean = true,
    badge: String? = null,
    content: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(end = if (content != null) 12.dp else 0.dp)
        ) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .widthIn(min = 30.dp)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isEnabled) OrangeyRed else Gray
            )
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = gamer.name,
                    fontSize = 16.sp,
                    color = if (isEnabled) Nero else Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    OptionBox(
                        text = badge,
                        color = Gray
                    )
                }
            }
        }

        content?.invoke(this)
    }
}
