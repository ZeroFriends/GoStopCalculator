package zero.friends.domain.usecase.calculate.v2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.usecase.calculate.AggregateGamerResultsUseCase
import zero.friends.domain.usecase.calculate.BuildRoundTraceUseCase
import zero.friends.domain.usecase.calculate.CalculateGameResultUseCase
import zero.friends.domain.usecase.calculate.CalculateLoserScoreUseCase
import zero.friends.domain.usecase.calculate.CalculateScoreOptionUseCase
import zero.friends.domain.usecase.calculate.CalculateSellScoreUseCase
import zero.friends.domain.usecase.calculate.UpdateAccountUseCase
import zero.friends.domain.util.Const

/**
 * [Black-Box Combinatorial & Metamorphic Test Suite]
 *
 * 내부 구현 상세/수식 복제 없이 퍼블릭 유스케이스 경계만 호출하여
 * `score = 10` 기준 유효 조합 공간 전체에 대해 다음 블랙박스 성질을 병렬 검증한다.
 * 1. 라운드 Zero-Sum 및 원장(Ledger) · 양방향 거래 대칭성
 * 2. CalculateGameResult <-> BuildRoundTrace <-> AggregateGamerResults 3계층 정합성
 * 3. 역할 공리(광팜 격리, 고박 독박 부담, 승패 부호)
 * 4. 변성(Metamorphic) 법칙: 룰 스케일 비례, 박 추가 시 2배 증가, 고 단조 증가, 입력 순서 불변성
 */
class BlackBoxCombinatorialTest {

    private data class RuleSet(
        val scorePerPoint: Int = 100,
        val fuckScore: Int = 500,
        val sellScore: Int = 500
    )

    private data class PlayerSpec(
        val name: String,
        val isSeller: Boolean = false,
        val sellCount: Int = 0,
        val isWinner: Boolean = false,
        val goCount: Int = 0,
        val scoreOptions: List<ScoreOption> = emptyList(),
        val loserOptions: List<LoserOption> = emptyList()
    )

    private data class RoundCase(
        val tag: String,
        val rules: RuleSet = RuleSet(),
        val players: List<PlayerSpec>
    )

    private data class RoundOutcome(
        val gamers: List<Gamer>,
        val traceTotalAmount: Int?,
        val traceTermsByGamerId: Map<Long, Int>?,
        val aggregatedByName: Map<String, Int>
    ) {
        fun accountOf(name: String): Int = gamers.first { it.name == name }.account
    }

    /**
     * 코루틴 청크 단위 격리 실행기. 각 청크가 독립 Mock 인스턴스를 보유해 데이터 레이스 없이 병렬 실행한다.
     */
    private class IsolatedHarness {
        private val ruleRepository = MockRuleRepository()
        private val gamerRepository = MockGamerRepository()
        private val calculateLoserScore = CalculateLoserScoreUseCase(ruleRepository, gamerRepository)
        private val calculateGameResult = CalculateGameResultUseCase(
            ruleRepository = ruleRepository,
            gamerRepository = gamerRepository,
            updateAccountUseCase = UpdateAccountUseCase(gamerRepository),
            calculateSellScoreUseCase = CalculateSellScoreUseCase(ruleRepository, gamerRepository),
            calculateLoserScoreUseCase = calculateLoserScore,
            calculateScoreOptionUseCase = CalculateScoreOptionUseCase(ruleRepository, gamerRepository)
        )
        private val buildRoundTrace = BuildRoundTraceUseCase(
            ruleRepository = ruleRepository,
            gamerRepository = gamerRepository,
            calculateLoserScoreUseCase = calculateLoserScore
        )
        private val aggregateGamerResults = AggregateGamerResultsUseCase()

