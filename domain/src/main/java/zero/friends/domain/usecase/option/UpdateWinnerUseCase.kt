package zero.friends.domain.usecase.option

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject

class UpdateWinnerUseCase @Inject constructor(
    private val gamerRepository: GamerRepository
) {
    suspend operator fun invoke(winner: Gamer) {
        gamerRepository.updateOption(winner.id, listOf(WinnerOption.Winner))
        gamerRepository.updateAccount(winner, winner.account)
    }
}