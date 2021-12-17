package zero.friends.domain.usecase

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Optional
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject

class SellingUseCase @Inject constructor(
    private val gamerRepository: GamerRepository
) {
    suspend operator fun invoke(seller: Gamer) {
        gamerRepository.updateOption(seller.id, listOf(Optional.Sell))
    }
}