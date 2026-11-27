package zero.friends.domain.usecase.calculate.audit

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.Target
import zero.friends.domain.usecase.calculate.isGoBakSoloCoverageMode
import zero.friends.domain.util.Const

class CalculationInvariantValidator {

    fun validateRound(
        phase: AuditPhase,
        gameId: Long,
        roundId: Long,
        gamers: List<Gamer>,
        rules: List<Rule>,
        trace: RoundTrace? = null,
        explicitSellerId: Long? = null,
        explicitWinnerId: Long? = null,
    ): CalculationAuditReport? {
        if (gamers.isEmpty()) return null

        val aliasByIndex = buildRoundAliases(gamers)
        val sellers = gamers.filter { it.sellerOption != null }
        val seller = sellers.firstOrNull()
            ?: explicitSellerId?.let { id -> gamers.firstOrNull { it.id == id } }
        val declaredWinner = gamers.firstOrNull { it.winnerOption != null }
            ?: explicitWinnerId?.let { id -> gamers.firstOrNull { it.id == id } }

        val hasSettledMoney = gamers.any { it.account != 0 || it.calculate.isNotEmpty() }
        val shouldCheckSettledRules = phase == AuditPhase.ROUND_CALCULATE || hasSettledMoney

        val scoreRule = rules.firstOrNull { it.name == Const.Rule.Score }?.score ?: 0
        val fuckRule = rules.firstOrNull { it.name == Const.Rule.Fuck }?.score ?: 0
        val sellRule = rules.firstOrNull { it.name == Const.Rule.Sell }?.score ?: 0

        val violations = mutableListOf<CalculationViolation>()

        val totalSum = gamers.sumOf { it.account.toLong() }
        if (totalSum != 0L) {
            violations += CalculationViolation(
                code = CalculationViolationCode.ZERO_SUM_BROKEN,
                message = "Round zero-sum broken: expected 0, actual $totalSum",
                expected = 0L,
                actual = totalSum,
            )
        }

        gamers.forEachIndexed { idx, gamer ->
            val alias = aliasByIndex[idx]
            val targetSum = gamer.calculate.sumOf { it.account.toLong() }
            if (targetSum != gamer.account.toLong()) {
                violations += CalculationViolation(
                    code = CalculationViolationCode.TARGET_SUM_MISMATCH,
                    message = "$alias ledger mismatch: account=${gamer.account}, targetsSum=$targetSum",
                    expected = gamer.account.toLong(),
                    actual = targetSum,
                )
            }
        }

        checkRoundPairSymmetry(gamers, aliasByIndex, violations)

        if (sellers.size > 1) {
            violations += CalculationViolation(
                code = CalculationViolationCode.SELLER_ROLE_CONTAMINATION,
                message = "Multiple sellers detected: count=${sellers.size}",
                expected = 1L,
                actual = sellers.size.toLong(),
            )
        }
        if (seller != null && (seller.winnerOption != null || seller.loserOption.isNotEmpty() || seller.scoreOption.isNotEmpty())) {
            val sellerAlias = aliasOfGamer(gamers, aliasByIndex, seller)
            violations += CalculationViolation(
                code = CalculationViolationCode.SELLER_ROLE_CONTAMINATION,
                message = "Seller $sellerAlias carries incompatible options (winner=${seller.winnerOption != null}, loser=${seller.loserOption.size}, scoreOpt=${seller.scoreOption.size})",
            )
        }

        if (shouldCheckSettledRules) {
            if (trace != null) {
                val traceSum = trace.terms.sumOf { it.amount.toLong() }
                if (traceSum != 0L) {
                    violations += CalculationViolation(
                        code = CalculationViolationCode.TRACE_TOTAL_MISMATCH,
                        message = "RoundTrace sum is non-zero: $traceSum",
                        expected = 0L,
                        actual = traceSum,
                    )
                }
                val traceByGamerId = trace.terms.associateBy { it.gamerId }
                gamers.forEachIndexed { idx, gamer ->
                    val alias = aliasByIndex[idx]
                    val traceAmount = traceByGamerId[gamer.id]?.amount ?: 0
                    if (traceAmount != gamer.account) {
                        violations += CalculationViolation(
                            code = CalculationViolationCode.TRACE_TOTAL_MISMATCH,
                            message = "$alias trace amount mismatch: account=${gamer.account}, trace=$traceAmount",
                            expected = gamer.account.toLong(),
                            actual = traceAmount.toLong(),
                        )
                    }
                }
                val ghostTerms = trace.terms.filter { term ->
                    term.amount != 0 && gamers.none { it.id == term.gamerId }
                }
                if (ghostTerms.isNotEmpty()) {
                    violations += CalculationViolation(
                        code = CalculationViolationCode.TRACE_TOTAL_MISMATCH,
                        message = "RoundTrace contains ${ghostTerms.size} unknown gamer terms",
                    )
                }
            } else if (gamers.any { it.account != 0 }) {
                violations += CalculationViolation(
                    code = CalculationViolationCode.TRACE_TOTAL_MISMATCH,
                    message = "RoundTrace is null despite non-zero round accounts",
                )
            }

            if (seller != null) {
                val sellerAlias = aliasOfGamer(gamers, aliasByIndex, seller)
                val expectedSellerPayout = sellRule.toLong() * seller.score.toLong() * (gamers.size - 1).coerceAtLeast(0).toLong()
                if (seller.account.toLong() != expectedSellerPayout) {
                    violations += CalculationViolation(
                        code = CalculationViolationCode.SELLER_PAYOUT_MISMATCH,
                        message = "Seller $sellerAlias payout mismatch: expected=$expectedSellerPayout, actual=${seller.account}",
                        expected = expectedSellerPayout,
                        actual = seller.account.toLong(),
                    )
                }
            }

            val activeGamers = if (seller != null) gamers.filter { it.id != seller.id } else gamers

            fun auxiliaryLong(targetGamer: Gamer): Long {
                if (seller != null && targetGamer.id == seller.id) {
                    return sellRule.toLong() * seller.score.toLong() * (gamers.size - 1).coerceAtLeast(0).toLong()
                }
                var aux = if (seller != null) -(sellRule.toLong() * seller.score.toLong()) else 0L
                activeGamers.forEach { owner ->
                    val otherCount = (activeGamers.size - 1).coerceAtLeast(0).toLong()
                    owner.scoreOption.forEach { option ->
                        val amount = option.calculateScore(fuckRule, scoreRule).toLong()
                        if (owner.id == targetGamer.id) {
                            aux += amount * otherCount
                        } else {
                            aux -= amount
                        }
                    }
                }
                return aux
            }

            if (declaredWinner != null) {
                val activeLosers = gamers.filter { it.id != declaredWinner.id && it.id != seller?.id }
                val goBakPayer = activeLosers.firstOrNull { it.loserOption.contains(LoserOption.GoBak) }
                if (isGoBakSoloCoverageMode(activeLosers.size) && goBakPayer != null) {
                    activeLosers
                        .filter { it.id != goBakPayer.id }
                        .forEach { otherLoser ->
                            val impliedBase = otherLoser.account.toLong() - auxiliaryLong(otherLoser)
                            if (impliedBase != 0L) {
                                val alias = aliasOfGamer(gamers, aliasByIndex, otherLoser)
                                violations += CalculationViolation(
                                    code = CalculationViolationCode.GO_BAK_SOLO_BREACH,
                                    message = "Non-GoBak loser $alias paid non-zero base amount $impliedBase under solo GoBak",
                                    expected = 0L,
                                    actual = impliedBase,
                                )
                            }
                        }
                }
            }

            if (declaredWinner == null && activeGamers.any { it.scoreOption.contains(ScoreOption.ThreeFuck) }) {
                activeGamers.forEach { activeGamer ->
                    val expected = auxiliaryLong(activeGamer)
                    if (activeGamer.account.toLong() != expected) {
                        val alias = aliasOfGamer(gamers, aliasByIndex, activeGamer)
                        violations += CalculationViolation(
                            code = CalculationViolationCode.THREE_FUCK_PAYOUT_MISMATCH,
                            message = "$alias ThreeFuck payout mismatch: expected=$expected, actual=${activeGamer.account}",
                            expected = expected,
                            actual = activeGamer.account.toLong(),
                        )
                    }
                }
            }

            if (declaredWinner != null && scoreRule >= 0 && declaredWinner.score >= 0) {
                val winnerBase = declaredWinner.account.toLong() - auxiliaryLong(declaredWinner)
                if (winnerBase < 0L) {
                    val winnerAlias = aliasOfGamer(gamers, aliasByIndex, declaredWinner)
                    violations += CalculationViolation(
                        code = CalculationViolationCode.BASE_PAYOUT_SIGN_INVALID,
                        message = "Winner $winnerAlias base payout is negative: $winnerBase",
                        expected = 0L,
                        actual = winnerBase,
                    )
                }
                gamers
                    .filter { it.id != declaredWinner.id && it.id != seller?.id }
                    .forEach { loser ->
                        val loserBase = loser.account.toLong() - auxiliaryLong(loser)
                        if (loserBase > 0L) {
                            val loserAlias = aliasOfGamer(gamers, aliasByIndex, loser)
                            violations += CalculationViolation(
                                code = CalculationViolationCode.BASE_PAYOUT_SIGN_INVALID,
                                message = "Loser $loserAlias base payout is positive: $loserBase",
                                expected = 0L,
                                actual = loserBase,
                            )
                        }
                    }
            }
        }

        if (violations.isEmpty()) return null

        val violationSignature = buildViolationSignature(violations)
        val summary = "[${phase.key}] $violationSignature (players=${gamers.size}) - ${violations.first().message}"
        val customKeys = linkedMapOf(
            "calc_audit_phase" to phase.key,
            "calc_audit_violations" to violationSignature,
            "calc_audit_game_id" to gameId.toString(),
            "calc_audit_round_id" to roundId.toString(),
            "calc_audit_players" to gamers.size.toString(),
            "calc_audit_rounds" to "1",
            "calc_audit_rules" to "score=$scoreRule,fuck=$fuckRule,sell=$sellRule",
            "calc_audit_zero_sum_delta" to totalSum.toString(),
            "calc_audit_summary" to summary.take(MAX_SUMMARY_KEY_LENGTH),
        )
        val payload = buildRoundPayload(
            phase = phase,
            gameId = gameId,
            roundId = roundId,
            gamers = gamers,
            aliasByIndex = aliasByIndex,
            seller = seller,
            declaredWinner = declaredWinner,
            scoreRule = scoreRule,
            fuckRule = fuckRule,
            sellRule = sellRule,
            violations = violations,
            trace = trace,
        )

        return CalculationAuditReport(
            phase = phase,
            gameId = gameId,
            roundId = roundId,
            playerCount = gamers.size,
            roundCount = 1,
            violations = violations,
            customKeys = customKeys,
            diagnosticSummary = summary,
            diagnosticPayload = payload,
        )
    }

