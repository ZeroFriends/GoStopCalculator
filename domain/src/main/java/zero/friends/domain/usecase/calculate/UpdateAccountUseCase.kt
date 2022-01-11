package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject


class UpdateAccountUseCase @Inject constructor(
    private val gamerRepository: GamerRepository
) {
    suspend operator fun invoke(gamer: Gamer, target: Gamer, account: Int) {
        gamerRepository.addAccount(gamer, account)
        gamerRepository.updateTarget(gamer, account, target)
    }
}