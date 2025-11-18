package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.util.Const
import javax.inject.Inject

/**
 * 점수 옵션 계산 UseCase (뻑, 따닥)
 * 
 * 룰:
 * - 첫 뻑 (FirstFuck): 뻑 점수 × 1
 * - 연 뻑 (SecondFuck): 뻑 점수 × 2
 * - 삼연 뻑 (ThreeFuck): 뻑 점수 × 4
 * - 첫 따닥 (FirstDdadak): 점당 × 3 (3점에 해당하는 금액)
 * 
 * 해당 옵션을 가진 플레이어가 다른 모든 플레이어로부터 받음
 */
class CalculateScoreOptionUseCase @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val gamerRepository: GamerRepository
) {
    
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
     * @param gameId 게임 ID (룰 조회용)
     * @param roundId 라운드 ID (플레이어 조회용)
     * @param seller 광팜 플레이어 (없을 경우 null, 점수 옵션 계산에서 제외)
     * @return 각 플레이어의 계산 결과
     */
    suspend operator fun invoke(
        gameId: Long,
        roundId: Long,
        seller: Gamer? = null
    ): ScoreOptionResult {
        val allGamers = gamerRepository.getRoundGamers(roundId)
        // 광팜 플레이어는 점수옵션 계산에서 제외
        val gamers = seller?.let { excludePlayer(allGamers, it.id) } ?: allGamers
        // 룰에서 필요한 값 가져오기
        val rules = ruleRepository.getRules(gameId)
        val fuckScore = rules.firstOrNull { it.name == Const.Rule.Fuck }?.score ?: 0
        val scorePerPoint = rules.firstOrNull { it.name == Const.Rule.Score }?.score ?: 0
        val accounts = mutableMapOf<Long, Int>()
        
        // 각 플레이어의 점수 옵션을 계산
        gamers.forEach { gamer ->
            gamer.scoreOption.forEach { option ->
                val amount = calculateScoreOptionAmount(option, fuckScore, scorePerPoint)
                
                // 해당 플레이어가 다른 모든 플레이어로부터 받음
                val otherGamers = excludePlayer(gamers, gamer.id)
                
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

    private fun excludePlayer(
        gamers: List<Gamer>,
        playerId: Long
    ): List<Gamer> = gamers.filter { it.id != playerId }
    
    /**
     * 점수 옵션별 금액 계산
     * 
     * @param option 점수 옵션
     * @param fuckScore 뻑 기본 점수
     * @param scorePerPoint 점당 금액 (첫따닥 계산용)
     * @return 계산된 금액
     */
    private fun calculateScoreOptionAmount(
        option: ScoreOption,
        fuckScore: Int,
        scorePerPoint: Int
    ): Int {
        return when (option) {
            ScoreOption.FirstFuck -> fuckScore
            ScoreOption.SecondFuck -> fuckScore * 2
            ScoreOption.ThreeFuck -> fuckScore * 4
            ScoreOption.FirstDdadak -> scorePerPoint * 3  // 3점에 해당하는 금액
        }
    }
}
