package zero.friends.gostopcalculator.ui.board.score.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import zero.friends.gostopcalculator.R

@Composable
fun OptionBox(
    text: String,
    color: Color,
    isSelected: Boolean = false,
    onClick: ((Boolean) -> Unit)? = null
) {
    val shape = remember { RoundedCornerShape(12.dp) }
    Surface(
        shape = shape,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) colorResource(id = R.color.orangey_red) else color
        ),
        color = if (isSelected) colorResource(id = R.color.orangey_red) else MaterialTheme.colors.surface,
        modifier = Modifier
            .clip(shape)
            .clickable(onClick = { onClick?.invoke(isSelected) }, enabled = onClick != null),
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
            text = text,
            color = if (isSelected) colorResource(id = R.color.white) else color
        )
    }
}

