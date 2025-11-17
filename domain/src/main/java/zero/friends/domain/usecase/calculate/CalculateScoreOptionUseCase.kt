package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.ScoreOption
import javax.inject.Inject

/**
 * 점수 옵션 계산 UseCase (뻑, 따닥)
 * 
 * 룰:
 * - 첫 뻑 (FirstFuck): 뻑 점수 × 1
 * - 연 뻑 (SecondFuck): 뻑 점수 × 2
 * - 삼연 뻑 (ThreeFuck): 뻑 점수 × 4
 * - 첫 따닥 (FirstDdadak): 뻑 점수 × 1
 * 
 * 해당 옵션을 가진 플레이어가 다른 모든 플레이어로부터 받음
 */
class CalculateScoreOptionUseCase @Inject constructor() {
    
    /**
     * 점수 옵션 계산 결과
     * @param accounts 각 플레이어가 주고받을 금액 맵 (playerId to account)
     */
    data class ScoreOptionResult(
        val accounts: Map<Long, Int>
    )
    
    /**
     * 점수 옵션 계산
     * 
     * @param gamers 점수 옵션 계산에 참여하는 플레이어들 (광팜 플레이어 제외)
     * @param fuckScore 뻑 기본 점수
     * @return 각 플레이어의 계산 결과
     */
    operator fun invoke(
        gamers: List<Gamer>,
        fuckScore: Int
    ): ScoreOptionResult {
        val accounts = mutableMapOf<Long, Int>()
        
        // 각 플레이어의 점수 옵션을 계산
        gamers.forEach { gamer ->
            gamer.scoreOption.forEach { option ->
                val amount = calculateScoreOptionAmount(option, fuckScore)
                
                // 해당 플레이어가 다른 모든 플레이어로부터 받음
                val otherGamers = gamers - gamer
                
                // 받는 사람
                accounts[gamer.id] = (accounts[gamer.id] ?: 0) + (amount * otherGamers.size)
                
                // 내는 사람들
                otherGamers.forEach { other ->
                    accounts[other.id] = (accounts[other.id] ?: 0) - amount
                }
            }
        }
        
        return ScoreOptionResult(accounts)
    }
    
    /**
     * 점수 옵션별 금액 계산
     * 
     * @param option 점수 옵션
     * @param fuckScore 뻑 기본 점수
     * @return 계산된 금액
     */
    private fun calculateScoreOptionAmount(
        option: ScoreOption,
        fuckScore: Int
    ): Int {
        return when (option) {
            ScoreOption.FirstFuck -> fuckScore
            ScoreOption.SecondFuck -> fuckScore * 2
            ScoreOption.ThreeFuck -> fuckScore * 4
            ScoreOption.FirstDdadak -> fuckScore
        }
    }
}

