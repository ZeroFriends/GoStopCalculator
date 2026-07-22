package zero.friends.data.source

import kotlinx.serialization.json.Json

expect fun readAsset(fileName: String): String

inline fun <reified T> parseAsset(fileName: String): T {
    val jsonContent = readAsset(fileName)
    return Json.decodeFromString(jsonContent)
}