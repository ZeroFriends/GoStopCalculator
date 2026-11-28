package zero.friends.domain.usecase.calculate.audit

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.CalculationFactors
import zero.friends.domain.model.CoveredLoserDetail
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.Target
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.usecase.calculate.AggregateGamerResultsUseCase
import zero.friends.domain.usecase.calculate.BuildRoundTraceUseCase
import zero.friends.domain.usecase.calculate.CalculateGameResultUseCase
import zero.friends.domain.usecase.calculate.CalculateLoserScoreUseCase
import zero.friends.domain.usecase.calculate.CalculateScoreOptionUseCase
import zero.friends.domain.usecase.calculate.CalculateSellScoreUseCase
import zero.friends.domain.usecase.calculate.UpdateAccountUseCase
import zero.friends.domain.util.Const

class CalculationInvariantValidatorTest {

    private val validator = CalculationInvariantValidator()

    private fun defaultRules(score: Int = 100, fuck: Int = 500, sell: Int = 500): List<Rule> = listOf(
        Rule(name = Const.Rule.Score, score = score),
        Rule(name = Const.Rule.Fuck, score = fuck),
        Rule(name = Const.Rule.Sell, score = sell),
    )

    private suspend fun executeRound(
        gameId: Long = 1L,
        roundId: Long = 1L,
        rules: List<Rule> = defaultRules(),
        initialGamers: List<Gamer>,
        seller: Gamer? = null,
        winner: Gamer? = null,
    ): Pair<List<Gamer>, RoundTrace?> {
        val ruleRepo = MockRuleRepository().apply { setRules(gameId, rules) }
        val gamerRepo = MockGamerRepository().apply { setRoundGamers(roundId, initialGamers) }
        val calculateLoserScore = CalculateLoserScoreUseCase(ruleRepo, gamerRepo)
        val calculateUseCase = CalculateGameResultUseCase(
            ruleRepository = ruleRepo,
            gamerRepository = gamerRepo,
            updateAccountUseCase = UpdateAccountUseCase(gamerRepo),
            calculateSellScoreUseCase = CalculateSellScoreUseCase(ruleRepo, gamerRepo),
            calculateLoserScoreUseCase = calculateLoserScore,
            calculateScoreOptionUseCase = CalculateScoreOptionUseCase(ruleRepo, gamerRepo),
        )
        val buildTrace = BuildRoundTraceUseCase(ruleRepo, gamerRepo, calculateLoserScore)
        calculateUseCase(gameId = gameId, roundId = roundId, seller = seller, winner = winner)
        val savedGamers = gamerRepo.getRoundGamers(roundId)
        val trace = buildTrace(gameId = gameId, roundId = roundId)
        return savedGamers to trace
    }

    @Test
    fun `유효한 2인 3인 4인 정산은 위반이 전혀 없어야 한다`() = runTest {
        val rules = defaultRules(score = 100, fuck = 500, sell = 500)

        // 2인전 맞고 (고박 + 피박)
        val p1 = Gamer(id = 1, playerId = 11, name = "A", score = 5, go = 2, winnerOption = WinnerOption.Winner)
        val p2 = Gamer(id = 2, playerId = 12, name = "B", loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak))
        val (gamers2, trace2) = executeRound(gameId = 1, roundId = 1, rules = rules, initialGamers = listOf(p1, p2), winner = p1)
        assertNull(validator.validateRound(AuditPhase.ROUND_CALCULATE, 1, 1, gamers2, rules, trace2))

        // 3인전 (고박 독박 + 광박 + 첫뻑 + 첫따닥)
        val a = Gamer(id = 1, playerId = 101, name = "A", score = 4, go = 1, winnerOption = WinnerOption.Winner, scoreOption = listOf(ScoreOption.FirstDdadak))
        val b = Gamer(id = 2, playerId = 102, name = "B", loserOption = listOf(LoserOption.LightBak), scoreOption = listOf(ScoreOption.FirstFuck))
        val c = Gamer(id = 3, playerId = 103, name = "C", loserOption = listOf(LoserOption.GoBak, LoserOption.PeaBak))
        val (gamers3, trace3) = executeRound(gameId = 2, roundId = 2, rules = rules, initialGamers = listOf(a, b, c), winner = a)
        assertNull(validator.validateRound(AuditPhase.ROUND_CALCULATE, 2, 2, gamers3, rules, trace3))

