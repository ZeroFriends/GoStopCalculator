package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.util.Const
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.abs

class RoundTraceBuilder @Inject constructor(
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

    suspend fun build(
        gameId: Long,
        roundId: Long
    ): RoundTrace? {
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

        val contributions = gamers.associate { it.id to Contribution() }

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

        val terms = gamers.mapNotNull { gamer ->
            val roleLabel = buildRoleLabel(gamer, winner, seller)
            val receiverCount = resolveReceiverCount(gamer, winner, seller, gamers.size)
            val contribution = contributions[gamer.id] ?: Contribution()
            if (contribution.total() == 0) return@mapNotNull null
            val isWinner = winner?.id == gamer.id
            val detail = buildFormula(
                baseScoreRaw = baseScoreRaw,
                goCount = baseGo,
                scorePerPoint = scorePerPoint,
                contribution = contribution,
                receiverCount = receiverCount,
                gamer = gamer,
                isWinner = isWinner
            )
            RoundTraceTerm(
                label = "${gamer.name}$roleLabel",
                detail = detail,
                amount = contribution.total(),
                wrapWithParenthesis = false
            )
        }

        if (terms.isEmpty()) return null

        val totalAmount = winner?.let { contributions[it.id]?.total() } ?: terms.sumOf { it.amount }
        return RoundTrace(
            roundId = roundId,
            gameId = gameId,
            winnerId = winner?.id ?: -1,
            terms = terms,
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

    private fun buildFormula(
        baseScoreRaw: Int,
        goCount: Int,
        scorePerPoint: Int,
        contribution: Contribution,
        receiverCount: Int,
        gamer: Gamer,
        isWinner: Boolean
    ): String {
        val count = receiverCount.coerceAtLeast(1)
        val divider = count.toDouble()

        val basePer = contribution.base / divider
        val fuckPer = contribution.fuck / divider
        val ddadakPer = contribution.ddadak / divider
        val sellPer = contribution.sell / divider

        val parts = mutableListOf<String>()

        if (basePer != 0.0 && (baseScoreRaw > 0 || goCount > 0 || scorePerPoint > 0)) {
            val goMultiplierLabel = if (goCount >= 3) "x2^${goCount - 2}(고)" else ""

            val bakCount = if (isWinner) 0 else gamer.loserOption.count { it != LoserOption.GoBak }
            val bakMultiplierLabel = if (bakCount > 0) "x2^${bakCount}(박)" else ""

            parts += "(${baseScoreRaw}+${goCount})x${formatNumber(scorePerPoint)}$goMultiplierLabel$bakMultiplierLabel"
        }

        formatSignedPart(fuckPer, "뻑")?.let { parts += it }
        formatSignedPart(ddadakPer, "따닥")?.let { parts += it }
        formatSignedPart(sellPer, "광팔기")?.let { parts += it }

        val inner = parts.joinToString(" ").ifEmpty { "0" }
        return "( $inner ) x ${count}명"
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

    private fun formatSignedPart(value: Double, label: String): String? {
        if (value == 0.0) return null
        val signed = formatSigned(value)
        return "$signed $label"
    }

    private fun formatNumber(value: Number): String {
        val formatter = DecimalFormat("#,##0.##")
        return formatter.format(value)
    }

    private fun formatSigned(value: Double): String {
        val sign = if (value >= 0) "+" else "-"
        return sign + formatNumber(abs(value))
    }
}
