package zero.friends.domain.usecase.option

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.repository.GamerRepository
import javax.inject.Inject

class ToggleScoreOptionUseCase @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val updateOptionsUseCase: UpdateOptionsUseCase
) {
    suspend operator fun invoke(gamer: Gamer, option: ScoreOption) {
        val roundGamers = gamerRepository.getRoundGamers(gamer.roundId)
        val duplicated = roundGamers.firstOrNull { it.scoreOption.contains(option) }
        if (duplicated != null && gamer != duplicated) {
            removeDuplicate(duplicated, option)
        }
        toggle(gamer, option)
    }

    private suspend fun removeDuplicate(
        duplicate: Gamer,
        option: ScoreOption
    ) {
        val removedOption = duplicate.scoreOption - option
        updateOptionsUseCase(duplicate.id, removedOption, option::class)
    }

    private suspend fun toggle(gamer: Gamer, option: ScoreOption) {
        val cacheGamer = gamerRepository.getGamer(gamer.id)
        val hasOption = cacheGamer.scoreOption.contains(option)
        val toggledList = if (hasOption) {
            cacheGamer.scoreOption - option
        } else {
            cacheGamer.scoreOption + option
        }
        updateOptionsUseCase(cacheGamer.id, toggledList, option::class)
    }

}