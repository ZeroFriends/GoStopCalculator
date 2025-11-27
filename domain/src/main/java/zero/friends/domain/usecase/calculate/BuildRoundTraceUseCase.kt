package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.CalculationFactors
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.util.Const
import javax.inject.Inject

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
        fun total(): Int = base + fuck + ddadak + sell
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

        val contributions = gamers.associateTo(mutableMapOf()) { it.id to Contribution() }

        if (declaredWinner != null) {
            val baseResult = calculateLoserScoreUseCase(
                gameId = gameId,
                roundId = roundId,
                winner = declaredWinner,
                seller = seller
            )
            baseResult.accounts.forEach { (id, amount) ->
                contributions[id]?.base = amount
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

        val baseScoreRaw = winner?.score ?: 0
        val baseGo = winner?.go ?: 0

        val allTerms = gamers.mapNotNull { gamer ->
            val roleLabel = buildRoleLabel(gamer, winner, seller)
            val receiverCount = resolveReceiverCount(gamer, winner, seller, gamers.size)
            val contribution = contributions[gamer.id] ?: Contribution()
            if (contribution.total() == 0) return@mapNotNull null
            val isWinner = winner?.id == gamer.id

            val bakCount = if (isWinner) 0 else gamer.loserOption.count()

            val factors = CalculationFactors(
                isSeller = gamer.id == seller?.id,
                baseScore = baseScoreRaw,
                goCount = baseGo,
                scorePerPoint = scorePerPoint,
                bakCount = bakCount,
                fuckAmount = contribution.fuck,
                ddadakAmount = contribution.ddadak,
                sellAmount = contribution.sell,
                receiverCount = receiverCount
            )

            RoundTraceTerm(
                gamerId = gamer.id,
                label = "${gamer.name}$roleLabel",
                amount = contribution.total(),
                factors = factors
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
                val amountPerOpponent = calculateScoreOptionAmount(option, fuckScore, scorePerPoint)
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

    private fun buildRoleLabel(
        gamer: Gamer,
        winner: Gamer?,
        seller: Gamer?
    ): String {
        val tags = mutableListOf<String>()
        if (winner != null && gamer.id == winner.id) {
            if (gamer.scoreOption.contains(ScoreOption.ThreeFuck)) {
                tags += "삼연뻑"
            }
            tags += "승자"
        } else if (gamer.scoreOption.contains(ScoreOption.ThreeFuck)) {
            tags += "삼연뻑"
        }

        if (seller != null && gamer.id == seller.id) {
            tags += "광팔이"
        }

        if (tags.isEmpty()) {
            tags += "패자"
            if (gamer.loserOption.isNotEmpty()) {
                tags += gamer.loserOption.joinToString(separator = ", ") { it.korean }
            }
        }

        return if (tags.isEmpty()) "" else "(${tags.joinToString(separator = ", ")})"
    }

    private fun calculateScoreOptionAmount(
        option: ScoreOption,
        fuckScore: Int,
        scorePerPoint: Int
    ): Int {
        return when (option) {
            ScoreOption.FirstFuck -> fuckScore
            ScoreOption.SecondFuck -> fuckScore * 2
            ScoreOption.ThreeFuck -> fuckScore * 4
            ScoreOption.FirstDdadak -> scorePerPoint * 3
        }
    }
}