        suspend fun evaluate(case: RoundCase): RoundOutcome {
            val gameId = 1L
            val roundId = 1L
            gamerRepository.clear()
            ruleRepository.setRules(
                gameId,
                listOf(
                    Rule(name = Const.Rule.Score, score = case.rules.scorePerPoint),
                    Rule(name = Const.Rule.Fuck, score = case.rules.fuckScore),
                    Rule(name = Const.Rule.Sell, score = case.rules.sellScore)
                )
            )

            val initialGamers = case.players.mapIndexed { index, spec ->
                val id = (index + 1).toLong()
                Gamer(
                    id = id,
                    playerId = id,
                    gameId = gameId,
                    roundId = roundId,
                    name = spec.name,
                    score = when {
                        spec.isSeller -> spec.sellCount
                        spec.isWinner -> WINNER_BASE_SCORE
                        else -> 0
                    },
                    go = if (spec.isWinner) spec.goCount else 0,
                    winnerOption = if (spec.isWinner) WinnerOption.Winner else null,
                    sellerOption = if (spec.isSeller) SellerOption.Seller else null,
                    scoreOption = spec.scoreOptions,
                    loserOption = spec.loserOptions
                )
            }
            gamerRepository.setRoundGamers(roundId, initialGamers)

            val seller = initialGamers.firstOrNull { it.sellerOption != null }
            val winner = initialGamers.firstOrNull { it.winnerOption != null }
            calculateGameResult(gameId = gameId, roundId = roundId, seller = seller, winner = winner)

            val updatedGamers = gamerRepository.getRoundGamers(roundId)
            val trace = buildRoundTrace(gameId = gameId, roundId = roundId)
            val aggregated = aggregateGamerResults(updatedGamers)

            return RoundOutcome(
                gamers = updatedGamers,
                traceTotalAmount = trace?.totalAmount,
                traceTermsByGamerId = trace?.terms?.associate { it.gamerId to it.amount },
                aggregatedByName = aggregated.associate { it.name to it.account }
            )
        }
    }

    private suspend fun runParallelBatches(
        cases: List<RoundCase>,
        chunkSize: Int = CHUNK_SIZE,
        verify: suspend (IsolatedHarness, RoundCase) -> Unit
    ) {
        withContext(Dispatchers.Default) {
            coroutineScope {
                cases.chunked(chunkSize).map { chunk ->
                    async {
                        val harness = IsolatedHarness()
                        for (case in chunk) {
                            verify(harness, case)
                        }
                    }
                }.awaitAll()
            }
        }
    }

    private fun assertBlackBoxInvariants(case: RoundCase, outcome: RoundOutcome) {
        val gamers = outcome.gamers

        // 1. Zero-Sum 보존
        assertEquals(0, gamers.sumOf { it.account }, "Zero-Sum broken: ${case.tag}")

        // 2. 원장 합계 & 거래 대칭성
        gamers.forEach { gamer ->
            assertEquals(
                gamer.account,
                gamer.calculate.sumOf { it.account },
                "Ledger mismatch for ${gamer.name}: ${case.tag}"
            )
        }
        val targetMap = gamers.associate { gamer ->
            gamer.playerId to gamer.calculate.associate { it.playerId to it.account }
        }
        for (a in gamers) {
            for (b in gamers) {
                if (a.playerId == b.playerId) continue
                val aToB = targetMap[a.playerId]?.get(b.playerId) ?: 0
                val bToA = targetMap[b.playerId]?.get(a.playerId) ?: 0
                assertEquals(
                    0,
                    aToB + bToA,
                    "Reciprocity broken (${a.name}<->${b.name}): ${case.tag}"
                )
            }
        }

        // 3. AggregateGamerResults 일치
        assertEquals(
            gamers.associate { it.name to it.account },
            outcome.aggregatedByName,
            "Aggregate mismatch: ${case.tag}"
        )

        // 4. BuildRoundTrace 일치 (정산 금액이 0이 아닌 참여자가 있으면 트레이스 필수)
        val hasNonZeroAccount = gamers.any { it.account != 0 }
        val traceTerms = outcome.traceTermsByGamerId
        if (hasNonZeroAccount) {
            assertNotNull(traceTerms, "Expected non-null RoundTrace: ${case.tag}")
        }
        if (traceTerms != null) {
            assertEquals(0, traceTerms.values.sum(), "Trace terms sum != 0: ${case.tag}")
            gamers.forEach { gamer ->
                assertEquals(
                    gamer.account,
                    traceTerms[gamer.id] ?: 0,
                    "Trace term mismatch for ${gamer.name}: ${case.tag}"
                )
            }
            val winnerGamer = gamers.firstOrNull { it.winnerOption != null }
            if (winnerGamer != null) {
                assertEquals(
                    winnerGamer.account,
                    outcome.traceTotalAmount,
                    "Trace totalAmount mismatch: ${case.tag}"
                )
            }
        }

        // 5. 역할 불변식
        val hasAnyScoreOption = case.players.any { !it.isSeller && it.scoreOptions.isNotEmpty() }
        if (!hasAnyScoreOption) {
            val sellerGamer = gamers.firstOrNull { it.sellerOption != null }
            val sellerSellCount = case.players.firstOrNull { it.isSeller }?.sellCount ?: 0
            if (sellerGamer != null) {
                assertEquals(
                    sellerSellCount * case.rules.sellScore * (gamers.size - 1),
                    sellerGamer.account,
                    "Seller income axiom broken: ${case.tag}"
                )
            }
            val goBakLoser = gamers.firstOrNull { it.loserOption.contains(LoserOption.GoBak) }
            if (goBakLoser != null) {
                val expectedNormalLoserAccount = if (sellerGamer != null) -(sellerSellCount * case.rules.sellScore) else 0
                gamers.filter {
                    it.winnerOption == null && it.sellerOption == null && it.id != goBakLoser.id
                }.forEach { normalLoser ->
                    assertEquals(
                        expectedNormalLoserAccount,
                        normalLoser.account,
                        "Non-GoBak loser should pay 0 base loss: ${case.tag}"
                    )
                }
            }
        }
    }

