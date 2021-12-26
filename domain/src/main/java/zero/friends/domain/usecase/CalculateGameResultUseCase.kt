package zero.friends.domain.usecase

import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.RuleRepository
import javax.inject.Inject

class CalculateGameResultUseCase @Inject constructor(
    private val ruleRepository: RuleRepository
) {
    suspend operator fun invoke(playerResults: List<Gamer>) {
        //todo 룰에 해당하는 Account를 계산해서 넣어주자.
        playerResults.map {

        }
    }
}