    fun validateSettlement(
        gameId: Long,
        rawGamers: List<Gamer>,
        aggregatedGamers: List<Gamer>,
        rules: List<Rule> = emptyList(),
    ): CalculationAuditReport? {
        if (rawGamers.isEmpty() && aggregatedGamers.isEmpty()) return null

        val aliasMap = buildSettlementAliasMap(rawGamers, aggregatedGamers)
        val aggregatedAliases = aggregatedGamers.map { aliasMap.aliasOf(it) }
        val roundGroups = rawGamers.groupBy { it.roundId }

        val scoreRule = rules.firstOrNull { it.name == Const.Rule.Score }?.score ?: 0
        val fuckRule = rules.firstOrNull { it.name == Const.Rule.Fuck }?.score ?: 0
        val sellRule = rules.firstOrNull { it.name == Const.Rule.Sell }?.score ?: 0

        val violations = mutableListOf<CalculationViolation>()
        val aggregatedSum = aggregatedGamers.sumOf { it.account.toLong() }
        val rawSum = rawGamers.sumOf { it.account.toLong() }

        if (aggregatedSum != 0L) {
            violations += CalculationViolation(
                code = CalculationViolationCode.ZERO_SUM_BROKEN,
                message = "Settlement zero-sum broken: expected 0, actual $aggregatedSum",
                expected = 0L,
                actual = aggregatedSum,
            )
        }

        if (rawGamers.isNotEmpty() && rawSum != aggregatedSum) {
            violations += CalculationViolation(
                code = CalculationViolationCode.AGGREGATION_TOTAL_DRIFT,
                message = "Aggregated sum ($aggregatedSum) diverges from raw rounds sum ($rawSum)",
                expected = rawSum,
                actual = aggregatedSum,
            )
        }

        aggregatedGamers.forEachIndexed { idx, gamer ->
            val alias = aggregatedAliases[idx]
            val targetSum = gamer.calculate.sumOf { it.account.toLong() }
            if (targetSum != gamer.account.toLong()) {
                violations += CalculationViolation(
                    code = CalculationViolationCode.TARGET_SUM_MISMATCH,
                    message = "$alias settlement ledger mismatch: account=${gamer.account}, targetsSum=$targetSum",
                    expected = gamer.account.toLong(),
                    actual = targetSum,
                )
            }
        }

        checkSettlementPairSymmetry(aggregatedGamers, aggregatedAliases, violations)

        if (violations.isEmpty()) return null

        val roundCount = roundGroups.size
        val violationSignature = buildViolationSignature(violations)
        val summary = "[${AuditPhase.GAME_SETTLEMENT.key}] $violationSignature (players=${aggregatedGamers.size}, rounds=$roundCount) - ${violations.first().message}"
        val customKeys = linkedMapOf(
            "calc_audit_phase" to AuditPhase.GAME_SETTLEMENT.key,
            "calc_audit_violations" to violationSignature,
            "calc_audit_game_id" to gameId.toString(),
            "calc_audit_round_id" to "-1",
            "calc_audit_players" to aggregatedGamers.size.toString(),
            "calc_audit_rounds" to roundCount.toString(),
            "calc_audit_rules" to "score=$scoreRule,fuck=$fuckRule,sell=$sellRule",
            "calc_audit_zero_sum_delta" to aggregatedSum.toString(),
            "calc_audit_summary" to summary.take(MAX_SUMMARY_KEY_LENGTH),
        )
        val payload = buildSettlementPayload(
            gameId = gameId,
            aggregatedGamers = aggregatedGamers,
            aggregatedAliases = aggregatedAliases,
            rawGamers = rawGamers,
            roundGroups = roundGroups,
            aliasMap = aliasMap,
            scoreRule = scoreRule,
            fuckRule = fuckRule,
            sellRule = sellRule,
            rawSum = rawSum,
            aggregatedSum = aggregatedSum,
            violations = violations,
        )

        return CalculationAuditReport(
            phase = AuditPhase.GAME_SETTLEMENT,
            gameId = gameId,
            roundId = null,
            playerCount = aggregatedGamers.size,
            roundCount = roundCount,
            violations = violations,
            customKeys = customKeys,
            diagnosticSummary = summary,
            diagnosticPayload = payload,
        )
    }