    @Test
    fun `exhaustive win-go-bak combinations at score 10 preserve zero-sum ledger trace and role axioms`() = runTest {
        val cases = buildList {
            val goCounts = 0..5
            val loserBak2 = validLoserSets(loserCount = 1)
            val loserBak3 = symmetryReducedLoserSets(loserCount = 2)
            val loserBak4NoSell = symmetryReducedLoserSets(loserCount = 3)

            // 2인 전체 (16 * 6 = 96)
            for (go in goCounts) {
                for (baks in loserBak2) {
                    add(
                        RoundCase(
                            tag = "2P go=$go bak=$baks",
                            players = listOf(
                                PlayerSpec(name = "P1", isWinner = true, goCount = go),
                                PlayerSpec(name = "P2", loserOptions = baks[0])
                            )
                        )
                    )
                }
            }

            // 3인 대칭 제거 전체 (100 * 6 = 600)
            for (go in goCounts) {
                for (baks in loserBak3) {
                    add(
                        RoundCase(
                            tag = "3P go=$go bak=$baks",
                            players = listOf(
                                PlayerSpec(name = "P1", isWinner = true, goCount = go),
                                PlayerSpec(name = "P2", loserOptions = baks[0]),
                                PlayerSpec(name = "P3", loserOptions = baks[1])
                            )
                        )
                    )
                }
            }

            // 4인 광팜 없음 대칭 제거 전체 (408 * 6 = 2,448)
            for (go in goCounts) {
                for (baks in loserBak4NoSell) {
                    add(
                        RoundCase(
                            tag = "4P-NoSell go=$go bak=$baks",
                            players = listOf(
                                PlayerSpec(name = "P1", isWinner = true, goCount = go),
                                PlayerSpec(name = "P2", loserOptions = baks[0]),
                                PlayerSpec(name = "P3", loserOptions = baks[1]),
                                PlayerSpec(name = "P4", loserOptions = baks[2])
                            )
                        )
                    )
                }
            }

            // 4인 광팜 (대표 장수 1, 2, 12 x 100 x 6 = 1,800)
            for (sellCount in listOf(1, 2, 12)) {
                for (go in goCounts) {
                    for (baks in loserBak3) {
                        add(
                            RoundCase(
                                tag = "4P-Sell($sellCount) go=$go bak=$baks",
                                players = listOf(
                                    PlayerSpec(name = "P1", isSeller = true, sellCount = sellCount),
                                    PlayerSpec(name = "P2", isWinner = true, goCount = go),
                                    PlayerSpec(name = "P3", loserOptions = baks[0]),
                                    PlayerSpec(name = "P4", loserOptions = baks[1])
                                )
                            )
                        )
                    }
                }
            }
        }

        assertEquals(4_944, cases.size)
        runParallelBatches(cases) { harness, case ->
            val outcome = harness.evaluate(case)
            assertBlackBoxInvariants(case, outcome)
        }
    }

