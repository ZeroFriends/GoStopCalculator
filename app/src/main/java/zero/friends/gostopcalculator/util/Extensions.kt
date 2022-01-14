package zero.friends.gostopcalculator.util

import zero.friends.gostopcalculator.R
import java.text.NumberFormat
import java.util.*

fun Int.getMoneyColor() = when {
    this > 0 -> R.color.orangey_red
    this < 0 -> R.color.blue
    else -> R.color.nero
}

fun Int.separateComma(): String =
    NumberFormat.getNumberInstance(Locale.KOREA).format(this)
