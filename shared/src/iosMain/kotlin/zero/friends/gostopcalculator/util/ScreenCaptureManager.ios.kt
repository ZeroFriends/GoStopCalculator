package zero.friends.gostopcalculator.util

import androidx.compose.ui.graphics.layer.GraphicsLayer
import me.tatarka.inject.annotations.Inject
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
            // Compose Multiplatform ImageBitmap to iOS UIImage
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

private fun androidx.compose.ui.graphics.ImageBitmap.toUIImage(): UIImage? {
    return try {
        // ImageBitmap to Skia / UIImage conversion placeholder for KMP
        null
    } catch (e: Exception) {
        null
    }
}
