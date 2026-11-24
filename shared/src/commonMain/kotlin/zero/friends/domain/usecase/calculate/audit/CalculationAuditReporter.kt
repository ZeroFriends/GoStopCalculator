package zero.friends.domain.usecase.calculate.audit

import kotlin.concurrent.Volatile

interface CalculationAuditReporter {
    fun report(report: CalculationAuditReport)
}

object PlatformFallbackAuditReporter : CalculationAuditReporter {
    override fun report(report: CalculationAuditReport) {
        logAuditFallback(
            tag = "CalculationAudit",
            message = "${report.diagnosticSummary}\n${report.diagnosticPayload}",
        )
    }
}

object CalculationAuditReporterRegistry {
    @Volatile
    private var delegate: CalculationAuditReporter = PlatformFallbackAuditReporter

    fun install(reporter: CalculationAuditReporter) {
        delegate = reporter
    }

    fun reset() {
        delegate = PlatformFallbackAuditReporter
    }

    fun current(): CalculationAuditReporter = delegate

    fun delegatingReporter(): CalculationAuditReporter = object : CalculationAuditReporter {
        override fun report(report: CalculationAuditReport) {
            current().report(report)
        }
    }
}

class AuditDeduplicator(private val maxEntries: Int = 256) {
    private val lock = AuditLock()
    private val seen = HashSet<String>()
    private val queue = ArrayDeque<String>()

    fun shouldReport(key: String): Boolean = lock.withLock {
        if (maxEntries <= 0) return@withLock true
        if (key in seen) return@withLock false
        while (queue.size >= maxEntries) {
            val oldest = queue.removeFirst()
            seen.remove(oldest)
        }
        queue.addLast(key)
        seen.add(key)
        true
    }

    fun clear() = lock.withLock {
        seen.clear()
        queue.clear()
    }
}
