package zero.friends.gostopcalculator.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    fun getCurrentTime(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())
}
