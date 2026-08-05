package zero.friends.data.source.preference

import zero.friends.domain.preference.AppPreference

expect class Preference() : AppPreference {
    override fun setFirstStart()
    override fun isFistStart(): Boolean
}



