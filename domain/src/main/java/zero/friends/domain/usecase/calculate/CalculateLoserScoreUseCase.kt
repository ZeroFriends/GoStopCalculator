package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import javax.inject.Inject
import kotlin.math.pow

/**
 * 패자 점수 계산 UseCase
 * 
 * 룰:
 * - 기본 금액 = 승자 점수 × 점당
 * - 박(Bak): 각 박 옵션마다 2배씩 증가
 *   - 피박, 광박, 멍박, 고박
 * - 고박(GoBak) 특수 룰:
 *   - 고박한 사람이 다른 모든 패자들의 금액까지 대신 지불
 *   - 고박한 사람 본인도 (자신의 박 옵션 - 고박)에 따라 추가 지불
 */
class CalculateLoserScoreUseCase @Inject constructor() {
    
    /**
     * 패자 계산 결과
     * @param accounts 각 플레이어가 주고받을 금액 맵 (playerId to account)
     */
    data class LoserScoreResult(
        val accounts: Map<Long, Int>
    )
    
    /**
     * 패자 점수 계산
     * 
     * @param winner 승자
     * @param losers 패자 목록 (광팜 플레이어 제외)
     * @param scorePerPoint 점당 점수
     * @return 각 플레이어의 계산 결과
     */
    operator fun invoke(
        winner: Gamer,
        losers: List<Gamer>,
        scorePerPoint: Int
    ): LoserScoreResult {
        val accounts = mutableMapOf<Long, Int>()
        
        // 승자 점수
        val winnerScore = winner.score
        
        // 고박 플레이어 찾기
        val goBakGamer = losers.firstOrNull { it.loserOption.contains(LoserOption.GoBak) }
        
        if (goBakGamer != null) {
            // 고박 케이스: 고박자가 모든 패자의 돈을 대신 냄
            val remainLosers = losers - goBakGamer
            
            var totalPayment = 0
            
            // 다른 패자들의 금액 계산 (고박자가 대신 냄)
            remainLosers.forEach { loser ->
                val loserAmount = calculateLoserAmount(
                    loserOptions = loser.loserOption - LoserOption.GoBak,
                    winnerScore = winnerScore,
                    scorePerPoint = scorePerPoint
                )
                totalPayment += loserAmount
            }
            
            // 고박자 본인의 금액 계산 (고박 제외한 나머지 박 옵션)
            val goBakOwnAmount = calculateLoserAmount(
                loserOptions = goBakGamer.loserOption - LoserOption.GoBak,
                winnerScore = winnerScore,
                scorePerPoint = scorePerPoint
            )
            totalPayment += goBakOwnAmount
            
            // 고박자가 모두 지불
            accounts[goBakGamer.id] = -totalPayment
            // 승자가 모두 받음
            accounts[winner.id] = totalPayment
            
        } else {
            // 일반 케이스: 각 패자가 자신의 금액을 냄
            var totalWinnerIncome = 0
            
            losers.forEach { loser ->
                val loserAmount = calculateLoserAmount(
                    loserOptions = loser.loserOption,
                    winnerScore = winnerScore,
                    scorePerPoint = scorePerPoint
                )
                accounts[loser.id] = -loserAmount
                totalWinnerIncome += loserAmount
            }
            
            // 승자가 모두 받음
            accounts[winner.id] = totalWinnerIncome
        }
        
        return LoserScoreResult(accounts)
    }
    
    /**
     * 개별 패자의 지불 금액 계산
     * 
     * 기본 금액 = 승자 점수 × 점당
     * 박이 있으면: 기본 금액 × (2 ^ 박의 개수)
     * 
     * @param loserOptions 패자의 박 옵션 (고박 제외)
     * @param winnerScore 승자 점수
     * @param scorePerPoint 점당
     * @return 패자가 지불할 금액
     */
    private fun calculateLoserAmount(
        loserOptions: List<LoserOption>,
        winnerScore: Int,
        scorePerPoint: Int
    ): Int {
        val baseAmount = winnerScore * scorePerPoint
        
        return if (loserOptions.isNotEmpty()) {
            // 박이 있으면 2의 (박 개수)제곱을 곱함
            val multiplier = (2.0).pow(loserOptions.size.toDouble()).toInt()
            baseAmount * multiplier
        } else {
            baseAmount
        }
    }
}

