package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Target
import javax.inject.Inject

/**
 * 동일 이름의 게이머들을 묶어 정산 금액/대상을 합산한다.
 */
class AggregateGamerResultsUseCase @Inject constructor() {

    operator fun invoke(gamers: List<Gamer>): List<Gamer> {
        return gamers
            .groupBy { it.name }
            .map { (name, sameNameGamers) ->
                Gamer(
                    name = name,
                    account = sameNameGamers.sumOf { it.account },
                    calculate = mergeTargets(sameNameGamers)
                )
            }
    }

    private fun mergeTargets(gamers: List<Gamer>): List<Target> {
        val mergedTargets = buildMap<String, Target> {
            gamers.forEach { gamer ->
                gamer.calculate.forEach { target ->
                    val newAccount = (this[target.name]?.account ?: 0) + target.account
                    put(target.name, target.copy(account = newAccount))
                }
            }
        }

        return mergedTargets.values.toList()
    }
}
