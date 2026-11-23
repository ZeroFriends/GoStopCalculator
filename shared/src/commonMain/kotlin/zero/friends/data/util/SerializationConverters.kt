package zero.friends.data.util

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.model.Rule
import zero.friends.domain.model.Target

class SerializationConverters {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true
    }

    @TypeConverter
    fun listToJson(value: List<Rule>) = json.encodeToString(value)

    @TypeConverter
    fun jsonToList(value: String): List<Rule> = json.decodeFromString(value)

    @TypeConverter
    fun targetToString(value: List<Target>) = json.encodeToString(value)

    @TypeConverter
    fun stringToTarget(value: String): List<Target> = json.decodeFromString(value)

    @TypeConverter
    fun roundTraceTermsToString(value: List<RoundTraceTerm>) = json.encodeToString(value)

    @TypeConverter
    fun stringToRoundTraceTerms(value: String): List<RoundTraceTerm> = json.decodeFromString(value)
}