    @Test
    fun `exhaustive ScoreOption combinations with and without winner satisfy ledger and trace parity`() = runTest {
        val singlePlayerScoreOptions = validScoreOptionSets() // 8종
        val cases = buildList {
            // 2~4인 (광팜 없음 / 광팜 있음) × (승자 있음 / 승자 없음)
            val layouts = listOf(
                Triple("2P", 2, 0),
                Triple("3P", 3, 0),
                Triple("4P-NoSell", 4, 0),
                Triple("4P-Sell2", 3, 2)
            )
            for ((label, activeCount, sellCount) in layouts) {
                // (a) 승자(1명, 10점) + 비승자(activeCount-1명 대칭)
                val nonWinnerCombos = combinationsWithRepetition(singlePlayerScoreOptions, activeCount - 1)
                for (winnerOpt in singlePlayerScoreOptions) {
                    for (otherOpts in nonWinnerCombos) {
                        val players = buildList {
                            if (sellCount > 0) add(PlayerSpec(name = "Seller", isSeller = true, sellCount = sellCount))
                            add(PlayerSpec(name = "P1", isWinner = true, scoreOptions = winnerOpt))
                            otherOpts.forEachIndexed { idx, opt ->
                                add(PlayerSpec(name = "P${idx + 2}", scoreOptions = opt))
                            }
                        }
                        add(RoundCase(tag = "$label-Win winOpt=$winnerOpt others=$otherOpts", players = players))
                    }
                }

                // (b) 승자 없음(삼연뻑 즉시 종료 또는 옵션 정산만 발생)
                val allActiveCombos = combinationsWithRepetition(singlePlayerScoreOptions, activeCount)
                for (opts in allActiveCombos) {
                    val players = buildList {
                        if (sellCount > 0) add(PlayerSpec(name = "Seller", isSeller = true, sellCount = sellCount))
                        opts.forEachIndexed { idx, opt ->
                            add(PlayerSpec(name = "P${idx + 1}", scoreOptions = opt))
                        }
                    }
                    add(RoundCase(tag = "$label-NoWin opts=$opts", players = players))
                }
            }
        }

        assertEquals(2_206, cases.size)
        runParallelBatches(cases) { harness, case ->
            val outcome = harness.evaluate(case)
            assertBlackBoxInvariants(case, outcome)
        }
    }

