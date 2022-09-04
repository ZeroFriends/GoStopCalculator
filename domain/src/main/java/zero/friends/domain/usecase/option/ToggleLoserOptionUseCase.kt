package zero.friends.domain.usecase.option

import zero.friends.domain.model.LoserOption
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject

class ToggleLoserOptionUseCase @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val updateOptionsUseCase: UpdateOptionsUseCase
) {
    suspend operator fun invoke(gamerId: Long, option: LoserOption) {
        val currentGamer = gamerRepository.getGamer(gamerId)
        val roundGamers = gamerRepository.getRoundGamers(currentGamer.roundId)

        val otherGoBakGamer =
            (roundGamers - currentGamer).firstOrNull { it.loserOption.contains(LoserOption.GoBak) }

        if (otherGoBakGamer != null) {//고박은 only 1개
            updateOptionsUseCase(otherGoBakGamer.id, emptyList(), LoserOption.GoBak::class)
        }
        val hasOption = currentGamer.loserOption.contains(option)
        val toggledList = if (hasOption) {
            currentGamer.loserOption - option
        } else {
            currentGamer.loserOption + option
        }
        updateOptionsUseCase(currentGamer.id, toggledList, option::class)
    }

}