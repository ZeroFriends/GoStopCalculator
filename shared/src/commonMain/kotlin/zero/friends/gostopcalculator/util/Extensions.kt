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

fun Int.separateComma(): String {
    val sign = if (this < 0) "-" else ""
    val absVal = abs(this)
    val formatted = absVal.toString().reversed().chunked(3).joinToString(",").reversed()
    return "$sign$formatted"
}
