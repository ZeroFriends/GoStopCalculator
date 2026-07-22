package zero.friends.gostopcalculator.util

import zero.friends.shared.R

fun Int.getMoneyColorRes(): Int = when {
    this > 0 -> R.color.orangey_red
    this < 0 -> R.color.blue
    else -> R.color.nero
}
