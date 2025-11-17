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
        // 룰 및 플레이어 정보 로드
        val rules = ruleRepository.getRules(gameId)
        val allGamers = gamerRepository.getRoundGamers(roundId)
        
        // 룰 값 추출
        val sellScorePerLight = rules.firstOrNull { it.name == Const.Rule.Sell }?.score ?: 0
        val scorePerPoint = rules.firstOrNull { it.name == Const.Rule.Score }?.score ?: 0
        val fuckScore = rules.firstOrNull { it.name == Const.Rule.Fuck }?.score ?: 0
        
        // 1. 광팜 계산
        if (seller != null) {
            applySellAccount(seller, allGamers, sellScorePerLight)
        }
        
        // 2. 패자 계산 (승자가 있을 경우)
        if (winner != null) {
            applyLoserAccount(winner, seller, allGamers, scorePerPoint)
        }
        
        // 3. 점수옵션 계산 (뻑, 따닥)
        applyScoreOptionAccount(seller, allGamers, fuckScore)
    }
    
    /**
     * 광팜 계산 및 적용
     */
    private suspend fun applySellAccount(
        seller: Gamer,
        allGamers: List<Gamer>,
        sellScorePerLight: Int
    ) {
        val sellResult = calculateSellScoreUseCase(
            seller = seller,
            allGamers = allGamers,
            sellScorePerLight = sellScorePerLight
        )
        
        // seller와 각 buyer 간 거래 기록
        val buyers = allGamers - seller
        val amountPerBuyer = sellScorePerLight * seller.score
        
        buyers.forEach { buyer ->
            updateAccountUseCase(seller, buyer, amountPerBuyer)
            updateAccountUseCase(buyer, seller, -amountPerBuyer)
        }
    }
    
    /**
     * 패자 계산 및 적용
     */
    private suspend fun applyLoserAccount(
        winner: Gamer,
        seller: Gamer?,
        allGamers: List<Gamer>,
        scorePerPoint: Int
    ) {
        // 광팜 플레이어를 제외한 패자 목록
        val losers = if (seller != null) {
            allGamers - winner - seller
        } else {
            allGamers - winner
        }
        
        val loserResult = calculateLoserScoreUseCase(
            winner = winner,
            losers = losers,
            scorePerPoint = scorePerPoint
        )
        
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
        seller: Gamer?,
        allGamers: List<Gamer>,
        fuckScore: Int
    ) {
        // 광팜 플레이어는 점수옵션 계산에서 제외
        val scoreOptionGamers = seller?.let { allGamers - it } ?: allGamers
        
        val scoreOptionResult = calculateScoreOptionUseCase(
            gamers = scoreOptionGamers,
            fuckScore = fuckScore
        )
        
        // 거래 기록: 각 옵션 소유자와 나머지 플레이어들 간 거래
        scoreOptionGamers.forEach { gamer ->
            val gamerAmount = scoreOptionResult.accounts[gamer.id] ?: 0
            if (gamerAmount != 0) {
                // 이 플레이어의 총 수입/지출을 addAccount로 적용
                gamerRepository.addAccount(gamer, gamerAmount)
                
                // 각 상대방과의 거래 기록
                val others = scoreOptionGamers - gamer
                gamer.scoreOption.forEach { option ->
                    val amount = when (option) {
                        zero.friends.domain.model.ScoreOption.FirstFuck -> fuckScore
                        zero.friends.domain.model.ScoreOption.SecondFuck -> fuckScore * 2
                        zero.friends.domain.model.ScoreOption.ThreeFuck -> fuckScore * 4
                        zero.friends.domain.model.ScoreOption.FirstDdadak -> fuckScore
                    }
                    others.forEach { other ->
                        gamerRepository.updateTarget(gamer, amount, other)
                    }
                }
            }
        }
    }
}
