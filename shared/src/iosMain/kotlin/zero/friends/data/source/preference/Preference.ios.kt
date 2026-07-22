package zero.friends.data.source.preference

import platform.Foundation.NSUserDefaults
import zero.friends.domain.preference.AppPreference

actual class Preference actual constructor() : AppPreference {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun setFirstStart() {
        userDefaults.setBool(false, forKey = "IS_FIRST_START")
    }

    override fun isFistStart(): Boolean {
        if (userDefaults.objectForKey("IS_FIRST_START") == null) {
            return true
        }
        return userDefaults.boolForKey("IS_FIRST_START")
    }
}
