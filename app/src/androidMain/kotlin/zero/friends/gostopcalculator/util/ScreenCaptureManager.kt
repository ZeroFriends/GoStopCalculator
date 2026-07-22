package zero.friends.gostopcalculator.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.FileProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ScreenCaptureManager @Inject constructor(
    @ActivityContext private val context: Context,
) {

    private var capturedFile: File? = null

    suspend fun captureAndGetUri(
        graphicsLayer: GraphicsLayer,
        authority: String,
        fileNamePrefix: String = "result_share"
    ): Uri? {
        val imageBitmap = graphicsLayer.toImageBitmap()
        return captureAndGetUri(imageBitmap, authority, fileNamePrefix)
    }

    suspend fun captureAndGetUri(
        imageBitmap: ImageBitmap,
        authority: String,
        fileNamePrefix: String = "result_share"
    ): Uri? {
        return try {
            clearOldTempFiles()
            val fileName = "${fileNamePrefix}_${System.currentTimeMillis()}.png"
            val bitmap = imageBitmap.asAndroidBitmap()
            val file = saveBitmapToFile(bitmap, fileName)
            capturedFile = file
            FileProvider.getUriForFile(context, authority, file)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Timber.e("캡쳐 실패 $e")
            Toast.makeText(context, "공유에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            clearTempFile()
            null
        }
    }

    fun clearOldTempFiles() {
        try {
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("result_share")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Timber.e("이전 임시 파일 청소 실패 $e")
        }
    }

    fun clearTempFile() {
        capturedFile?.delete()
        capturedFile = null
    }

    private suspend fun saveBitmapToFile(bitmap: Bitmap, fileName: String): File =
        withContext(Dispatchers.IO) {
            File(context.cacheDir, fileName).apply {
                outputStream().use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    out.flush()
                }
            }
        }
}


