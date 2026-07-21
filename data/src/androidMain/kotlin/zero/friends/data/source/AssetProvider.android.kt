package zero.friends.data.source

import android.content.Context

private lateinit var assetContext: Context

fun initAssetContext(context: Context) {
    assetContext = context.applicationContext
}

actual fun readAsset(fileName: String): String {
    return assetContext.assets.open(fileName).use { stream ->
        val size = stream.available()
        val buffer = ByteArray(size)
        stream.read(buffer)
        String(buffer, Charsets.UTF_8)
    }
}
