package zero.friends.data.source

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.charset.Charset
import javax.inject.Inject

class AssetProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun loadAsset(fileName: String): String {
        return context.assets.open(fileName).use {
            val size = it.available()
            val buffer = ByteArray(size)
            it.read(buffer)
            String(buffer, Charset.forName("UTF-8"))
        }
    }

}