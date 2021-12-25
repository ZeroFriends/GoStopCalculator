package zero.friends.domain.usecase.option

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.SellerOption
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject

class SellingUseCase @Inject constructor(
    private val gamerRepository: GamerRepository
) {
    suspend operator fun invoke(seller: Gamer) {
        gamerRepository.updateOption(seller.id, listOf(SellerOption.Sell))
        gamerRepository.updateAccount(seller, seller.account)
    }
}