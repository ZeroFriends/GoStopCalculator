package zero.friends.domain.usecase

import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.usecase.gamer.GetRoundGamerUseCase
import javax.inject.Inject

class CalculateGameResultUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val ruleRepository: RuleRepository,
    private val gamerRepository: GamerRepository,
    private val roundGamerUseCase: GetRoundGamerUseCase
) {
    suspend operator fun invoke() {
        //todo 룰에 해당하는 Account를 계산해서 넣어주자.

        val rules = ruleRepository.getRules(requireNotNull(gameRepository.getCurrentGameId()))

        return roundGamerUseCase().forEach { gamer ->
            var totalAccount = 0
            if (gamer.winnerOption?.korean != null) {
                val findScore = rules.first { it.name == "점당" }.score
                totalAccount += findScore * gamer.account
            }
            if (gamer.sellerOption?.korean != null) {
                val findSellScore = rules.first { it.name == "광팔기" }.score
                if (findSellScore != 0) {
                    totalAccount += findSellScore * gamer.account
                }
            }
            totalAccount += gamer.scoreOption.mapNotNull { option ->
                val score = rules.firstOrNull { it.name == option.korean }?.score
                if (score != null && score != 0) {
                    score * gamer.account
                } else null
            }.sumOf { it }
            println("${gamer.name} : $totalAccount")
            gamerRepository.updateAccount(gamer, totalAccount)
        }
    }
}