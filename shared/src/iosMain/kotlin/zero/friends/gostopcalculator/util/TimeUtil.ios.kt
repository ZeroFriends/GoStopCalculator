package zero.friends.gostopcalculator.util

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual object TimeUtil {
    actual fun getCurrentTime(): String {
        val formatter = NSDateFormatter().apply {
            dateFormat = "yyyy-MM-dd"
        }
        return formatter.stringFromDate(NSDate())
    }
}
