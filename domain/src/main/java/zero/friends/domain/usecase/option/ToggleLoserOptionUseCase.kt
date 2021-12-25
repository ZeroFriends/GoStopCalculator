package zero.friends.domain.usecase.option

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject

class ToggleLoserOptionUseCase @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val updateOptionsUseCase: UpdateOptionsUseCase
) {
    suspend operator fun invoke(gamer: Gamer, option: LoserOption) {
        val cacheGamer = gamerRepository.getGamer(gamer.id)
        val hasOption = cacheGamer.loserOption.contains(option)
        val toggledList = if (hasOption) {
            cacheGamer.loserOption - option
        } else {
            cacheGamer.loserOption + option
        }
        updateOptionsUseCase(cacheGamer.id, toggledList, option::class)
    }

}