package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.util.Const
import javax.inject.Inject

/**
 * 게임 결과 계산 UseCase (통합)
 * 
 * 전체 계산 순서:
 * 1. 광팜 계산 (있을 경우)
 * 2. 패자 계산 (승자가 있을 경우)
 * 3. 점수옵션 계산 (뻑, 따닥)
 * 
 * 각 단계는 독립적인 UseCase로 분리되어 테스트 가능
 */
class CalculateGameResultUseCase @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val gamerRepository: GamerRepository,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val calculateSellScoreUseCase: CalculateSellScoreUseCase,
    private val calculateLoserScoreUseCase: CalculateLoserScoreUseCase,
    private val calculateScoreOptionUseCase: CalculateScoreOptionUseCase
) {
    
    /**
     * 게임 결과 계산 및 저장
     * 
     * @param gameId 게임 ID
     * @param roundId 라운드 ID
     * @param seller 광팜 플레이어 (없을 경우 null)
     * @param winner 승자 (없을 경우 null)
     */
    suspend operator fun invoke(
        gameId: Long,
        roundId: Long,
        seller: Gamer?,
        winner: Gamer?
    ) {
        // 1. 광팜 계산
        if (seller != null) {
            applySellAccount(gameId, roundId, seller)
        }
        
        // 2. 패자 계산 (승자가 있을 경우)
        if (winner != null) {
            applyLoserAccount(gameId, roundId, winner, seller)
        }
        
        // 3. 점수옵션 계산 (뻑, 따닥)
        applyScoreOptionAccount(gameId, roundId, seller)
    }
    
    /**
     * 광팜 계산 및 적용
     */
    private suspend fun applySellAccount(
        gameId: Long,
        roundId: Long,
        seller: Gamer
    ) {
        val sellResult = calculateSellScoreUseCase(
            gameId = gameId,
            roundId = roundId,
            seller = seller
        )
        val allGamers = gamerRepository.getRoundGamers(roundId)
        
        // seller와 각 buyer 간 거래 기록
        val buyers = allGamers.filter { it.id != seller.id }
        if (buyers.isEmpty()) return
        // sellResult에서 각 buyer가 지불하는 금액 계산
        val amountPerBuyer = -(sellResult.accounts[buyers.first().id] ?: 0)  // 음수이므로 부호 반전
        
        buyers.forEach { buyer ->
            updateAccountUseCase(seller, buyer, amountPerBuyer)
            updateAccountUseCase(buyer, seller, -amountPerBuyer)
        }
    }
    
    /**
     * 패자 계산 및 적용
     */
    private suspend fun applyLoserAccount(
        gameId: Long,
        roundId: Long,
        winner: Gamer,
        seller: Gamer?
    ) {
        val loserResult = calculateLoserScoreUseCase(
            gameId = gameId,
            roundId = roundId,
            winner = winner,
            seller = seller
        )
        val allGamers = gamerRepository.getRoundGamers(roundId)
        // 광팜 플레이어를 제외한 패자 목록
        val losers = allGamers.filter { gamer ->
            gamer.id != winner.id && (seller == null || gamer.id != seller.id)
        }
        
        // 거래 기록
        // 고박 케이스와 일반 케이스 구분
        val goBakGamer = losers.firstOrNull { it.loserOption.contains(LoserOption.GoBak) }
        
        if (goBakGamer != null) {
            // 고박 케이스: 고박자와 승자 간 거래
            val goBakAmount = loserResult.accounts[goBakGamer.id] ?: 0  // 이미 음수
            updateAccountUseCase(goBakGamer, winner, goBakAmount)  // 고박자는 음수만큼 추가
            updateAccountUseCase(winner, goBakGamer, -goBakAmount)  // 승자는 양수만큼 추가
        } else {
            // 일반 케이스: 각 패자와 승자 간 거래
            losers.forEach { loser ->
                val loserAmount = loserResult.accounts[loser.id] ?: 0  // 이미 음수
                updateAccountUseCase(loser, winner, loserAmount)  // 패자는 음수만큼 추가
                updateAccountUseCase(winner, loser, -loserAmount)  // 승자는 양수만큼 추가
            }
        }
    }
    
    /**
     * 점수옵션 계산 및 적용
     */
    private suspend fun applyScoreOptionAccount(
        gameId: Long,
        roundId: Long,
        seller: Gamer?
    ) {
        val scoreOptionResult = calculateScoreOptionUseCase(
            gameId = gameId,
            roundId = roundId,
            seller = seller
        )
        val allGamers = gamerRepository.getRoundGamers(roundId)
        // 광팜 플레이어는 점수옵션 계산에서 제외
        val scoreOptionGamers = if (seller != null) {
            allGamers.filter { it.id != seller.id }
        } else {
            allGamers
        }
        
        // 룰에서 필요한 값 가져오기 (target 기록용)
        val rules = ruleRepository.getRules(gameId)
        val fuckScore = rules.firstOrNull { it.name == Const.Rule.Fuck }?.score ?: 0
        val scorePerPoint = rules.firstOrNull { it.name == Const.Rule.Score }?.score ?: 0
        
        // 거래 기록: 각 옵션 소유자와 나머지 플레이어들 간 거래
        scoreOptionGamers.forEach { gamer ->
            val gamerAmount = scoreOptionResult.accounts[gamer.id] ?: 0
            if (gamerAmount != 0) {
                // 이 플레이어의 총 수입/지출을 addAccount로 적용
                gamerRepository.addAccount(gamer, gamerAmount)
                
                // 각 상대방과의 거래 기록
                // 옵션 소유자는 다른 플레이어들로부터 받음
                val others = scoreOptionGamers.filter { it.id != gamer.id }
                gamer.scoreOption.forEach { option ->
                    val amount = when (option) {
                        zero.friends.domain.model.ScoreOption.FirstFuck -> fuckScore
                        zero.friends.domain.model.ScoreOption.SecondFuck -> fuckScore * 2
                        zero.friends.domain.model.ScoreOption.ThreeFuck -> fuckScore * 4
                        zero.friends.domain.model.ScoreOption.FirstDdadak -> scorePerPoint * 3  // 3점에 해당하는 금액
                    }
                    others.forEach { other ->
                        // 옵션 소유자(gamer)가 다른 플레이어(other)로부터 받음
                        gamerRepository.updateTarget(gamer, amount, other)  // gamer가 other로부터 +amount 받음
                        // 다른 플레이어(other)가 옵션 소유자(gamer)에게 줌
                        gamerRepository.updateTarget(other, -amount, gamer)  // other가 gamer에게 -amount 줌
                    }
                }
            }
        }
    }
}
