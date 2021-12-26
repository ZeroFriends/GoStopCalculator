package zero.friends.gostopcalculator.util

import zero.friends.gostopcalculator.R

fun Int.getMoneyColor() = when {
    this > 0 -> R.color.orangey_red
    this < 0 -> R.color.blue
    else -> R.color.nero
}