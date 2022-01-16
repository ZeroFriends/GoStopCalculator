package zero.friends.data.source.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.hilt.android.qualifiers.ApplicationContext
import zero.friends.domain.preference.AppPreference
import javax.inject.Inject

class Preference @Inject constructor(@ApplicationContext context: Context) : AppPreference {
    private val preferences = context.getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)

    override fun setFirstStart() {
        with(preferences.edit()) {
            putBoolean(IS_FIRST_START, false)
            commit()
        }
    }

    override fun isFistStart(): Boolean {
        return preferences.getBoolean(IS_FIRST_START, true)
    }

    companion object {
        private const val APP_PREFERENCE = "app_preference"
        private const val IS_FIRST_START = "IS_FIRST_START"
    }
}
