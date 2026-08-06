package zero.friends.gostopcalculator.util

import androidx.compose.ui.graphics.Color
import zero.friends.gostopcalculator.theme.Blue
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.theme.OrangeyRed
import kotlin.math.abs

fun Int.getMoneyColor(): Color = when {
    this > 0 -> OrangeyRed
    this < 0 -> Blue
    else -> Nero
}

expect fun Int.separateComma(): String
expect fun Long.separateComma(): String

