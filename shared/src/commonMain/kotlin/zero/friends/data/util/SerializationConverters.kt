package zero.friends.data.util

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.model.Rule
import zero.friends.domain.model.Target

class SerializationConverters {
    @TypeConverter
    fun listToJson(value: List<Rule>) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToList(value: String): List<Rule> = Json.decodeFromString(value)

    @TypeConverter
    fun targetToString(value: List<Target>) = Json.encodeToString(value)

    @TypeConverter
    fun stringToTarget(value: String): List<Target> = Json.decodeFromString(value)

    @TypeConverter
    fun roundTraceTermsToString(value: List<RoundTraceTerm>) = Json.encodeToString(value)

    @TypeConverter
    fun stringToRoundTraceTerms(value: String): List<RoundTraceTerm> = Json.decodeFromString(value)

}
