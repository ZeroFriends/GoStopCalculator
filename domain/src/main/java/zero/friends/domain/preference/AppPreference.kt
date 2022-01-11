package zero.friends.domain.preference

interface AppPreference {
    fun insertLastPlayerId(id: Long)
    fun getLastInsertedPlayerId(): Long

}