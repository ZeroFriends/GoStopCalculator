package zero.friends.gostopcalculator.ui.board.score.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import zero.friends.gostopcalculator.R
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
            text = stringResource(R.string.player_list),
            fontSize = 16.sp,
            color = colorResource(id = R.color.nero),
            fontWeight = FontWeight.Bold
        )
        RoundedCornerText(
            text = buttonText,
            onClick = onButtonClick
        )
    }
}

@Preview
@Composable
private fun GameListHeaderPreview() {
    GamerListHeader(
        buttonText = "Edit",
        onButtonClick = {}
    )
}

