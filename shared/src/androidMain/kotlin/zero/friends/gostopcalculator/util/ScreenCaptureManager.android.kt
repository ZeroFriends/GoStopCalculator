package zero.friends.gostopcalculator.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.FileProvider
import me.tatarka.inject.annotations.Inject
import java.io.File

@Inject
actual class ScreenCaptureManager actual constructor() {
    private val context: Context get() = ApplicationContextProvider.getContext() as Context


    actual suspend fun captureAndShare(
        graphicsLayer: GraphicsLayer,
        authority: String,
        fileNamePrefix: String
    ) {
        try {
            clearOldTempFiles()
            val imageBitmap = graphicsLayer.toImageBitmap()
            val bitmap = imageBitmap.asAndroidBitmap()
            val fileName = "${fileNamePrefix}_${System.currentTimeMillis()}.png"
            val file = File(context.cacheDir, fileName).apply {
                outputStream().use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    out.flush()
                }
            }
            val uri = FileProvider.getUriForFile(context, authority, file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "공유하기").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Toast.makeText(context, "공유에 실패하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearOldTempFiles() {
        try {
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("result_share")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            // ignore
        }
    }
}
