package zero.friends.gostopcalculator.util

import androidx.compose.ui.graphics.layer.GraphicsLayer
import me.tatarka.inject.annotations.Inject

@Inject
actual class ScreenCaptureManager actual constructor() {
    actual suspend fun captureAndShare(
        graphicsLayer: GraphicsLayer,
        authority: String,
        fileNamePrefix: String
    ) {
        // iOS implementation placeholder
    }
}
