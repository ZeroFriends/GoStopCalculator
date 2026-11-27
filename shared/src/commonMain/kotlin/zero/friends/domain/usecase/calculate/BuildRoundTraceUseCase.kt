package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.CalculationFactors
import zero.friends.domain.model.CoveredLoserDetail
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.TraceTag
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.util.Const
import zero.friends.domain.util.Inject

/**
 * 저장된 스냅샷 없이 현재 데이터/규칙으로 라운드 트레이스를 생성한다.
 */
class BuildRoundTraceUseCase @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val gamerRepository: GamerRepository,
    private val calculateLoserScoreUseCase: CalculateLoserScoreUseCase
) {
    private data class Contribution(
        var base: Int = 0,
        var fuck: Int = 0,
        var ddadak: Int = 0,
        var sell: Int = 0
    ) {
        fun total(): Int = (base.toLong() + fuck.toLong() + ddadak.toLong() + sell.toLong())
            .coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong())
            .toInt()

        fun hasAnyContribution(): Boolean = base != 0 || fuck != 0 || ddadak != 0 || sell != 0
    }

    suspend operator fun invoke(gameId: Long, roundId: Long): RoundTrace? {
        val gamers = gamerRepository.getRoundGamers(roundId)
        if (gamers.isEmpty()) return null

        val rules = ruleRepository.getRules(gameId).associateBy { it.name }
        val scorePerPoint = rules[Const.Rule.Score]?.score ?: 0
        val fuckScore = rules[Const.Rule.Fuck]?.score ?: 0
        val sellScorePerLight = rules[Const.Rule.Sell]?.score ?: 0

        val declaredWinner = gamers.firstOrNull { it.winnerOption != null }
        val threeFuckWinner = gamers.firstOrNull { it.scoreOption.contains(ScoreOption.ThreeFuck) }
        val winner = declaredWinner ?: threeFuckWinner
        val seller = gamers.firstOrNull { it.sellerOption != null }

        val losers = if (declaredWinner != null) {
            gamers.filter { gamer ->
                gamer.id != declaredWinner.id && (seller == null || gamer.id != seller.id)
            }
        } else {
            emptyList()
        }
        val loserCount = losers.size
        val isSoloGoBakEligible = isGoBakSoloCoverageMode(loserCount)
        var goBakGamer = losers.firstOrNull { it.loserOption.contains(LoserOption.GoBak) }
        var isLegacyDoubleGoBak = false

        val contributions = gamers.associateTo(mutableMapOf()) { it.id to Contribution() }
        var individualLosses: Map<Long, Int> = emptyMap()

        if (declaredWinner != null) {
            val baseResult = calculateLoserScoreUseCase(
                gameId = gameId,
                roundId = roundId,
                winner = declaredWinner,
                seller = seller
            )
            individualLosses = baseResult.individualLosses
            baseResult.accounts.forEach { (id, amount) ->
                contributions[id]?.base = amount
            }
            goBakGamer = baseResult.goBakPayerId?.let { payerId ->
                losers.firstOrNull { it.id == payerId }
            }
        }

        applyScoreOption(
            contributions = contributions,
            gamers = gamers,
            seller = seller,
            fuckScore = fuckScore,
            scorePerPoint = scorePerPoint
        )
        applySelling(contributions, gamers, seller, sellScorePerLight)

        // v1.4.x 이하 구버전에서 저장된 3·4인 고박 라운드는 당시 규칙(고박 본인 2배 + 대납)으로 DB에 저장되어 있어,
        // 저장된 정산액(gamer.account)과 현재 v1.5.0 계산 결과가 어긋나고 구규칙 결과와 정확히 일치할 때 구규칙 트레이스로 폴백한다.
        val currentGoBakGamer = goBakGamer
        if (
            declaredWinner != null &&
            isSoloGoBakEligible &&
            currentGoBakGamer != null &&
            gamers.any { it.account != 0 } &&
            contributions[currentGoBakGamer.id]?.total() != currentGoBakGamer.account
        ) {
            val legacyResult = calculateLoserScoreUseCase(
                gameId = gameId,
                roundId = roundId,
                winner = declaredWinner,
                seller = seller,
                legacyDoubleGoBak = true,
            )
            val goBakContribution = contributions[currentGoBakGamer.id]
            val legacyGoBakTotal = if (goBakContribution != null) {
                ((legacyResult.accounts[currentGoBakGamer.id] ?: 0).toLong() +
                    goBakContribution.fuck +
                    goBakContribution.ddadak +
                    goBakContribution.sell)
                    .coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong())
                    .toInt()
            } else {
                null
            }
            if (legacyGoBakTotal == currentGoBakGamer.account) {
                isLegacyDoubleGoBak = true
                individualLosses = legacyResult.individualLosses
                contributions.values.forEach { it.base = 0 }
                legacyResult.accounts.forEach { (id, amount) ->
                    contributions[id]?.base = amount
                }
            }
        }

        val baseScoreRaw = declaredWinner?.score ?: 0
        val baseGo = declaredWinner?.go ?: 0
        val baseShake = declaredWinner?.shakeCount?.coerceAtLeast(0) ?: 0
        val baseBomb = declaredWinner?.bombCount?.coerceAtLeast(0) ?: 0

        val allTerms = gamers.mapNotNull { gamer ->
            val isWinner = winner?.id == gamer.id
            val isSeller = seller?.id == gamer.id
            val isLoser = declaredWinner != null && !isWinner && !isSeller
            val isGoBakSoloPayer = isLoser && isSoloGoBakEligible && goBakGamer?.id == gamer.id
            val exemptedOriginalLoss = if (isLoser && isSoloGoBakEligible && goBakGamer != null && goBakGamer.id != gamer.id) {
                individualLosses[gamer.id] ?: 0
            } else {
                0
            }
            val isGoBakExempted = exemptedOriginalLoss > 0

            val contribution = contributions[gamer.id] ?: Contribution()
            if (!contribution.hasAnyContribution() && !isGoBakExempted) return@mapNotNull null

            val traceTags = buildTraceTags(
                gamer = gamer,
                winner = winner,
                seller = seller,
                isGoBakExempted = isGoBakExempted,
            )
            val receiverCount = resolveReceiverCount(gamer, winner, seller, gamers.size)
            val bakCount = when {
                isWinner || isSeller -> 0
                isLegacyDoubleGoBak && gamer.id == goBakGamer?.id -> gamer.loserOption.distinct().size
                else -> effectiveBakCount(gamer.loserOption, loserCount)
            }

            val coveredLoserDetails = if (isGoBakSoloPayer) {
                losers
                    .filter { it.id != gamer.id }
                    .map { other ->
                        CoveredLoserDetail(
                            playerName = other.name,
                            amount = -(individualLosses[other.id] ?: 0),
                            bakCount = effectiveBakCount(other.loserOption, loserCount),
                        )
                    }
            } else {
                emptyList()
            }

            val selfBaseLoss = when {
                isGoBakSoloPayer -> -(individualLosses[gamer.id] ?: 0)
                isLoser && !isGoBakExempted -> contribution.base
                else -> 0
            }
            val goBakCoveredAmount = if (isGoBakSoloPayer) {
                coveredLoserDetails.sumOf { it.amount }
            } else {
                0
            }

            val factors = CalculationFactors(
                isSeller = isSeller,
                baseScore = baseScoreRaw,
                goCount = baseGo,
                scorePerPoint = scorePerPoint,
                bakCount = bakCount,
                fuckAmount = contribution.fuck,
                ddadakAmount = contribution.ddadak,
                sellAmount = contribution.sell,
                receiverCount = receiverCount,
                selfBaseLoss = selfBaseLoss,
                goBakCoveredAmount = goBakCoveredAmount,
                goBakExemptedAmount = -exemptedOriginalLoss,
                goBakPayerName = if (isGoBakExempted) goBakGamer?.name.orEmpty() else "",
                isSoloGoBakMode = isSoloGoBakEligible && goBakGamer != null,
                coveredLosers = coveredLoserDetails,
                shakeCount = if (isSeller) 0 else baseShake,
                bombCount = if (isSeller) 0 else baseBomb,
            )

            RoundTraceTerm(
                gamerId = gamer.id,
                label = gamer.name,
                amount = contribution.total(),
                factors = factors,
                playerName = gamer.name,
                traceTags = traceTags
            )
        }
        
        if (winner != null && allTerms.none { it.gamerId != winner.id }) return null
        if (allTerms.isEmpty()) return null
        
        val totalAmount = winner?.let { contributions[it.id]?.total() } ?: 0
        
        return RoundTrace(
            roundId = roundId,
            gameId = gameId,
            winnerId = winner?.id ?: -1,
            terms = allTerms,
            totalAmount = totalAmount
        )
    }

    private fun applyScoreOption(
        contributions: Map<Long, Contribution>,
        gamers: List<Gamer>,
        seller: Gamer?,
        fuckScore: Int,
        scorePerPoint: Int
    ) {
        val candidates = seller?.let { sellerGamer ->
            gamers.filter { it.id != sellerGamer.id }
        } ?: gamers

        candidates.forEach { owner ->
            val others = candidates.filter { it.id != owner.id }
            owner.scoreOption.forEach { option ->
                val amountPerOpponent = option.calculateScore(fuckScore, scorePerPoint)
                val total = amountPerOpponent * others.size
                val ownerContribution = contributions[owner.id] ?: return@forEach
                when (option) {
                    ScoreOption.FirstDdadak -> ownerContribution.ddadak += total
                    else -> ownerContribution.fuck += total
                }
                others.forEach { other ->
                    val otherContribution = contributions[other.id] ?: return@forEach
                    when (option) {
                        ScoreOption.FirstDdadak -> otherContribution.ddadak -= amountPerOpponent
                        else -> otherContribution.fuck -= amountPerOpponent
                    }
                }
            }
        }
    }

    private fun applySelling(
        contributions: Map<Long, Contribution>,
        gamers: List<Gamer>,
        seller: Gamer?,
        sellScorePerLight: Int
    ) {
        if (seller == null || seller.score == 0 || sellScorePerLight == 0) return
        val buyers = gamers.filter { it.id != seller.id }
        if (buyers.isEmpty()) return

        val amountPerBuyer = seller.score * sellScorePerLight
        val sellerContribution = contributions[seller.id] ?: return
        sellerContribution.sell += amountPerBuyer * buyers.size
        buyers.forEach { buyer ->
            contributions[buyer.id]?.let { it.sell -= amountPerBuyer }
        }
    }

    private fun resolveReceiverCount(
        gamer: Gamer,
        winner: Gamer?,
        seller: Gamer?,
        totalPlayers: Int
    ): Int {
        return when {
            winner != null && gamer.id == winner.id -> {
                val sellerOffset = if (seller != null && seller.id != winner.id) 1 else 0
                (totalPlayers - 1 - sellerOffset).coerceAtLeast(1)
            }
            seller != null && gamer.id == seller.id -> (totalPlayers - 1).coerceAtLeast(1)
            else -> 1
        }
    }

    private fun buildTraceTags(
        gamer: Gamer,
        winner: Gamer?,
        seller: Gamer?,
        isGoBakExempted: Boolean = false,
    ): List<TraceTag> {
        val tags = mutableListOf<TraceTag>()
        val isWinner = winner != null && gamer.id == winner.id
        val isSeller = seller != null && gamer.id == seller.id

        if (gamer.scoreOption.contains(ScoreOption.ThreeFuck)) {
            tags += TraceTag.THREE_FUCK
        }
        if (isWinner) {
            tags += TraceTag.WINNER
        }
        if (isSeller) {
            tags += TraceTag.SELLER
        }
        if (winner != null && !isWinner && !isSeller) {
            tags += TraceTag.LOSER
            gamer.loserOption.forEach { option ->
                tags += when (option) {
                    LoserOption.PeaBak -> TraceTag.PEA_BAK
                    LoserOption.LightBak -> TraceTag.LIGHT_BAK
                    LoserOption.MongBak -> TraceTag.MONG_BAK
                    LoserOption.GoBak -> TraceTag.GO_BAK
                }
            }
            if (isGoBakExempted) {
                tags += TraceTag.GO_BAK_EXEMPT
            }
        }
        return tags
    }
}
