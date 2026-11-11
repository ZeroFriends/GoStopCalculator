package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Target
import zero.friends.domain.util.Inject

class AggregateGamerResultsUseCase @Inject constructor() {

    operator fun invoke(gamers: List<Gamer>): List<Gamer> {
        val playerNameById = buildMap {
            gamers.forEach { gamer ->
                if (gamer.playerId != 0L) {
                    put(gamer.playerId, gamer.name)
                }
            }
        }
        return gamers
            .groupBy { gamerKey(it.playerId, it.name) }
            .map { (_, samePlayerGamers) ->
                val first = samePlayerGamers.first()
                Gamer(
                    name = first.name,
                    playerId = first.playerId,
                    account = samePlayerGamers.sumOf(Gamer::account),
                    calculate = mergeTargets(samePlayerGamers, playerNameById)
                )
            }
    }

    private fun mergeTargets(gamers: List<Gamer>, playerNameById: Map<Long, String>): List<Target> {
        val mergedTargets = buildMap<String, Target> {
            gamers.forEach { gamer ->
                gamer.calculate.forEach { target ->
                    // Fallback by name for legacy Target rows whose playerId stored round-local Gamer.id.
                    val isKnownPlayerTarget =
                        target.playerId != 0L && playerNameById[target.playerId] == target.name
                    val key = if (isKnownPlayerTarget) {
                        "id:${target.playerId}"
                    } else {
                        "name:${target.name}"
                    }
                    val newAccount = (this[key]?.account ?: 0) + target.account
                    put(key, target.copy(account = newAccount))
                }
            }
        }

        return mergedTargets.values.toList()
    }

    private fun gamerKey(playerId: Long, name: String): String =
        if (playerId != 0L) "id:$playerId" else "name:$name"
}
