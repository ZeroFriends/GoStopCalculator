package zero.friends.gostopcalculator.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import zero.friends.gostopcalculator.GoogleAdmob
import zero.friends.gostopcalculator.R
import java.io.File
import javax.inject.Inject

class ScreenCaptureManager @Inject constructor(
    @ActivityContext private val context: Context,
    private val googleAdmob: GoogleAdmob,
) : DefaultLifecycleObserver {

    private var capturedFile: File? = null

    override fun onStart(owner: LifecycleOwner) {
        // 사용자가 공유 화면에서 앱으로 돌아왔을 때
        googleAdmob.showAd { }
        capturedFile?.delete()
        capturedFile = null
    }

    suspend fun captureAndShare(view: View, authority: String, fileName: String = "result_share.png") {
        try {
            val bitmap = captureView(view)
            val file = saveBitmapToFile(bitmap, fileName)
            capturedFile = file // 공유할 파일 참조 저장
            val uri = FileProvider.getUriForFile(context, authority, file)
            shareImage(uri)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Toast.makeText(context, "공유에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            capturedFile = null // 실패 시 참조 제거
        }
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

    private fun shareImage(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)))
    }
}
