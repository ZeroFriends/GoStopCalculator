package zero.friends.gostopcalculator.util

import java.text.SimpleDateFormat
import java.util.Locale

actual object TimeUtil {
    actual fun getCurrentTime(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())
}