    private fun checkRoundPairSymmetry(
        gamers: List<Gamer>,
        aliasByIndex: List<String>,
        violations: MutableList<CalculationViolation>,
    ) {
        val directedSum = HashMap<Pair<Int, Int>, Long>()
        gamers.forEachIndexed { fromIdx, gamer ->
            val fromAlias = aliasByIndex[fromIdx]
            gamer.calculate.forEach { target ->
                val toIdx = resolveRoundTargetIndex(target, gamers)
                if (toIdx == null || toIdx == fromIdx) {
                    if (target.account != 0) {
                        violations += CalculationViolation(
                            code = CalculationViolationCode.PAIR_ASYMMETRY,
                            message = "$fromAlias has unresolved/self target (${target.account})",
                            expected = 0L,
                            actual = target.account.toLong(),
                        )
                    }
                } else {
                    val key = fromIdx to toIdx
                    directedSum[key] = (directedSum[key] ?: 0L) + target.account.toLong()
                }
            }
        }

        for (i in gamers.indices) {
            for (j in (i + 1) until gamers.size) {
                val ab = directedSum[i to j] ?: 0L
                val ba = directedSum[j to i] ?: 0L
                if (ab + ba != 0L) {
                    violations += CalculationViolation(
                        code = CalculationViolationCode.PAIR_ASYMMETRY,
                        message = "Pair ${aliasByIndex[i]}<->${aliasByIndex[j]} asymmetry: forward=$ab, reverse=$ba",
                        expected = 0L,
                        actual = ab + ba,
                    )
                }
            }
        }
    }

