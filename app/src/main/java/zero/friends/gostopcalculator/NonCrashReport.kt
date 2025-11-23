package zero.friends.gostopcalculator

import com.google.firebase.crashlytics.FirebaseCrashlytics

object NonCrashReport {
    fun send(message: String, extras: Map<String, String> = emptyMap()) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.log(message)
        extras.forEach { (key, value) ->
            crashlytics.setCustomKey(key, value)
        }
        crashlytics.recordException(NonFatalException(message))
    }
}

class NonFatalException(override val message: String) : Exception(message)