        // 4인전 (광팔기 + 연뻑 + 고박)
        val s = Gamer(id = 1, playerId = 201, name = "S", score = 3, sellerOption = SellerOption.Seller)
        val w = Gamer(id = 2, playerId = 202, name = "W", score = 7, go = 3, winnerOption = WinnerOption.Winner)
        val l1 = Gamer(id = 3, playerId = 203, name = "L1", scoreOption = listOf(ScoreOption.SecondFuck), loserOption = listOf(LoserOption.MongBak))
        val l2 = Gamer(id = 4, playerId = 204, name = "L2", loserOption = listOf(LoserOption.GoBak))
        val (gamers4, trace4) = executeRound(gameId = 3, roundId = 3, rules = rules, initialGamers = listOf(s, w, l1, l2), seller = s, winner = w)
        assertNull(validator.validateRound(AuditPhase.ROUND_CALCULATE, 3, 3, gamers4, rules, trace4))
    }

    @Test
    fun `승자가 상대 뻑이나 광팔기 때문에 순손실이 발생해도 정상 케이스로 오탐하지 않는다`() = runTest {
        val rules = defaultRules(score = 100, fuck = 500, sell = 500)

        // 케이스 A: 승자 3점(0고) = +600원, 패자1 연뻑(+1000*2, 승자에게 -1000) -> 승자 최종 -400원
        val w1 = Gamer(id = 1, playerId = 1, name = "W", score = 3, winnerOption = WinnerOption.Winner)
        val l1 = Gamer(id = 2, playerId = 2, name = "L1", scoreOption = listOf(ScoreOption.SecondFuck))
        val l2 = Gamer(id = 3, playerId = 3, name = "L2")
        val (gamersA, traceA) = executeRound(gameId = 1, roundId = 1, rules = rules, initialGamers = listOf(w1, l1, l2), winner = w1)
        assertTrue(gamersA.first { it.id == 1L }.account < 0)
        assertNull(validator.validateRound(AuditPhase.ROUND_CALCULATE, 1, 1, gamersA, rules, traceA))

        // 케이스 B: 광팔기 3장(-1500) + 승자 3점(+600) -> 승자 최종 -900원
        val s2 = Gamer(id = 1, playerId = 11, name = "S", score = 3, sellerOption = SellerOption.Seller)
        val w2 = Gamer(id = 2, playerId = 12, name = "W", score = 3, winnerOption = WinnerOption.Winner)
        val l3 = Gamer(id = 3, playerId = 13, name = "L3")
        val l4 = Gamer(id = 4, playerId = 14, name = "L4")
        val (gamersB, traceB) = executeRound(gameId = 2, roundId = 2, rules = rules, initialGamers = listOf(s2, w2, l3, l4), seller = s2, winner = w2)
        assertTrue(gamersB.first { it.id == 2L }.account < 0)
        assertNull(validator.validateRound(AuditPhase.ROUND_CALCULATE, 2, 2, gamersB, rules, traceB))
    }

    @Test
    fun `계산 전 초기 라운드는 상세 조회 시 오탐하지 않는다`() {
        val gamers = listOf(
            Gamer(id = 1, playerId = 1, name = "P1"),
            Gamer(id = 2, playerId = 2, name = "P2"),
            Gamer(id = 3, playerId = 3, name = "P3"),
        )
        assertNull(validator.validateRound(AuditPhase.ROUND_DETAIL, 1L, 1L, gamers, defaultRules(), trace = null))
    }

    @Test
    fun `v1_4_x 구규칙 고박 라운드와 v1_4_0 로컬 gamerId 타겟도 정상 통과한다`() = runTest {
        val rules = defaultRules(score = 100, fuck = 500, sell = 500)
        val ruleRepo = MockRuleRepository().apply { setRules(1L, rules) }
        val gamerRepo = MockGamerRepository()

        // 구버전: 고박(2배) + 다른 패자 200원 대납 -> 고박자 400 + 200 = -600 (타겟 playerId에 라운드 로컬 id 저장)
        val winner = Gamer(
            id = 10,
            playerId = 101,
            roundId = 5,
            name = "A",
            score = 2,
            account = 600,
            winnerOption = WinnerOption.Winner,
            calculate = listOf(Target(playerId = 30, name = "C", account = 600)),
        )
        val normalLoser = Gamer(
            id = 20,
            playerId = 102,
            roundId = 5,
            name = "B",
            account = 0,
            calculate = emptyList(),
        )
        val goBakLoser = Gamer(
            id = 30,
            playerId = 103,
            roundId = 5,
            name = "C",
            account = -600,
            loserOption = listOf(LoserOption.GoBak),
            calculate = listOf(Target(playerId = 10, name = "A", account = -600)),
        )
        gamerRepo.setRoundGamers(5, listOf(winner, normalLoser, goBakLoser))
        val trace = BuildRoundTraceUseCase(ruleRepo, gamerRepo, CalculateLoserScoreUseCase(ruleRepo, gamerRepo)).invoke(1L, 5L)

        assertNull(validator.validateRound(AuditPhase.ROUND_DETAIL, 1L, 5L, listOf(winner, normalLoser, goBakLoser), rules, trace))
    }

    @Test
    fun `동명이인 플레이어도 playerId가 다르면 정확히 매칭되어 오탐하지 않는다`() {
        val gamers = listOf(
            Gamer(
                id = 1,
                playerId = 10,
                name = "홍길동",
                score = 3,
                account = 300,
                winnerOption = WinnerOption.Winner,
                calculate = listOf(Target(playerId = 20, name = "홍길동", account = 300)),
            ),
            Gamer(
                id = 2,
                playerId = 20,
                name = "홍길동",
                account = -300,
                calculate = listOf(Target(playerId = 10, name = "홍길동", account = -300)),
            ),
        )
        val trace = RoundTrace(
            roundId = 1L,
            gameId = 1L,
            winnerId = 1L,
            totalAmount = 300,
            terms = listOf(
                RoundTraceTerm(1L, "홍길동", 300, CalculationFactors(false, 3, 0, 100, 0, 0, 0, 0, 1)),
                RoundTraceTerm(2L, "홍길동", -300, CalculationFactors(false, 3, 0, 100, 0, 0, 0, 0, 1)),
            ),
        )
        assertNull(validator.validateRound(AuditPhase.ROUND_CALCULATE, 1L, 1L, gamers, defaultRules(), trace))
    }

    @Test
    fun `주입된 계산 위반 코드가 모두 정확히 탐지된다`() {
        val rules = defaultRules(score = 100, fuck = 500, sell = 500)

        // 1. ZERO_SUM_BROKEN + TARGET_SUM_MISMATCH + PAIR_ASYMMETRY
        val brokenZero = listOf(
            Gamer(id = 1, playerId = 1, name = "A", score = 3, account = 500, winnerOption = WinnerOption.Winner, calculate = listOf(Target(2, "B", 300))),
            Gamer(id = 2, playerId = 2, name = "B", account = -300, calculate = listOf(Target(1, "A", -200))),
        )
        val report1 = validator.validateRound(AuditPhase.ROUND_CALCULATE, 1, 1, brokenZero, rules, trace = null)
        assertNotNull(report1)
        val codes1 = report1.violations.map { it.code }.toSet()
        assertTrue(CalculationViolationCode.ZERO_SUM_BROKEN in codes1)
        assertTrue(CalculationViolationCode.TARGET_SUM_MISMATCH in codes1)
        assertTrue(CalculationViolationCode.PAIR_ASYMMETRY in codes1)
        assertTrue(CalculationViolationCode.TRACE_TOTAL_MISMATCH in codes1)

        // 2. 미확인/자기참조 타겟도 PAIR_ASYMMETRY 탐지
        val unresolvedTarget = listOf(
            Gamer(id = 1, playerId = 1, name = "A", score = 3, account = 300, winnerOption = WinnerOption.Winner, calculate = listOf(Target(999, "Ghost", 300))),
            Gamer(id = 2, playerId = 2, name = "B", account = -300, calculate = listOf(Target(1, "A", -300))),
        )
        val reportUnresolved = validator.validateRound(AuditPhase.ROUND_DETAIL, 1, 1, unresolvedTarget, rules, trace = null)
        assertNotNull(reportUnresolved)
        assertTrue(reportUnresolved.violations.any { it.code == CalculationViolationCode.PAIR_ASYMMETRY })

        // 3. SELLER_ROLE_CONTAMINATION + SELLER_PAYOUT_MISMATCH
        val badSeller = listOf(
            Gamer(
                id = 1,
                playerId = 1,
                name = "S",
                score = 2,
                account = 500,
                sellerOption = SellerOption.Seller,
                winnerOption = WinnerOption.Winner,
                loserOption = listOf(LoserOption.PeaBak),
                scoreOption = listOf(ScoreOption.FirstFuck),
                calculate = listOf(Target(2, "B", 500)),
            ),
            Gamer(id = 2, playerId = 2, name = "B", account = -500, calculate = listOf(Target(1, "S", -500))),
        )
        val reportSeller = validator.validateRound(AuditPhase.ROUND_CALCULATE, 1, 1, badSeller, rules, trace = null)
        assertNotNull(reportSeller)
        val sellerCodes = reportSeller.violations.map { it.code }.toSet()
        assertTrue(CalculationViolationCode.SELLER_ROLE_CONTAMINATION in sellerCodes)
        assertTrue(CalculationViolationCode.SELLER_PAYOUT_MISMATCH in sellerCodes)

        // 4. GO_BAK_SOLO_BREACH (3인 이상에서 비고박 패자가 판 점수 1000원 지불)
        val goBakBreach = listOf(
            Gamer(
                id = 1,
                playerId = 1,
                name = "W",
                score = 10,
                account = 2000,
                winnerOption = WinnerOption.Winner,
                calculate = listOf(Target(2, "L1", 1000), Target(3, "L2", 1000)),
            ),
            Gamer(
                id = 2,
                playerId = 2,
                name = "L1",
                account = -1000,
                calculate = listOf(Target(1, "W", -1000)),
            ),
            Gamer(
                id = 3,
                playerId = 3,
                name = "L2",
                account = -1000,
                loserOption = listOf(LoserOption.GoBak),
                calculate = listOf(Target(1, "W", -1000)),
            ),
        )
        val reportGoBak = validator.validateRound(AuditPhase.ROUND_CALCULATE, 1, 1, goBakBreach, rules, trace = null)
        assertNotNull(reportGoBak)
        assertTrue(reportGoBak.violations.any { it.code == CalculationViolationCode.GO_BAK_SOLO_BREACH })

        // 5. THREE_FUCK_PAYOUT_MISMATCH
        val badThreeFuck = listOf(
            Gamer(
                id = 1,
                playerId = 1,
                name = "T",
                account = 2000,
                scoreOption = listOf(ScoreOption.ThreeFuck),
                calculate = listOf(Target(2, "O1", 1000), Target(3, "O2", 1000)),
            ),
            Gamer(id = 2, playerId = 2, name = "O1", account = -1000, calculate = listOf(Target(1, "T", -1000))),
            Gamer(id = 3, playerId = 3, name = "O2", account = -1000, calculate = listOf(Target(1, "T", -1000))),
        )
        val reportThreeFuck = validator.validateRound(AuditPhase.ROUND_CALCULATE, 1, 1, badThreeFuck, rules, trace = null)
        assertNotNull(reportThreeFuck)
        assertTrue(reportThreeFuck.violations.any { it.code == CalculationViolationCode.THREE_FUCK_PAYOUT_MISMATCH })

        // 6. BASE_PAYOUT_SIGN_INVALID + TRACE_TOTAL_MISMATCH (트레이스 수치 불일치)
        val badSign = listOf(
            Gamer(
                id = 1,
                playerId = 1,
                name = "W",
                score = 3,
                account = -300,
                winnerOption = WinnerOption.Winner,
                calculate = listOf(Target(2, "L", -300)),
            ),
            Gamer(
                id = 2,
                playerId = 2,
                name = "L",
                account = 300,
                calculate = listOf(Target(1, "W", 300)),
            ),
        )
        val mismatchTrace = RoundTrace(
            roundId = 1L,
            gameId = 1L,
            winnerId = 1L,
            totalAmount = 300,
            terms = listOf(
                RoundTraceTerm(1L, "W", 300, CalculationFactors(false, 3, 0, 100, 0, 0, 0, 0, 1)),
                RoundTraceTerm(2L, "L", -300, CalculationFactors(false, 3, 0, 100, 0, 0, 0, 0, 1)),
            ),
        )
        val reportSign = validator.validateRound(AuditPhase.ROUND_CALCULATE, 1, 1, badSign, rules, mismatchTrace)
        assertNotNull(reportSign)
        val signCodes = reportSign.violations.map { it.code }.toSet()
        assertTrue(CalculationViolationCode.BASE_PAYOUT_SIGN_INVALID in signCodes)
        assertTrue(CalculationViolationCode.TRACE_TOTAL_MISMATCH in signCodes)
    }

    @Test
    fun `누적 정산 검증은 정상 병합 시 null을 반환하고 드리프트 및 불일치를 탐지한다`() {
        val r1g1 = Gamer(id = 1, roundId = 10, playerId = 1, name = "P1", account = 1000, calculate = listOf(Target(2, "P2", 1000)))
        val r1g2 = Gamer(id = 2, roundId = 10, playerId = 2, name = "P2", account = -1000, calculate = listOf(Target(1, "P1", -1000)))
        val r2g1 = Gamer(id = 3, roundId = 20, playerId = 1, name = "P1", account = -500, calculate = listOf(Target(2, "P2", -500)))
        val r2g2 = Gamer(id = 4, roundId = 20, playerId = 2, name = "P2", account = 500, calculate = listOf(Target(1, "P1", 500)))
        val raw = listOf(r1g1, r1g2, r2g1, r2g2)
        val aggregated = AggregateGamerResultsUseCase().invoke(raw)

        assertNull(validator.validateSettlement(gameId = 7L, rawGamers = raw, aggregatedGamers = aggregated, rules = defaultRules()))

        // 깨진 라운드/집계 드리프트 주입
        val brokenRaw = listOf(
            r1g1,
            r1g2,
            r2g1.copy(account = -900),
            r2g2,
        )
        val driftAggregated = listOf(
            aggregated[0].copy(account = 999),
            aggregated[1],
        )
        val settlementReport = validator.validateSettlement(
            gameId = 7L,
            rawGamers = brokenRaw,
            aggregatedGamers = driftAggregated,
            rules = defaultRules(),
        )
        assertNotNull(settlementReport)
        val codes = settlementReport.violations.map { it.code }.toSet()
        assertTrue(CalculationViolationCode.ZERO_SUM_BROKEN in codes)
        assertTrue(CalculationViolationCode.AGGREGATION_TOTAL_DRIFT in codes)
        assertTrue(CalculationViolationCode.TARGET_SUM_MISMATCH in codes)
        assertTrue(settlementReport.diagnosticPayload.contains("round=20"))
    }

    @Test
    fun `리포트 요약과 페이로드와 커스텀 키에 실제 플레이어 이름이 절대 포함되지 않는다`() {
        val sensitiveNames = listOf("홍길동", "김철수", "이영희", "박민수")
        val roundGamers = listOf(
            Gamer(
                id = 1,
                roundId = 101,
                playerId = 10,
                name = "홍길동",
                score = 5,
                account = 2500,
                winnerOption = WinnerOption.Winner,
                calculate = listOf(Target(20, "김철수", 1000)),
            ),
            Gamer(
                id = 2,
                roundId = 101,
                playerId = 20,
                name = "김철수",
                account = -1000,
                loserOption = listOf(LoserOption.GoBak),
                calculate = listOf(Target(10, "홍길동", -900)),
            ),
            Gamer(
                id = 3,
                roundId = 101,
                playerId = 30,
                name = "이영희",
                account = -500,
                calculate = listOf(Target(10, "홍길동", -500)),
            ),
            Gamer(
                id = 4,
                roundId = 101,
                playerId = 40,
                name = "박민수",
                score = 1,
                account = 300,
                sellerOption = SellerOption.Seller,
                calculate = listOf(Target(10, "홍길동", 300)),
            ),
        )
        val traceWithNames = RoundTrace(
            roundId = 101,
            gameId = 1,
            winnerId = 1,
            totalAmount = 2500,
            terms = listOf(
                RoundTraceTerm(1, "홍길동", 2000, CalculationFactors(false, 5, 0, 100, 0, 0, 0, 0, 2), playerName = "홍길동"),
                RoundTraceTerm(
                    2,
                    "김철수",
                    -1000,
                    CalculationFactors(
                        isSeller = false,
                        baseScore = 5,
                        goCount = 0,
                        scorePerPoint = 100,
                        bakCount = 0,
                        fuckAmount = 0,
                        ddadakAmount = 0,
                        sellAmount = 0,
                        receiverCount = 1,
                        goBakPayerName = "김철수",
                        isSoloGoBakMode = true,
                        coveredLosers = listOf(CoveredLoserDetail(playerName = "이영희", amount = -500)),
                    ),
                    playerName = "김철수",
                ),
            ),
        )

        val roundReport = validator.validateRound(AuditPhase.ROUND_DETAIL, 1L, 101L, roundGamers, defaultRules(), traceWithNames)
        assertNotNull(roundReport)

        val settlementReport = validator.validateSettlement(
            gameId = 1L,
            rawGamers = roundGamers,
            aggregatedGamers = roundGamers,
            rules = defaultRules(),
        )
        assertNotNull(settlementReport)

        listOf(roundReport, settlementReport).forEach { report ->
            val corpus = buildString {
                appendLine(report.diagnosticSummary)
                appendLine(report.diagnosticPayload)
                report.customKeys.forEach { (k, v) ->
                    appendLine(k)
                    appendLine(v)
                }
                report.violations.forEach { appendLine(it.message) }
            }
            sensitiveNames.forEach { secret ->
                assertFalse(corpus.contains(secret), "Sensitive player name '$secret' leaked in audit report!")
            }
            assertTrue(report.diagnosticPayload.contains("P1"))
            assertTrue(report.diagnosticPayload.contains("P2"))
        }
    }

    @Test
    fun `흔들기 및 폭탄 배수가 반영된 정산 라운드는 감사 위반 없이 정상 통과한다`() = runTest {
        val rules = defaultRules(score = 100, fuck = 500, sell = 500)
        val ruleRepo = MockRuleRepository().apply { setRules(1L, rules) }
        val gamerRepo = MockGamerRepository()

        val initial = listOf(
            Gamer(id = 1, playerId = 11, roundId = 9, name = "승자", score = 5, go = 0, shakeCount = 1, bombCount = 1, winnerOption = WinnerOption.Winner),
            Gamer(id = 2, playerId = 12, roundId = 9, name = "패자1"),
            Gamer(id = 3, playerId = 13, roundId = 9, name = "패자2", loserOption = listOf(LoserOption.PeaBak)),
        )
        gamerRepo.setRoundGamers(9L, initial)
        val loserUseCase = CalculateLoserScoreUseCase(ruleRepo, gamerRepo)
        val calculateUseCase = CalculateGameResultUseCase(
            ruleRepository = ruleRepo,
            gamerRepository = gamerRepo,
            updateAccountUseCase = UpdateAccountUseCase(gamerRepo),
            calculateSellScoreUseCase = CalculateSellScoreUseCase(ruleRepo, gamerRepo),
            calculateLoserScoreUseCase = loserUseCase,
            calculateScoreOptionUseCase = CalculateScoreOptionUseCase(ruleRepo, gamerRepo),
        )
        calculateUseCase(gameId = 1L, roundId = 9L, seller = null, winner = initial.first())

        val settledGamers = gamerRepo.getRoundGamers(9L)
        val trace = BuildRoundTraceUseCase(ruleRepo, gamerRepo, loserUseCase).invoke(1L, 9L)
        assertNull(validator.validateRound(AuditPhase.ROUND_CALCULATE, 1L, 9L, settledGamers, rules, trace))
    }
}