    private fun checkSettlementPairSymmetry(
        aggregatedGamers: List<Gamer>,
        aggregatedAliases: List<String>,
        violations: MutableList<CalculationViolation>,
    ) {
        val directedSum = HashMap<Pair<Int, Int>, Long>()
        aggregatedGamers.forEachIndexed { fromIdx, gamer ->
            val fromAlias = aggregatedAliases[fromIdx]
            gamer.calculate.forEach { target ->
                val toIdx = resolveSettlementTargetIndex(target, aggregatedGamers)
                if (toIdx == null || toIdx == fromIdx) {
                    if (target.account != 0) {
                        violations += CalculationViolation(
                            code = CalculationViolationCode.PAIR_ASYMMETRY,
                            message = "$fromAlias has unresolved/self settlement target (${target.account})",
                            expected = 0L,
                            actual = target.account.toLong(),
                        )
                    }
                } else {
                    val key = fromIdx to toIdx
                    directedSum[key] = (directedSum[key] ?: 0L) + target.account.toLong()
                }
            }
        }

        for (i in aggregatedGamers.indices) {
            for (j in (i + 1) until aggregatedGamers.size) {
                val ab = directedSum[i to j] ?: 0L
                val ba = directedSum[j to i] ?: 0L
                if (ab + ba != 0L) {
                    violations += CalculationViolation(
                        code = CalculationViolationCode.PAIR_ASYMMETRY,
                        message = "Settlement pair ${aggregatedAliases[i]}<->${aggregatedAliases[j]} asymmetry: forward=$ab, reverse=$ba",
                        expected = 0L,
                        actual = ab + ba,
                    )
                }
            }
        }
    }