    @Test
    fun `metamorphic laws rule scaling bak doubling go monotonicity and permutation hold cleanly`() = runTest {
        val sampleBaks = listOf(
            emptyList(),
            listOf(LoserOption.PeaBak),
            listOf(LoserOption.PeaBak, LoserOption.LightBak),
            listOf(LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak)
        )
        val cases = buildList {
            for (sellCount in 0..12) {
                for (go in listOf(0, 1, 2, 3, 5)) {
                    for (loserBak in sampleBaks) {
                        val players = buildList {
                            if (sellCount > 0) add(PlayerSpec(name = "Seller", isSeller = true, sellCount = sellCount))
                            add(PlayerSpec(name = "Winner", isWinner = true, goCount = go, scoreOptions = listOf(ScoreOption.FirstFuck)))
                            add(PlayerSpec(name = "Loser1", loserOptions = loserBak, scoreOptions = listOf(ScoreOption.FirstDdadak)))
                            if (sellCount == 0) add(PlayerSpec(name = "Loser2"))
                        }
                        add(RoundCase(tag = "Meta sell=$sellCount go=$go bak=$loserBak", players = players))
                    }
                }
            }
        }

        assertEquals(260, cases.size)
        runParallelBatches(cases, chunkSize = 32) { harness, case ->
            val baseOutcome = harness.evaluate(case)
            assertBlackBoxInvariants(case, baseOutcome)

            // Law 1: 룰 단가 2배 Scaling -> 모든 플레이어 정산액 2배
            val doubledOutcome = harness.evaluate(
                case.copy(
                    tag = "${case.tag} [2x]",
                    rules = RuleSet(
                        scorePerPoint = case.rules.scorePerPoint * 2,
                        fuckScore = case.rules.fuckScore * 2,
                        sellScore = case.rules.sellScore * 2
                    )
                )
            )
            baseOutcome.gamers.forEach { baseGamer ->
                assertEquals(
                    baseGamer.account * 2,
                    doubledOutcome.accountOf(baseGamer.name),
                    "2x scaling failed for ${baseGamer.name}: ${case.tag}"
                )
            }

            // Law 2: 룰 단가 0원 -> 모두 0원
            val zeroOutcome = harness.evaluate(
                case.copy(tag = "${case.tag} [0x]", rules = RuleSet(0, 0, 0))
            )
            assertTrue(
                zeroOutcome.gamers.all { it.account == 0 },
                "Zero rule should produce 0 accounts: ${case.tag}"
            )

            // Law 3: 입력 플레이어 순서 순열 불변성
            val reversedOutcome = harness.evaluate(
                case.copy(tag = "${case.tag} [Reversed]", players = case.players.reversed())
            )
            baseOutcome.gamers.forEach { baseGamer ->
                assertEquals(
                    baseGamer.account,
                    reversedOutcome.accountOf(baseGamer.name),
                    "Permutation invariance broken for ${baseGamer.name}: ${case.tag}"
                )
            }
        }

        // Law 4: 박 추가 시 승패 순부담 2배 & 고 증가 시 단조 증가 (대표 2~4인)
        for (go in 0..4) {
            val harness = IsolatedHarness()
            val prev = harness.evaluate(
                RoundCase(
                    tag = "GoMono go=$go",
                    players = listOf(
                        PlayerSpec("P1", isWinner = true, goCount = go),
                        PlayerSpec("P2"),
                        PlayerSpec("P3", loserOptions = listOf(LoserOption.PeaBak))
                    )
                )
            )
            val next = harness.evaluate(
                RoundCase(
                    tag = "GoMono go=${go + 1}",
                    players = listOf(
                        PlayerSpec("P1", isWinner = true, goCount = go + 1),
                        PlayerSpec("P2"),
                        PlayerSpec("P3", loserOptions = listOf(LoserOption.PeaBak))
                    )
                )
            )
            assertTrue(
                next.accountOf("P1") > prev.accountOf("P1"),
                "Winner income must strictly increase from ${go}go to ${go + 1}go"
            )
        }

        // 비고박 단일 패자에 박 추가 시 지불액 정확히 2배 증가 검증
        val bakChain = listOf(
            emptyList(),
            listOf(LoserOption.PeaBak),
            listOf(LoserOption.PeaBak, LoserOption.LightBak),
            listOf(LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak)
        )
        for (i in 0 until bakChain.lastIndex) {
            val harness = IsolatedHarness()
            val before = harness.evaluate(
                RoundCase(
                    tag = "BakDouble before=$i",
                    players = listOf(
                        PlayerSpec("P1", isWinner = true, goCount = 2),
                        PlayerSpec("P2", loserOptions = bakChain[i])
                    )
                )
            )
            val after = harness.evaluate(
                RoundCase(
                    tag = "BakDouble after=${i + 1}",
                    players = listOf(
                        PlayerSpec("P1", isWinner = true, goCount = 2),
                        PlayerSpec("P2", loserOptions = bakChain[i + 1])
                    )
                )
            )
            assertEquals(
                before.accountOf("P2") * 2,
                after.accountOf("P2"),
                "Adding one bak must double loser payment"
            )
        }
    }

