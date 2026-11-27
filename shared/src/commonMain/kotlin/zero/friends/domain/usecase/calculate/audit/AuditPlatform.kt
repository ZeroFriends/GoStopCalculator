package zero.friends.domain.usecase.calculate.audit

internal expect class AuditLock()

internal expect fun <T> AuditLock.withLock(block: () -> T): T

internal expect fun logAuditFallback(tag: String, message: String)
