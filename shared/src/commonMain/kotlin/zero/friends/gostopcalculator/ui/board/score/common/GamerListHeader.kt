package zero.friends.gostopcalculator.ui.board.score.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.player_count_format
import zero.friends.gostopcalculator.shared.generated.resources.player_list

@Composable
fun GamerListHeader(
    buttonText: String,
    onButtonClick: () -> Unit,
    playerCount: Int? = null,
    buttonIcon: DrawableResource? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(Res.string.player_list),
                fontSize = 15.sp,
                color = ScoreTextDark,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
            if (playerCount != null) {
                Text(
                    text = stringResource(Res.string.player_count_format, playerCount),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ScorePrimaryRed,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(ScoreSoftPinkBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        val pillShape = RoundedCornerShape(100.dp)
        Surface(
            modifier = Modifier
                .clip(pillShape)
                .clickable(role = Role.Button, onClick = onButtonClick),
            shape = pillShape,
            color = ScoreSoftPinkButtonBg,
            border = BorderStroke(1.dp, ScoreSoftPinkBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (buttonIcon != null) {
                    Icon(
                        painter = painterResource(buttonIcon),
                        contentDescription = null,
                        tint = ScorePrimaryRed,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = buttonText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ScorePrimaryRed,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}