    @Test
    fun `cross-axis selling go multi-bak GoBak and ScoreOption combinations preserve invariants`() = runTest {
        val sellCounts = listOf(0, 1, 12)
        val goCounts = listOf(0, 2, 4)
        val loserOptionSamples = listOf(
            listOf(emptyList(), emptyList()),
            listOf(listOf(LoserOption.PeaBak, LoserOption.LightBak), listOf(LoserOption.MongBak)),
            listOf(listOf(LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak), listOf(LoserOption.GoBak)),
            listOf(listOf(LoserOption.GoBak, LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak), listOf(LoserOption.PeaBak))
        )
        val winnerScoreOpts = listOf(
            emptyList(),
            listOf(ScoreOption.FirstFuck, ScoreOption.FirstDdadak),
            listOf(ScoreOption.ThreeFuck)
        )
        val loserScoreOpts = listOf(
            emptyList(),
            listOf(ScoreOption.SecondFuck),
            listOf(ScoreOption.ThreeFuck, ScoreOption.FirstDdadak)
        )

        val cases = buildList {
            for (sellCount in sellCounts) {
                for (go in goCounts) {
                    for (bakPair in loserOptionSamples) {
                        for (winOpt in winnerScoreOpts) {
                            for (loserOpt in loserScoreOpts) {
                                val players = buildList {
                                    if (sellCount > 0) add(PlayerSpec("Seller", isSeller = true, sellCount = sellCount))
                                    add(PlayerSpec("Winner", isWinner = true, goCount = go, scoreOptions = winOpt))
                                    add(PlayerSpec("Loser1", loserOptions = bakPair[0], scoreOptions = loserOpt))
                                    add(PlayerSpec("Loser2", loserOptions = bakPair[1]))
                                    if (sellCount == 0) add(PlayerSpec("Loser3"))
                                }
                                add(
                                    RoundCase(
                                        tag = "Cross sell=$sellCount go=$go baks=$bakPair winOpt=$winOpt loserOpt=$loserOpt",
                                        players = players
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        assertEquals(324, cases.size)
        runParallelBatches(cases, chunkSize = 64) { harness, case ->
            val outcome = harness.evaluate(case)
            assertBlackBoxInvariants(case, outcome)
        }
    }

    private fun validScoreOptionSets(): List<List<ScoreOption>> {
        val fuckVariants = listOf(
            null,
            ScoreOption.FirstFuck,
            ScoreOption.SecondFuck,
            ScoreOption.ThreeFuck
        )
        return buildList {
            for (fuck in fuckVariants) {
                add(listOfNotNull(fuck))
                add(listOfNotNull(fuck, ScoreOption.FirstDdadak))
            }
        }
    }

    private fun nonGoBakSubsets(): List<List<LoserOption>> = buildList {
        val flags = listOf(LoserOption.PeaBak, LoserOption.LightBak, LoserOption.MongBak)
        for (mask in 0 until (1 shl flags.size)) {
            add(flags.filterIndexed { index, _ -> (mask and (1 shl index)) != 0 })
        }
    }

    private fun validLoserSets(loserCount: Int): List<List<List<LoserOption>>> {
        val nonGoBak = nonGoBakSubsets()
        val withGoBak = nonGoBak.map { it + LoserOption.GoBak }
        val result = mutableListOf<List<List<LoserOption>>>()

        // GoBak 0명
        fun buildNoGoBak(remaining: Int, current: List<List<LoserOption>>) {
            if (remaining == 0) {
                result.add(current)
                return
            }
            for (set in nonGoBak) {
                buildNoGoBak(remaining - 1, current + listOf(set))
            }
        }
        buildNoGoBak(loserCount, emptyList())

        // GoBak 정확히 1명
        for (goBakIdx in 0 until loserCount) {
            fun buildWithGoBak(idx: Int, current: List<List<LoserOption>>) {
                if (idx == loserCount) {
                    result.add(current)
                    return
                }
                val candidates = if (idx == goBakIdx) withGoBak else nonGoBak
                for (set in candidates) {
                    buildWithGoBak(idx + 1, current + listOf(set))
                }
            }
            buildWithGoBak(0, emptyList())
        }
        return result
    }

    private fun symmetryReducedLoserSets(loserCount: Int): List<List<List<LoserOption>>> {
        val nonGoBak = nonGoBakSubsets()
        val withGoBak = nonGoBak.map { it + LoserOption.GoBak }
        val result = mutableListOf<List<List<LoserOption>>>()

        // 1. GoBak 없음: 비고박 8종의 중복조합
        result.addAll(combinationsWithRepetition(nonGoBak, loserCount))

        // 2. GoBak 1명: 1번째 슬롯(8종) × 나머지 (loserCount-1)명 비고박 중복조합
        val restCombos = combinationsWithRepetition(nonGoBak, loserCount - 1)
        for (goBakSet in withGoBak) {
            for (rest in restCombos) {
                result.add(listOf(goBakSet) + rest)
            }
        }
        return result
    }

    private fun <T> combinationsWithRepetition(items: List<T>, count: Int): List<List<T>> {
        if (count == 0) return listOf(emptyList())
        val result = mutableListOf<List<T>>()
        fun recurse(startIdx: Int, remaining: Int, current: List<T>) {
            if (remaining == 0) {
                result.add(current)
                return
            }
            for (i in startIdx until items.size) {
                recurse(i, remaining - 1, current + listOf(items[i]))
            }
        }
        recurse(0, count, emptyList())
        return result
    }

    companion object {
        private const val WINNER_BASE_SCORE = 10
        private const val CHUNK_SIZE = 128
    }
}
