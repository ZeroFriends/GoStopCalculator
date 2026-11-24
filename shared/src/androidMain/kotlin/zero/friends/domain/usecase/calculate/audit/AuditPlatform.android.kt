package zero.friends.domain.usecase.calculate.audit

import android.util.Log

internal actual class AuditLock

internal actual fun <T> AuditLock.withLock(block: () -> T): T = synchronized(this, block)

internal actual fun logAuditFallback(tag: String, message: String) {
    runCatching { Log.e(tag, message) }
        .onFailure { println("[$tag] $message") }
}
