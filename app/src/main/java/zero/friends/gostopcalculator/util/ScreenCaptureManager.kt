package zero.friends.gostopcalculator.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
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

    suspend fun captureAndGetUri(view: View, authority: String, fileName: String = "result_share.png"): Uri? {
        return try {
            val bitmap = captureView(view)
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

    fun clearTempFile() {
        capturedFile?.delete()
        capturedFile = null
    }

    private suspend fun captureView(view: View): Bitmap = withContext(Dispatchers.Main) {
        val scrollView = view.parent as? ScrollView
        val content = if (scrollView != null) scrollView.getChildAt(0) else view

        val bitmap = createBitmap(content.width, content.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        content.draw(canvas)
        bitmap
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
