package zero.friends.gostopcalculator.util

import androidx.compose.ui.graphics.layer.GraphicsLayer

expect class ScreenCaptureManager() {
    suspend fun captureAndShare(
        graphicsLayer: GraphicsLayer,
        authority: String = "zero.friends.gostopcalculator.fileprovider",
        fileNamePrefix: String = "result_share"
    )
}
