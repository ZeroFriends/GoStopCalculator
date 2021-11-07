package zero.friends.data.util

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import zero.friends.domain.model.Rule

class SerializationConverters {
    @TypeConverter
    fun listToJson(value: List<Rule>) = Json.encodeToString(value)

    @TypeConverter
    fun jsonToList(value: String): List<Rule> = Json.decodeFromString(value)

}