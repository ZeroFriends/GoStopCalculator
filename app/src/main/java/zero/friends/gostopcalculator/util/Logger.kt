package zero.friends.gostopcalculator.util

import timber.log.Timber

const val TAG = "ZeroFriends"

object log {
    fun d(message: String) {
        Timber.tag(TAG).d(message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.tag(TAG).e(throwable, message)
        } else {
            Timber.tag(TAG).e(message)
        }
    }

    fun w(message: String) {
        Timber.tag(TAG).w(message)
    }

    fun i(message: String) {
        Timber.tag(TAG).i(message)
    }
}
