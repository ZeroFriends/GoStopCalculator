package zero.friends.gostopcalculator.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import me.tatarka.inject.annotations.Inject
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIActivityItemSourceProtocol
import platform.UIKit.UIActivityType
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.darwin.NSObject

@Inject
actual class ScreenCaptureManager actual constructor() {
    actual suspend fun captureAndShare(
        graphicsLayer: GraphicsLayer,
        authority: String,
        fileNamePrefix: String
    ) {
        try {
            val imageBitmap = runCatching { graphicsLayer.toImageBitmap() }.getOrNull() ?: return
            val uiImage = imageBitmap.toUIImage() ?: return

            val itemSource = ImageActivityItemSource(uiImage)
            val activityViewController = UIActivityViewController(
                activityItems = listOf(itemSource),
                applicationActivities = null
            )

            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(
                viewControllerToPresent = activityViewController,
                animated = true,
                completion = null
            )
        } catch (e: Throwable) {
            // ignore all exceptions and errors
        }
    }
}

private class ImageActivityItemSource(
    private val image: UIImage
) : NSObject(), UIActivityItemSourceProtocol {

    override fun activityViewControllerPlaceholderItem(activityViewController: UIActivityViewController): Any {
        return image
    }

    @ObjCSignatureOverride
    override fun activityViewController(
        activityViewController: UIActivityViewController,
        itemForActivityType: UIActivityType?
    ): Any? {
        return image
    }

    @ObjCSignatureOverride
    override fun activityViewController(
        activityViewController: UIActivityViewController,
        dataTypeIdentifierForActivityType: UIActivityType?
    ): String {
        return "public.png"
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
    } catch (e: Throwable) {
        null
    }
}
