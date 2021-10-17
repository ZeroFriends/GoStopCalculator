package zero.friends.data.source.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import zero.friends.domain.preference.AppPreference

class Preference constructor(context: Context) : AppPreference {
    private val preferences = context.getSharedPreferences(APP_PREFERENCE, MODE_PRIVATE)

    override fun insertLastPlayerId(id: Long) {
        with(preferences.edit()) {
            putLong(LAST_PLAYER_ID, id)
            commit()
        }
    }

    override fun getLastInsertedPlayerId(): Long {
        return preferences.getLong(LAST_PLAYER_ID, 0)
    }

    companion object {
        private const val APP_PREFERENCE = "app_preference"
        private const val LAST_PLAYER_ID = "last_player_id"
    }
}
