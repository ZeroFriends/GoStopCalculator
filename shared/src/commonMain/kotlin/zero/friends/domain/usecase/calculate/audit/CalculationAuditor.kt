package zero.friends.domain.usecase.calculate.audit

import kotlinx.coroutines.CancellationException
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.model.Rule

interface CalculationAuditor {
    suspend fun auditRound(
        phase: AuditPhase,
        gameId: Long,
        roundId: Long,
        gamers: List<Gamer>,
        rules: List<Rule>,
        trace: RoundTrace? = null,
        explicitSellerId: Long? = null,
        explicitWinnerId: Long? = null,
    )

    fun auditSettlement(
        gameId: Long,
        rawGamers: List<Gamer>,
        aggregatedGamers: List<Gamer>,
        rules: List<Rule> = emptyList(),
    )

    object NoOp : CalculationAuditor {
        override suspend fun auditRound(
            phase: AuditPhase,
            gameId: Long,
            roundId: Long,
            gamers: List<Gamer>,
            rules: List<Rule>,
            trace: RoundTrace?,
            explicitSellerId: Long?,
            explicitWinnerId: Long?,
        ) = Unit

        override fun auditSettlement(
            gameId: Long,
            rawGamers: List<Gamer>,
            aggregatedGamers: List<Gamer>,
            rules: List<Rule>,
        ) = Unit
    }
}

class DefaultCalculationAuditor(
    private val validator: CalculationInvariantValidator,
    private val reporter: CalculationAuditReporter,
    private val deduplicator: AuditDeduplicator = AuditDeduplicator(),
) : CalculationAuditor {

    override suspend fun auditRound(
        phase: AuditPhase,
        gameId: Long,
        roundId: Long,
        gamers: List<Gamer>,
        rules: List<Rule>,
        trace: RoundTrace?,
        explicitSellerId: Long?,
        explicitWinnerId: Long?,
    ) {
        try {
            val report = validator.validateRound(
                phase = phase,
                gameId = gameId,
                roundId = roundId,
                gamers = gamers,
                rules = rules,
                trace = trace,
                explicitSellerId = explicitSellerId,
                explicitWinnerId = explicitWinnerId,
            )
            if (report != null && deduplicator.shouldReport(report.dedupeKey)) {
                reporter.report(report)
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (t: Throwable) {
            logAuditFallback(
                tag = "CalculationAudit",
                message = "Auditor internal error: ${t::class.simpleName}: ${t.message}",
            )
        }
    }

    override fun auditSettlement(
        gameId: Long,
        rawGamers: List<Gamer>,
        aggregatedGamers: List<Gamer>,
        rules: List<Rule>,
    ) {
        try {
            val report = validator.validateSettlement(
                gameId = gameId,
                rawGamers = rawGamers,
                aggregatedGamers = aggregatedGamers,
                rules = rules,
            )
            if (report != null && deduplicator.shouldReport(report.dedupeKey)) {
                reporter.report(report)
            }
        } catch (t: Throwable) {
            logAuditFallback(
                tag = "CalculationAudit",
                message = "Auditor internal error: ${t::class.simpleName}: ${t.message}",
            )
        }
    }
}
