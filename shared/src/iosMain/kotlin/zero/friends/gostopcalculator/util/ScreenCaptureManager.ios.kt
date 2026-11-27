package zero.friends.gostopcalculator.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Surface
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIActivityItemSourceProtocol
import platform.UIKit.UIActivityType
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.UIKit.popoverPresentationController
import platform.darwin.NSObject

@Inject
actual class ScreenCaptureManager actual constructor() {
    private val shareMutex = Mutex()

    actual suspend fun captureAndShare(
        graphicsLayer: GraphicsLayer,
        authority: String,
        fileNamePrefix: String
    ) {
        if (!shareMutex.tryLock()) return
        try {
            val imageBitmap = withContext(Dispatchers.Main.immediate) {
                runCatching { graphicsLayer.toImageBitmap() }.getOrNull()
            } ?: return
            val bytes = withContext(Dispatchers.Default) {
                imageBitmap.encodeToPngBytes()
            } ?: return
            val uiImage = bytes.toUIImage() ?: return

            withContext(Dispatchers.Main.immediate) {
                val itemSource = ImageActivityItemSource(uiImage)
                val activityViewController = UIActivityViewController(
                    activityItems = listOf(itemSource),
                    applicationActivities = null
                )

                val presenter = topViewController() ?: return@withContext
                activityViewController.popoverPresentationController?.apply {
                    sourceView = presenter.view
                    sourceRect = presenter.view.bounds
                }
                presenter.presentViewController(
                    viewControllerToPresent = activityViewController,
                    animated = true,
                    completion = null
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            // ignore all non-cancellation errors
        } finally {
            shareMutex.unlock()
        }
    }

    private fun topViewController(): UIViewController? {
        val window = UIApplication.sharedApplication.connectedScenes
            .mapNotNull { it as? UIWindowScene }
            .flatMap { scene -> scene.windows.mapNotNull { it as? UIWindow } }
            .firstOrNull { it.isKeyWindow() }
            ?: UIApplication.sharedApplication.keyWindow
        var top = window?.rootViewController
        while (top?.presentedViewController != null) {
            top = top.presentedViewController
        }
        return top
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

private fun ImageBitmap.encodeToPngBytes(maxDimension: Int = 4096): ByteArray? {
    val skiaImage = try {
        Image.makeFromBitmap(this.asSkiaBitmap())
    } catch (_: Throwable) {
        return null
    }
    return try {
        val longest = maxOf(skiaImage.width, skiaImage.height)
        if (longest > maxDimension && skiaImage.width > 0 && skiaImage.height > 0) {
            val ratio = maxDimension.toFloat() / longest.toFloat()
            val targetW = (skiaImage.width * ratio).toInt().coerceAtLeast(1)
            val targetH = (skiaImage.height * ratio).toInt().coerceAtLeast(1)
            val surface = Surface.makeRasterN32Premul(targetW, targetH)
            try {
                surface.canvas.drawImageRect(
                    skiaImage,
                    Rect.makeWH(targetW.toFloat(), targetH.toFloat()),
                )
                val scaledImage = surface.makeImageSnapshot()
                try {
                    val data = scaledImage.encodeToData()
                    try {
                        data?.bytes
                    } finally {
                        data?.close()
                    }
                } finally {
                    scaledImage.close()
                }
            } finally {
                surface.close()
            }
        } else {
            val data = skiaImage.encodeToData()
            try {
                data?.bytes
            } finally {
                data?.close()
            }
        }
    } catch (_: Throwable) {
        null
    } finally {
        skiaImage.close()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toUIImage(): UIImage? {
    if (isEmpty()) return null
    return try {
        val nsData = usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
        }
        UIImage.imageWithData(nsData)
    } catch (_: Throwable) {
        null
    }
}
