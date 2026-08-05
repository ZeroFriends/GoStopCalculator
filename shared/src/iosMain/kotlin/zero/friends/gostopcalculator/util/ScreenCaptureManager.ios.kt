package zero.friends.gostopcalculator.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import me.tatarka.inject.annotations.Inject
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage

@Inject
actual class ScreenCaptureManager actual constructor() {
    actual suspend fun captureAndShare(
        graphicsLayer: GraphicsLayer,
        authority: String,
        fileNamePrefix: String
    ) {
        try {
            val imageBitmap = graphicsLayer.toImageBitmap()
            val uiImage = imageBitmap.toUIImage() ?: return

            val activityViewController = UIActivityViewController(
                activityItems = listOf(uiImage),
                applicationActivities = null
            )

            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(
                viewControllerToPresent = activityViewController,
                animated = true,
                completion = null
            )
        } catch (e: Exception) {
            // ignore
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ImageBitmap.toUIImage(): UIImage? {
    return try {
        val skiaBitmap = this.asSkiaBitmap()
        val skiaImage = Image.makeFromBitmap(skiaBitmap)
        val bytes = skiaImage.encodeToData()?.bytes ?: return null
        val nsData = bytes.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
        }
        UIImage.imageWithData(nsData)
    } catch (e: Exception) {
        null
    }
}
