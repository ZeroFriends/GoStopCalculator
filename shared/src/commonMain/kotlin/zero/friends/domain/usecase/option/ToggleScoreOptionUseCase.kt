package zero.friends.domain.usecase.option

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.util.Inject

class ToggleScoreOptionUseCase @Inject constructor(
    private val gamerRepository: GamerRepository,
    private val updateOptionsUseCase: UpdateOptionsUseCase
) {
    private val fucks = listOf(ScoreOption.FirstFuck, ScoreOption.SecondFuck, ScoreOption.ThreeFuck)

    suspend operator fun invoke(gamer: Gamer, option: ScoreOption, checkThreeFuck: (Boolean) -> Unit) {
        val latest = gamerRepository.getGamer(gamer.id)
        if (!latest.scoreOption.contains(option)) {
            checkThreeFuck(option == ScoreOption.ThreeFuck)
            if (fucks.contains(option)) {
                clearFucks(latest, option)
            }
        }

        toggle(latest, option)
    }

    private suspend fun clearFucks(duplicateFucks: Gamer, option: ScoreOption) {
        updateOptionsUseCase(duplicateFucks.id, duplicateFucks.scoreOption - fucks, option::class)
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