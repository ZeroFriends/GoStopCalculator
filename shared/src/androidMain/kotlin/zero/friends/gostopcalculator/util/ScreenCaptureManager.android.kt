package zero.friends.gostopcalculator.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.FileProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
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
    private val shareMutex = Mutex()

    actual suspend fun captureAndShare(
        graphicsLayer: GraphicsLayer,
        authority: String,
        fileNamePrefix: String
    ) {
        if (!shareMutex.tryLock()) return
        try {
            val shareTitle = getString(Res.string.share_result_title)
            val chooserTitle = getString(Res.string.share_chooser_title)
            val rawBitmap = withContext(Dispatchers.Main.immediate) {
                graphicsLayer.toImageBitmap().asAndroidBitmap()
            }
            val file = try {
                withContext(Dispatchers.IO) {
                    val bitmapToSave = scaleDownIfNeeded(rawBitmap)
                    try {
                        val fileName = "${fileNamePrefix}_${System.currentTimeMillis()}.png"
                        File(context.cacheDir, fileName).apply {
                            outputStream().use { out ->
                                bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, out)
                                out.flush()
                            }
                        }.also { savedFile ->
                            clearOldTempFiles(prefix = fileNamePrefix, excludeFile = savedFile)
                        }
                    } finally {
                        if (bitmapToSave !== rawBitmap && !bitmapToSave.isRecycled) {
                            bitmapToSave.recycle()
                        }
                    }
                }
            } finally {
                if (!rawBitmap.isRecycled) {
                    rawBitmap.recycle()
                }
            }
            withContext(Dispatchers.Main.immediate) {
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
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            val failedMessage = runCatching { getString(Res.string.share_failed) }
                .getOrDefault("Failed to share.")
            withContext(Dispatchers.Main.immediate) {
                Toast.makeText(context, failedMessage, Toast.LENGTH_SHORT).show()
            }
        } finally {
            shareMutex.unlock()
        }
    }

    private fun scaleDownIfNeeded(bitmap: Bitmap, maxDimension: Int = 4096): Bitmap {
        val longest = maxOf(bitmap.width, bitmap.height)
        if (longest <= maxDimension || bitmap.width <= 0 || bitmap.height <= 0) return bitmap
        val ratio = maxDimension.toFloat() / longest.toFloat()
        val targetW = (bitmap.width * ratio).toInt().coerceAtLeast(1)
        val targetH = (bitmap.height * ratio).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
    }

    private fun clearOldTempFiles(prefix: String, excludeFile: File) {
        try {
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.absolutePath != excludeFile.absolutePath &&
                    (file.name.startsWith("${prefix}_") || file.name.startsWith("result_share"))
                ) {
                    file.delete()
                }
            }
        } catch (_: Exception) {
            // ignore
        }
    }
}