    private fun resolveRoundTargetIndex(target: Target, gamers: List<Gamer>): Int? {
        if (target.playerId != 0L) {
            if (target.name.isNotEmpty()) {
                gamers.indices.filter { idx ->
                    gamers[idx].playerId == target.playerId && gamers[idx].name == target.name
                }.singleOrNull()?.let { return it }
            }
            gamers.indices.filter { idx ->
                gamers[idx].playerId == target.playerId
            }.singleOrNull()?.let { return it }

            if (target.name.isNotEmpty()) {
                gamers.indices.filter { idx ->
                    gamers[idx].id == target.playerId && gamers[idx].name == target.name
                }.singleOrNull()?.let { return it }
            }
            gamers.indices.filter { idx ->
                gamers[idx].id == target.playerId
            }.singleOrNull()?.let { return it }
        }
        if (target.name.isNotEmpty()) {
            return gamers.indices.filter { idx -> gamers[idx].name == target.name }.singleOrNull()
        }
        return null
    }

    private fun resolveSettlementTargetIndex(target: Target, aggregatedGamers: List<Gamer>): Int? {
        if (target.playerId != 0L) {
            if (target.name.isNotEmpty()) {
                aggregatedGamers.indices.filter { idx ->
                    aggregatedGamers[idx].playerId == target.playerId && aggregatedGamers[idx].name == target.name
                }.singleOrNull()?.let { return it }
            }
            aggregatedGamers.indices.filter { idx ->
                aggregatedGamers[idx].playerId == target.playerId
            }.singleOrNull()?.let { return it }
        }
        if (target.name.isNotEmpty()) {
            return aggregatedGamers.indices.filter { idx -> aggregatedGamers[idx].name == target.name }.singleOrNull()
        }
        return null
    }

    private fun buildRoundAliases(gamers: List<Gamer>): List<String> {
        val sortedIndices = gamers.indices.sortedWith(
            compareBy(
                { idx -> if (gamers[idx].playerId != 0L) 0 else 1 },
                { idx ->
                    val g = gamers[idx]
                    when {
                        g.playerId != 0L -> g.playerId
                        g.id != 0L -> g.id
                        else -> idx.toLong()
                    }
                },
                { idx -> gamers[idx].id },
                { idx -> idx },
            )
        )
        val result = MutableList(gamers.size) { "" }
        sortedIndices.forEachIndexed { order, originalIdx ->
            result[originalIdx] = "P${order + 1}"
        }
        return result
    }

