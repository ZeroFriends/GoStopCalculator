package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.util.Const
import javax.inject.Inject

/**
 * 광팜(광 팔기) 계산 UseCase
 * 
 * 룰:
 * - 광을 판 사람(seller)이 각 플레이어에게 (광팔기 점수 × 판 광 수)만큼 받음
 * - 광을 판 사람은 게임에 참여하지 않음
 */
class CalculateSellScoreUseCase @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val gamerRepository: GamerRepository
) {
    
    /**
     * 광팜 계산 결과
     * @param accounts 각 플레이어가 주고받을 금액 맵 (playerId to account)
     */
    data class SellScoreResult(
        val accounts: Map<Long, Int>
    )
    
    /**
     * 광팜 계산
     * 
     * @param gameId 게임 ID (룰 조회용)
     * @param roundId 라운드 ID (플레이어 조회용)
     * @param seller 광을 판 사람
     * @return 각 플레이어의 계산 결과
     */
    suspend operator fun invoke(
        gameId: Long,
        roundId: Long,
        seller: Gamer
    ): SellScoreResult {
        val allGamers = gamerRepository.getRoundGamers(roundId)
        // 룰에서 광팔기 점수 가져오기
        val rules = ruleRepository.getRules(gameId)
        val sellScorePerLight = rules.firstOrNull { it.name == Const.Rule.Sell }?.score ?: 0
        val accounts = mutableMapOf<Long, Int>()
        
        // 광 팔기 총액 계산 (광 하나당 가격 × 판 광 수)
        val sellTotalAmount = sellScorePerLight * seller.score
        
        // 판 사람을 제외한 나머지 플레이어들이 지불
        val buyers = allGamers - seller
        
        // 판 사람은 받음
        accounts[seller.id] = sellTotalAmount * buyers.size
        
        // 각 플레이어는 지불
        buyers.forEach { buyer ->
            accounts[buyer.id] = -sellTotalAmount
        }
        
        return SellScoreResult(accounts)
    }
}

