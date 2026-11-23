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
        val uniquePlayerIdByName = playerNameById.entries
            .groupBy(keySelector = { it.value }, valueTransform = { it.key })
            .mapNotNull { (name, ids) ->
                ids.singleOrNull()?.let { name to it }
            }
            .toMap()

        return gamers
            .groupBy { gamerKey(it.playerId, it.name) }
            .map { (_, samePlayerGamers) ->
                val first = samePlayerGamers.first()
                Gamer(
                    name = first.name,
                    playerId = first.playerId,
                    account = samePlayerGamers.sumOf(Gamer::account),
                    calculate = mergeTargets(samePlayerGamers, playerNameById, uniquePlayerIdByName)
                )
            }
    }

    private fun mergeTargets(
        gamers: List<Gamer>,
        playerNameById: Map<Long, String>,
        uniquePlayerIdByName: Map<String, Long>,
    ): List<Target> {
        val mergedTargets = buildMap<String, Target> {
            gamers.forEach { gamer ->
                gamer.calculate.forEach { target ->
                    // Fallback to unique name->playerId mapping for legacy Target rows whose playerId stored round-local Gamer.id
                    // so pre-/post-v1.4.1 rounds within the same game merge cleanly without splitting rows.
                    val isKnownPlayerTarget =
                        target.playerId != 0L && playerNameById[target.playerId] == target.name
                    val resolvedPlayerId = if (isKnownPlayerTarget) {
                        target.playerId
                    } else {
                        uniquePlayerIdByName[target.name]
                    }
                    val key = if (resolvedPlayerId != null) {
                        "id:$resolvedPlayerId"
                    } else {
                        "name:${target.name}"
                    }
                    val newAccount = (this[key]?.account ?: 0) + target.account
                    put(key, target.copy(playerId = resolvedPlayerId ?: target.playerId, account = newAccount))
                }
            }
        }

        return mergedTargets.values.toList()
    }

    private fun gamerKey(playerId: Long, name: String): String =
        if (playerId != 0L) "id:$playerId" else "name:$name"
}
