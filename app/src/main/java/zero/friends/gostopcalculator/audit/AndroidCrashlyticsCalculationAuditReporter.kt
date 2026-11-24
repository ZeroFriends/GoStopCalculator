package zero.friends.gostopcalculator.audit

import com.google.firebase.crashlytics.FirebaseCrashlytics
import zero.friends.domain.usecase.calculate.audit.CalculationAuditReport
import zero.friends.domain.usecase.calculate.audit.CalculationAuditReporter
import zero.friends.gostopcalculator.util.log

internal interface CrashlyticsAuditSink {
    fun setCustomKey(key: String, value: String)
    fun log(message: String)
    fun recordException(throwable: Throwable)
}

private class FirebaseCrashlyticsAuditSink(
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance(),
) : CrashlyticsAuditSink {
    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}

class AndroidCrashlyticsCalculationAuditReporter internal constructor(
    private val isCrashlyticsEnabled: Boolean,
    private val appVersionName: String,
    private val appVersionCode: Int,
    private val sinkProvider: () -> CrashlyticsAuditSink = { FirebaseCrashlyticsAuditSink() },
    private val localLogger: (String) -> Unit = { message -> log.e(message) },
) : CalculationAuditReporter {

    constructor(
        isCrashlyticsEnabled: Boolean,
        appVersionName: String,
        appVersionCode: Int,
    ) : this(
        isCrashlyticsEnabled = isCrashlyticsEnabled,
        appVersionName = appVersionName,
        appVersionCode = appVersionCode,
        sinkProvider = { FirebaseCrashlyticsAuditSink() },
        localLogger = { message -> log.e(message) },
    )

    override fun report(report: CalculationAuditReport) {
        runCatching {
            localLogger("[CalculationAudit] ${report.diagnosticSummary}\n${report.diagnosticPayload}")
        }
        if (!isCrashlyticsEnabled) return

        runCatching {
            val sink = sinkProvider()
            sink.setCustomKey(
                "calc_audit_app_version",
                "$appVersionName ($appVersionCode)".take(MAX_CUSTOM_KEY_LENGTH),
            )
            report.customKeys.forEach { (key, value) ->
                sink.setCustomKey(key.take(MAX_CUSTOM_KEY_LENGTH), value.take(MAX_CUSTOM_KEY_LENGTH))
            }
            report.diagnosticPayload
                .take(MAX_PAYLOAD_CHARS)
                .lineSequence()
                .filter { it.isNotBlank() }
                .forEach { line ->
                    sink.log(line.take(MAX_LOG_LINE_LENGTH))
                }
            sink.recordException(report.toException())
        }
    }

    companion object {
        private const val MAX_CUSTOM_KEY_LENGTH = 1000
        private const val MAX_LOG_LINE_LENGTH = 1000
        private const val MAX_PAYLOAD_CHARS = 8000
    }
}