    private class SettlementAliasMap(
        private val aliasByKey: Map<String, String>,
    ) {
        fun aliasOf(gamer: Gamer): String = aliasByKey[participantKey(gamer)] ?: "UNKNOWN_GAMER"
    }

    private fun buildSettlementAliasMap(
        rawGamers: List<Gamer>,
        aggregatedGamers: List<Gamer>,
    ): SettlementAliasMap {
        data class ParticipantMeta(
            val key: String,
            val playerId: Long,
            val firstSeenOrder: Int,
        )

        val seen = linkedMapOf<String, ParticipantMeta>()
        var orderCounter = 0
        (aggregatedGamers + rawGamers).forEach { gamer ->
            val key = participantKey(gamer)
            if (key !in seen) {
                seen[key] = ParticipantMeta(
                    key = key,
                    playerId = gamer.playerId,
                    firstSeenOrder = orderCounter++,
                )
            }
        }

        val sorted = seen.values.sortedWith(
            compareBy(
                { if (it.playerId != 0L) 0 else 1 },
                { it.playerId },
                { it.firstSeenOrder },
            )
        )
        val aliasByKey = buildMap {
            sorted.forEachIndexed { idx, meta ->
                put(meta.key, "P${idx + 1}")
            }
        }
        return SettlementAliasMap(aliasByKey)
    }

    private fun aliasOfGamer(
        gamers: List<Gamer>,
        aliasByIndex: List<String>,
        target: Gamer,
    ): String {
        val exactIndex = gamers.indexOfFirst { it === target || it.id == target.id }
        return if (exactIndex in aliasByIndex.indices) aliasByIndex[exactIndex] else "UNKNOWN_GAMER"
    }

    private fun buildRoundPayload(
        phase: AuditPhase,
        gameId: Long,
        roundId: Long,
        gamers: List<Gamer>,
        aliasByIndex: List<String>,
        seller: Gamer?,
        declaredWinner: Gamer?,
        scoreRule: Int,
        fuckRule: Int,
        sellRule: Int,
        violations: List<CalculationViolation>,
        trace: RoundTrace?,
    ): String {
        val aliasByGamerId = buildMap {
            gamers.forEachIndexed { idx, gamer ->
                put(gamer.id, aliasByIndex[idx])
            }
        }

        val text = buildString {
            appendLine("phase=${phase.key}, gameId=$gameId, roundId=$roundId, players=${gamers.size}")
            appendLine("rules: score=$scoreRule, fuck=$fuckRule, sell=$sellRule")
            appendLine("violations(${violations.size}):")
            violations.forEach { v ->
                appendLine("- [${v.code.code}] ${v.message}")
            }
            appendLine("gamers:")
            gamers.forEachIndexed { idx, g ->
                val alias = aliasByIndex[idx]
                val role = when {
                    seller?.id == g.id || g.sellerOption != null -> "SELLER"
                    declaredWinner?.id == g.id || g.winnerOption != null -> "WINNER"
                    else -> "LOSER"
                }
                val targetsText = g.calculate.joinToString(",") { t ->
                    val targetAlias = resolveRoundTargetIndex(t, gamers)?.let { aliasByIndex[it] } ?: "UNKNOWN_TARGET"
                    "$targetAlias:${t.account}"
                }
                appendLine(
                    "- $alias(id=${g.id},pid=${g.playerId}): role=$role, score=${g.score}, go=${g.go}, " +
                        "scoreOpt=${g.scoreOption.map { it.name }}, loserOpt=${g.loserOption.map { it.name }}, " +
                        "account=${g.account}, targets=[$targetsText]"
                )
            }
            if (trace != null) {
                val termsText = trace.terms.joinToString(", ") { term ->
                    val alias = aliasByGamerId[term.gamerId] ?: "UNKNOWN_GAMER"
                    "$alias:${term.amount}(b=${term.factors.baseScore},g=${term.factors.goCount},bak=${term.factors.bakCount}," +
                        "f=${term.factors.fuckAmount},d=${term.factors.ddadakAmount},s=${term.factors.sellAmount})"
                }
                appendLine("trace(winnerAlias=${aliasByGamerId[trace.winnerId] ?: "NONE"}, total=${trace.totalAmount}): [$termsText]")
            } else {
                appendLine("trace: null")
            }
        }
        return capPayload(text)
    }

