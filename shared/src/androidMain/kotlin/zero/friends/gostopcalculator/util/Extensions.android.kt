package zero.friends.gostopcalculator.util

import zero.friends.shared.R
import java.text.NumberFormat

fun Int.getMoneyColorRes(): Int = when {
    this > 0 -> R.color.orangey_red
    this < 0 -> R.color.blue
    else -> R.color.nero
}

actual fun Int.separateComma(): String = NumberFormat.getInstance(java.util.Locale.US).format(this)
actual fun Long.separateComma(): String = NumberFormat.getInstance(java.util.Locale.US).format(this)
