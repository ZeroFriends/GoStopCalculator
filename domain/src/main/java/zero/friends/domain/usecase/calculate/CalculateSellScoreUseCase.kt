package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import javax.inject.Inject

/**
 * 광팜(광 팔기) 계산 UseCase
 * 
 * 룰:
 * - 광을 판 사람(seller)이 각 플레이어에게 (광팔기 점수 × 판 광 수)만큼 받음
 * - 광을 판 사람은 게임에 참여하지 않음
 */
class CalculateSellScoreUseCase @Inject constructor() {
    
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
     * @param seller 광을 판 사람
     * @param allGamers 라운드의 모든 플레이어
     * @param sellScorePerLight 광 하나당 가격
     * @return 각 플레이어의 계산 결과
     */
    operator fun invoke(
        seller: Gamer,
        allGamers: List<Gamer>,
        sellScorePerLight: Int
    ): SellScoreResult {
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

