package zero.friends.gostopcalculator.audit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import zero.friends.domain.usecase.calculate.audit.AuditPhase
import zero.friends.domain.usecase.calculate.audit.CalculationAuditReport
import zero.friends.domain.usecase.calculate.audit.CalculationInvariantException
import zero.friends.domain.usecase.calculate.audit.CalculationViolation
import zero.friends.domain.usecase.calculate.audit.CalculationViolationCode

class AndroidCrashlyticsCalculationAuditReporterTest {

    private class FakeSink : CrashlyticsAuditSink {
        val customKeys = linkedMapOf<String, String>()
        val logs = mutableListOf<String>()
        var recordedException: Throwable? = null

        override fun setCustomKey(key: String, value: String) {
            customKeys[key] = value
        }

        override fun log(message: String) {
            logs += message
        }

        override fun recordException(throwable: Throwable) {
            recordedException = throwable
        }
    }

    private fun sampleReport(): CalculationAuditReport = CalculationAuditReport(
        phase = AuditPhase.ROUND_CALCULATE,
        gameId = 12L,
        roundId = 34L,
        playerCount = 3,
        roundCount = 1,
        violations = listOf(
            CalculationViolation(
                code = CalculationViolationCode.ZERO_SUM_BROKEN,
                message = "Round zero-sum broken: expected 0, actual 100",
                expected = 0L,
                actual = 100L,
            )
        ),
        customKeys = linkedMapOf(
            "calc_audit_phase" to "round_calculate",
            "calc_audit_violations" to "ZERO_SUM_BROKEN",
            "calc_audit_game_id" to "12",
        ),
        diagnosticSummary = "[round_calculate] ZERO_SUM_BROKEN (players=3) - Round zero-sum broken: expected 0, actual 100",
        diagnosticPayload = "line1\n\nline2\nline3",
    )

    @Test
    fun `Crashlytics 비활성화 시 로컬 로그만 남기고 Sink는 호출하지 않는다`() {
        var sinkCalls = 0
        val localLogs = mutableListOf<String>()
        val reporter = AndroidCrashlyticsCalculationAuditReporter(
            isCrashlyticsEnabled = false,
            appVersionName = "1.5.0",
            appVersionCode = 19,
            sinkProvider = {
                sinkCalls++
                FakeSink()
            },
            localLogger = { localLogs += it },
        )

        reporter.report(sampleReport())

        assertEquals(0, sinkCalls)
        assertEquals(1, localLogs.size)
        assertTrue(localLogs.single().contains("ZERO_SUM_BROKEN"))
    }

    @Test
    fun `Crashlytics 활성화 시 커스텀 키와 멀티라인 로그 및 비치명 예외를 기록한다`() {
        val sink = FakeSink()
        val localLogs = mutableListOf<String>()
        val reporter = AndroidCrashlyticsCalculationAuditReporter(
            isCrashlyticsEnabled = true,
            appVersionName = "1.5.0",
            appVersionCode = 19,
            sinkProvider = { sink },
            localLogger = { localLogs += it },
        )

        val report = sampleReport()
        reporter.report(report)

        assertEquals(1, localLogs.size)
        assertEquals("1.5.0 (19)", sink.customKeys["calc_audit_app_version"])
        assertEquals("round_calculate", sink.customKeys["calc_audit_phase"])
        assertEquals("ZERO_SUM_BROKEN", sink.customKeys["calc_audit_violations"])
        assertEquals("12", sink.customKeys["calc_audit_game_id"])
        assertEquals(listOf("line1", "line2", "line3"), sink.logs)

        val exception = sink.recordedException
        assertNotNull(exception)
        assertTrue(exception is CalculationInvariantException)
        val invariantException = exception as CalculationInvariantException
        assertEquals(report.issueKey, invariantException.issueKey)
        assertEquals(report.diagnosticSummary, invariantException.message)
    }

    @Test
    fun `Crashlytics Sink가 예외를 던져도 앱이 크래시하지 않는다`() {
        var localLogCalled = 0
        val reporter = AndroidCrashlyticsCalculationAuditReporter(
            isCrashlyticsEnabled = true,
            appVersionName = "1.5.0",
            appVersionCode = 19,
            sinkProvider = { error("Firebase unavailable") },
            localLogger = { localLogCalled++ },
        )

        reporter.report(sampleReport())
        assertEquals(1, localLogCalled)
    }
}
