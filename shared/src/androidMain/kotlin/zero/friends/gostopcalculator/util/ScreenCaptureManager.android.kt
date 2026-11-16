package zero.friends.gostopcalculator.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.FileProvider
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.share_chooser_title
import zero.friends.gostopcalculator.shared.generated.resources.share_failed
import zero.friends.gostopcalculator.shared.generated.resources.share_result_title
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
            val shareTitle = getString(Res.string.share_result_title)
            val chooserTitle = getString(Res.string.share_chooser_title)
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
                clipData = ClipData.newRawUri(shareTitle, uri)
                putExtra(Intent.EXTRA_TITLE, shareTitle)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, chooserTitle).apply {
                clipData = ClipData.newRawUri(shareTitle, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            val failedMessage = runCatching { getString(Res.string.share_failed) }
                .getOrDefault("Failed to share.")
            Toast.makeText(context, failedMessage, Toast.LENGTH_SHORT).show()
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
