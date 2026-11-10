package zero.friends.data.source

import android.content.Context

private lateinit var assetContext: Context

fun initAssetContext(context: Context) {
    assetContext = context.applicationContext
}

actual fun readAsset(fileName: String): String {
    return assetContext.assets.open(fileName).use { stream ->
        stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}
