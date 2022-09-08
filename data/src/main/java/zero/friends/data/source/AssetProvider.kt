package zero.friends.data.source

import android.content.Context
import android.content.res.AssetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AssetProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val assetManager: AssetManager = context.assets
    inline operator fun <reified T> invoke(fileName: String): T {
        val json = assetManager.open(fileName).use {
            val size = it.available()
            val buffer = ByteArray(size)
            it.read(buffer)
            String(buffer, Charsets.UTF_8)
        }
        return Json.decodeFromString(json)
    }

}