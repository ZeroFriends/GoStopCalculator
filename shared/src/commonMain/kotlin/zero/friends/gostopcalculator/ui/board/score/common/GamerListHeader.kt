package zero.friends.gostopcalculator.ui.board.score.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.player_list
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.ui.common.RoundedCornerText

@Composable
fun GamerListHeader(
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(Res.string.player_list),
            fontSize = 16.sp,
            color = Nero,
            fontWeight = FontWeight.Bold
        )
        RoundedCornerText(
            text = buttonText,
            onClick = onButtonClick
        )
    }
}
