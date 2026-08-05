package zero.friends.domain.usecase

import kotlinx.coroutines.flow.firstOrNull
import me.tatarka.inject.annotations.Inject
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RoundRepository

@Inject
class CleanEmptyRoundsUseCase(
    private val gameRepository: GameRepository,
    private val roundRepository: RoundRepository,
    private val gamerRepository: GamerRepository
) {
    suspend operator fun invoke(): Int {
        var deletedCount = 0
        try {
            val games = gameRepository.observeGameList().firstOrNull() ?: emptyList()
            games.forEach { game ->
                val rounds = roundRepository.observeAllRound(game.id).firstOrNull() ?: emptyList()
                rounds.forEach { round ->
                    val gamers = gamerRepository.getRoundGamers(round.id)
                    val shouldDeleteRound = gamers.isEmpty() || gamers.all { gamer ->
                        gamer.score == 0 && gamer.account == 0 && gamer.calculate.isEmpty()
                    }

                    if (shouldDeleteRound) {
                        roundRepository.deleteRound(round.id)
                        deletedCount++
                    }
                }
            }
        } catch (e: Exception) {
            // ignore
        }
        return deletedCount
    }
}
