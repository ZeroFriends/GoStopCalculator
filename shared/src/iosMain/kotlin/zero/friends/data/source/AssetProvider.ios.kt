package zero.friends.data.source

import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

actual fun readAsset(fileName: String): String {
    val nameWithoutExtension = fileName.substringBeforeLast(".")
    val extension = fileName.substringAfterLast(".", "")
    val path = NSBundle.mainBundle.pathForResource(nameWithoutExtension, extension)
        ?: return ""
    return NSString.stringWithContentsOfFile(path, encoding = NSUTF8StringEncoding, error = null) ?: ""
}
