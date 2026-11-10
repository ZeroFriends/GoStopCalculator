package zero.friends.data.source

import kotlinx.serialization.json.Json

expect fun readAsset(fileName: String): String

@PublishedApi
internal val safeJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

inline fun <reified T> parseAsset(fileName: String): T {
    val rawContent = runCatching { readAsset(fileName) }.getOrNull()
    val jsonContent = rawContent?.takeIf { it.isNotBlank() } ?: when (fileName) {
        "rule.json" -> DEFAULT_RULE_JSON
        "manual.json" -> DEFAULT_MANUAL_JSON
        else -> null
    }

    if (jsonContent.isNullOrBlank()) {
        return safeJson.decodeFromString("[]")
    }

    return runCatching {
        safeJson.decodeFromString<T>(jsonContent)
    }.getOrElse {
        safeJson.decodeFromString("[]")
    }
}