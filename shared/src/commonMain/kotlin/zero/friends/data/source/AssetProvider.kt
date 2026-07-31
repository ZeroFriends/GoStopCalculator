package zero.friends.data.source

import kotlinx.serialization.json.Json

expect fun readAsset(fileName: String): String

inline fun <reified T> parseAsset(fileName: String): T {
    val jsonContent = readAsset(fileName).takeIf { it.isNotBlank() } ?: when (fileName) {
        "rule.json" -> DEFAULT_RULE_JSON
        "manual.json" -> DEFAULT_MANUAL_JSON
        else -> ""
    }
    return Json.decodeFromString(jsonContent)
}