    private fun buildSettlementPayload(
        gameId: Long,
        aggregatedGamers: List<Gamer>,
        aggregatedAliases: List<String>,
        rawGamers: List<Gamer>,
        roundGroups: Map<Long, List<Gamer>>,
        aliasMap: SettlementAliasMap,
        scoreRule: Int,
        fuckRule: Int,
        sellRule: Int,
        rawSum: Long,
        aggregatedSum: Long,
        violations: List<CalculationViolation>,
    ): String {
        val text = buildString {
            appendLine(
                "phase=${AuditPhase.GAME_SETTLEMENT.key}, gameId=$gameId, players=${aggregatedGamers.size}, " +
                    "rounds=${roundGroups.size}, rawCount=${rawGamers.size}"
            )
            appendLine("rules: score=$scoreRule, fuck=$fuckRule, sell=$sellRule")
            appendLine("totals: rawSum=$rawSum, aggregatedSum=$aggregatedSum")
            appendLine("violations(${violations.size}):")
            violations.forEach { v ->
                appendLine("- [${v.code.code}] ${v.message}")
            }
            appendLine("aggregated:")
            aggregatedGamers.forEachIndexed { idx, g ->
                val alias = aggregatedAliases[idx]
                val targetsText = g.calculate.joinToString(",") { t ->
                    val targetAlias = resolveSettlementTargetIndex(t, aggregatedGamers)?.let { aggregatedAliases[it] } ?: "UNKNOWN_TARGET"
                    "$targetAlias:${t.account}"
                }
                appendLine("- $alias(pid=${g.playerId}): account=${g.account}, targets=[$targetsText]")
            }

            if (roundGroups.isNotEmpty()) {
                data class RoundSummary(
                    val roundId: Long,
                    val sum: Long,
                    val targetMismatchCount: Int,
                    val accountsText: String,
                    val isBroken: Boolean,
                )

                val summaries = roundGroups.entries
                    .sortedBy { it.key }
                    .map { (rId, rGamers) ->
                        val rSum = rGamers.sumOf { it.account.toLong() }
                        val mismatchCount = rGamers.count { g ->
                            g.calculate.sumOf { it.account.toLong() } != g.account.toLong()
                        }
                        val accountsText = rGamers.joinToString(",") { g ->
                            "${aliasMap.aliasOf(g)}=${g.account}"
                        }
                        RoundSummary(
                            roundId = rId,
                            sum = rSum,
                            targetMismatchCount = mismatchCount,
                            accountsText = accountsText,
                            isBroken = rSum != 0L || mismatchCount > 0,
                        )
                    }
                    .sortedWith(compareByDescending<RoundSummary> { it.isBroken }.thenBy { it.roundId })
                    .take(MAX_SETTLEMENT_ROUNDS_IN_PAYLOAD)

                appendLine("rounds_summary(shown=${summaries.size}/${roundGroups.size}):")
                summaries.forEach { rs ->
                    appendLine(
                        "- round=${rs.roundId} sum=${rs.sum} targetMismatches=${rs.targetMismatchCount} [${rs.accountsText}]"
                    )
                }
            }
        }
        return capPayload(text)
    }

    private fun buildViolationSignature(violations: List<CalculationViolation>): String =
        violations.map { it.code.code }.distinct().sorted().joinToString(",")

    private fun capPayload(raw: String): String {
        if (raw.length <= MAX_PAYLOAD_CHARS) return raw
        return raw.take(MAX_PAYLOAD_CHARS - TRUNCATED_SUFFIX.length) + TRUNCATED_SUFFIX
    }

    companion object {
        private const val MAX_SUMMARY_KEY_LENGTH = 256
        private const val MAX_PAYLOAD_CHARS = 8000
        private const val MAX_SETTLEMENT_ROUNDS_IN_PAYLOAD = 20
        private const val TRUNCATED_SUFFIX = "\n... (truncated)"

        private fun participantKey(gamer: Gamer): String =
            if (gamer.playerId != 0L) "id:${gamer.playerId}" else "name:${gamer.name}"
    }
}
