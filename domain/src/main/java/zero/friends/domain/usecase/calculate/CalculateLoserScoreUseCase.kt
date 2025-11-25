package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.util.Const
import javax.inject.Inject
import kotlin.math.pow

/**
 * 패자 점수 계산 UseCase
 * 
 * 룰:
 * - 기본 금액 = (승자 점수 + 고 점수) × 점당
 *   - 고 점수: 1고=+1, 2고=+2, 3고부터는 2배씩 증가 (3고=+4, 4고=+8 ...)
 * - 박(Bak): 각 박 옵션마다 2배씩 증가
 *   - 피박, 광박, 멍박, 고박
 * - 고박(GoBak) 특수 룰:
 *   - 고박한 사람이 다른 모든 패자들의 금액까지 대신 지불
 *     - 맞고(2명): 다른 사람의 금액에 고박의 2배 적용
 *     - 고스톱(3명 이상): 다른 사람의 금액을 그대로 대신 지불
 *   - 고박한 사람 본인의 금액 계산
 *     - 맞고(2명): 고박 포함하여 계산 (고박 2배 적용)
 *     - 고스톱(3명 이상): 고박 제외하고 계산 (고박은 다른 사람 대신 내는 것이므로 본인 금액에는 적용 안됨)
 */
class CalculateLoserScoreUseCase @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val gamerRepository: GamerRepository
) {
    
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
     * @param gameId 게임 ID (룰 조회용)
     * @param roundId 라운드 ID (플레이어 조회용)
     * @param winner 승자
     * @param seller 광팜 플레이어 (없을 경우 null, 패자 계산에서 제외)
     * @return 각 플레이어의 계산 결과
     */
    suspend operator fun invoke(
        gameId: Long,
        roundId: Long,
        winner: Gamer,
        seller: Gamer? = null
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
        
        // 승자 점수 (고 규칙 반영)
        val winnerScore = calculateGoScore(winner.score, winner.go)
        
        // 고박 플레이어 찾기
        val goBakGamer = losers.firstOrNull { it.loserOption.contains(LoserOption.GoBak) }
        
        if (goBakGamer != null) {
            // 고박 케이스: 고박자가 모든 패자의 돈을 대신 냄
            val remainLosers = losers - goBakGamer
            
            // 전체 게임 인원 수 확인 (winner + losers)
            // 맞고(2명): losers.size = 1
            // 고스톱(3명): losers.size = 2
            // 고스톱(4명): losers.size = 3
            val isMatgo = losers.size == 1  // 맞고인 경우
            
            if (!isMatgo) {
                // 고스톱: 고박자가 다른 패자 금액을 2배로 대신 지불, 본인 옵션만 추가
                val basePerLoser = winnerScore * scorePerPoint
                val baseTotal = basePerLoser * losers.size

                val goBakExtra = calculateLoserAmount(
                    loserOptions = goBakGamer.loserOption - LoserOption.GoBak,
                    winnerScore = winnerScore,
                    scorePerPoint = scorePerPoint
                ) - basePerLoser

                val remainExtras = remainLosers.sumOf { loser ->
                    calculateLoserAmount(
                        loserOptions = loser.loserOption - LoserOption.GoBak,
                        winnerScore = winnerScore,
                        scorePerPoint = scorePerPoint
                    ) - basePerLoser
                }

                val totalPayment = baseTotal + goBakExtra + (remainExtras * 2)
                accounts[goBakGamer.id] = -totalPayment
                accounts[winner.id] = totalPayment
            } else {
                var totalPayment = 0
                remainLosers.forEach { loser ->
                    val loserAmount = calculateLoserAmount(
                        loserOptions = loser.loserOption - LoserOption.GoBak,
                        winnerScore = winnerScore,
                        scorePerPoint = scorePerPoint
                    )
                    totalPayment += loserAmount * 2
                }

                val goBakOwnAmount = calculateLoserAmount(
                    loserOptions = goBakGamer.loserOption,
                    winnerScore = winnerScore,
                    scorePerPoint = scorePerPoint
                )
                totalPayment += goBakOwnAmount

                accounts[goBakGamer.id] = -totalPayment
                accounts[winner.id] = totalPayment
            }
            
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
     * @param loserOptions 패자의 박 옵션
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
