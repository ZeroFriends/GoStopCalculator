package zero.friends.data.source.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import zero.friends.domain.preference.AppPreference

private lateinit var preferenceContext: Context

fun initPreferenceContext(context: Context) {
    preferenceContext = context.applicationContext
}

actual class Preference actual constructor() : AppPreference {
    private val preferences by lazy {
        preferenceContext.getSharedPreferences("app_preference", MODE_PRIVATE)
    }

    actual override fun setFirstStart() {
        preferences.edit(commit = true) {
            putBoolean("IS_FIRST_START", false)
        }
    }

    actual override fun isFistStart(): Boolean {
        return preferences.getBoolean("IS_FIRST_START", true)
    }

}
