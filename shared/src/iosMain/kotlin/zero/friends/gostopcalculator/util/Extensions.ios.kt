package zero.friends.gostopcalculator.util

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

actual fun Int.separateComma(): String = this.toLong().separateComma()

actual fun Long.separateComma(): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterDecimalStyle
    }
    return formatter.stringFromNumber(NSNumber(longLong = this)) ?: this.toString()
}
