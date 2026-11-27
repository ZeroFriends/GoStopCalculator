package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.util.Const
import zero.friends.domain.util.Inject

/**
 * 패자 점수 계산 UseCase (나무위키 표준 룰)
 * 
 * 룰:
 * - 기본 금액 = 승자 최종 점수(고 규칙 반영) × 점당
 *   - 고 점수: 1고=+1, 2고=+2, 3고 이상=(기본점수+고)×2^(고-2)
 * - 박(Bak):
 *   - 2인전(맞고, 패자 1명): 피박·광박·멍박·고박 각각 2배씩 중첩
 *   - 3인 이상 고스톱(패자 2명 이상): 개별 패자 금액은 피박·광박·멍박만 각각 2배씩 중첩하며,
 *     고박(GoBak)을 한 플레이어가 본인의 판 점수와 다른 모든 패자의 판 점수를 합산하여 전액 독박 지불한다.
 */
class CalculateLoserScoreUseCase @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val gamerRepository: GamerRepository
) {
    
    /**
     * 패자 계산 결과
     * @param accounts 각 플레이어가 주고받을 금액 맵 (playerId to account)
     * @param individualLosses 고박 대납 합산 전 각 패자의 원래 산출 판 점수 지불액 (양수 금액, playerId to amount)
     * @param goBakPayerId 독박 대납을 수행한 고박 플레이어 ID (없으면 null)
     */
    data class LoserScoreResult(
        val accounts: Map<Long, Int>,
        val individualLosses: Map<Long, Int> = emptyMap(),
        val goBakPayerId: Long? = null,
    )
    
    /**
     * 패자 점수 계산
     * 
     * @param gameId 게임 ID (룰 조회용)
     * @param roundId 라운드 ID (플레이어 조회용)
     * @param winner 승자
     * @param seller 광팜 플레이어 (없을 경우 null, 패자 계산에서 제외)
     * @param legacyDoubleGoBak v1.4.x 구규칙 호환 조회용 플래그 (3인 이상에서도 고박 2배 포함)
     * @return 각 플레이어의 계산 결과
     */
    suspend operator fun invoke(
        gameId: Long,
        roundId: Long,
        winner: Gamer,
        seller: Gamer? = null,
        legacyDoubleGoBak: Boolean = false,
    ): LoserScoreResult {
        // 룰에서 점당 가져오기
        val rules = ruleRepository.getRules(gameId)
        val scorePerPoint = rules.firstOrNull { it.name == Const.Rule.Score }?.score ?: 0
        
        val allGamers = gamerRepository.getRoundGamers(roundId)
        // 광팜 플레이어를 제외한 패자 목록
        val losers = allGamers.filter { gamer ->
            gamer.id != winner.id && (seller == null || gamer.id != seller.id)
        }
        val accounts = mutableMapOf<Long, Int>()
        
        // 승자 점수 (고 + 흔들기·폭탄 규칙 반영)
        val winnerScore = calculateWinnerScore(
            baseScore = winner.score,
            goCount = winner.go,
            shakeCount = winner.shakeCount,
            bombCount = winner.bombCount,
        )
        val loserCount = losers.size
        // 동일 라운드의 광팔기·뻑·첫따닥 가감 시 Int 오버플로우로 Zero-Sum이 깨지지 않도록
        // 공통 상한(calculateMaxPerLoser)을 적용한다.
        val maxPerLoser = calculateMaxPerLoser(loserCount)

        val loserAmounts = losers.associate { loser ->
            loser.id to calculateLoserAmount(
                loserOptions = loser.loserOption,
                loserCount = loserCount,
                winnerScore = winnerScore,
                scorePerPoint = scorePerPoint,
                maxAmount = maxPerLoser,
                legacyDoubleGoBak = legacyDoubleGoBak,
            )
        }

        // 고박 플레이어 찾기 (있다면 고박자가 모든 패자 금액을 대신 지불)
        val goBakGamer = losers.firstOrNull { it.loserOption.contains(LoserOption.GoBak) }

        if (goBakGamer != null) {
            val totalPayment = loserAmounts.values.sum()
            accounts[goBakGamer.id] = -totalPayment
            accounts[winner.id] = totalPayment
        } else {
            var totalWinnerIncome = 0
            losers.forEach { loser ->
                val loserAmount = loserAmounts[loser.id] ?: 0
                accounts[loser.id] = -loserAmount
                totalWinnerIncome += loserAmount
            }
            accounts[winner.id] = totalWinnerIncome
        }

        return LoserScoreResult(
            accounts = accounts,
            individualLosses = loserAmounts,
            goBakPayerId = goBakGamer?.id,
        )
    }
    
    /**
     * 개별 패자의 원래 판 점수 지불 금액 계산
     * 
     * 기본 금액 = 승자 점수 × 점당
     * 박이 있으면: 기본 금액 × (2 ^ 유효 박 개수)
     */
    private fun calculateLoserAmount(
        loserOptions: List<LoserOption>,
        loserCount: Int,
        winnerScore: Int,
        scorePerPoint: Int,
        maxAmount: Long = Int.MAX_VALUE.toLong(),
        legacyDoubleGoBak: Boolean = false,
    ): Int {
        if (winnerScore <= 0 || scorePerPoint <= 0 || maxAmount <= 0L) return 0
        val baseAmount = winnerScore.toLong() * scorePerPoint.toLong()
        val bakCount = if (legacyDoubleGoBak) {
            loserOptions.distinct().size
        } else {
            effectiveBakCount(loserOptions, loserCount)
        }
        val shift = bakCount.coerceAtMost(30)
        val total = if (baseAmount > maxAmount shr shift) {
            maxAmount
        } else {
            baseAmount shl shift
        }
        return total.coerceIn(0L, maxAmount).toInt()
    }
}
