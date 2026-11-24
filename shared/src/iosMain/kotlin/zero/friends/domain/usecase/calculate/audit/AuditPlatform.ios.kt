package zero.friends.domain.usecase.calculate.audit

import platform.Foundation.NSLock

internal actual class AuditLock {
    val lock = NSLock()
}

internal actual fun <T> AuditLock.withLock(block: () -> T): T {
    lock.lock()
    return try {
        block()
    } finally {
        lock.unlock()
    }
}

internal actual fun logAuditFallback(tag: String, message: String) {
    println("[$tag] $message")
}
