package zero.friends.gostopcalculator.util

import android.content.Context

actual object ApplicationContextProvider {
    private var applicationContext: Context? = null

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    actual fun getContext(): Any {
        return applicationContext ?: error("ApplicationContextProvider is not initialized")
    }
}
