package zero.friends.domain.usecase.option

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject

class ToggleScoreOptionUseCase @Inject constructor(
    private val gamerRepository: GamerRepository

) {
    suspend operator fun invoke(gamer: Gamer, option: ScoreOption) {
        val cacheGamer = gamerRepository.getGamer(gamer.id)
        val hasOption = cacheGamer.scoreOption.contains(option)
        val toggledList = if (hasOption) {
            cacheGamer.scoreOption - option
        } else {
            cacheGamer.scoreOption + option
        }
        if (toggledList.isEmpty()) {
            gamerRepository.clearOption(cacheGamer.id, option::class)
        } else {
            gamerRepository.updateOption(cacheGamer.id, toggledList)
        }

    }
}