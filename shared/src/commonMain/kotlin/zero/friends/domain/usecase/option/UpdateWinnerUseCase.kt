package zero.friends.domain.usecase.option

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.util.Inject

class UpdateWinnerUseCase @Inject constructor(
    private val gamerRepository: GamerRepository
) {
    suspend operator fun invoke(winner: Gamer) {
        gamerRepository.updateOption(winner.id, listOf(WinnerOption.Winner))
        gamerRepository.updateScore(winner, winner.score, winner.go)
    }
}
