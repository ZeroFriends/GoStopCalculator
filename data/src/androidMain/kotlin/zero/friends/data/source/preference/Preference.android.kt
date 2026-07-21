package zero.friends.data.source.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import zero.friends.domain.preference.AppPreference

private lateinit var preferenceContext: Context

fun initPreferenceContext(context: Context) {
    preferenceContext = context.applicationContext
}

actual class Preference actual constructor() : AppPreference {
    private val preferences by lazy {
        preferenceContext.getSharedPreferences("app_preference", MODE_PRIVATE)
    }

    override fun setFirstStart() {
        with(preferences.edit()) {
            putBoolean("IS_FIRST_START", false)
            commit()
        }
    }

    override fun isFistStart(): Boolean {
        return preferences.getBoolean("IS_FIRST_START", true)
    }
}
