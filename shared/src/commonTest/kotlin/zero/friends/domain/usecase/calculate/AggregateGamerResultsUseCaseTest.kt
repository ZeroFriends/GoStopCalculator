package zero.friends.domain.usecase.calculate

import kotlin.test.Test
import kotlin.test.assertEquals
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Target

class AggregateGamerResultsUseCaseTest {

    private val aggregateGamerResultsUseCase = AggregateGamerResultsUseCase()

    @Test
    fun testAggregateSameGamerResultsAndTargets() {
        val gamer1Round1 = Gamer(
            id = 1,
            name = "PlayerA",
            account = 1000,
            calculate = listOf(
                Target(playerId = 2, name = "PlayerB", account = 1000)
            )
        )

        val gamer1Round2 = Gamer(
            id = 2,
            name = "PlayerA",
            account = 2000,
            calculate = listOf(
                Target(playerId = 2, name = "PlayerB", account = 1000),
                Target(playerId = 3, name = "PlayerC", account = 1000)
            )
        )

        val gamer2Round1 = Gamer(
            id = 3,
            name = "PlayerB",
            account = -1000,
            calculate = listOf(
                Target(playerId = 1, name = "PlayerA", account = -1000)
            )
        )

        val result = aggregateGamerResultsUseCase(listOf(gamer1Round1, gamer1Round2, gamer2Round1))

        assertEquals(2, result.size)

        val playerA = result.first { it.name == "PlayerA" }
        assertEquals(3000, playerA.account)
        assertEquals(2, playerA.calculate.size)

        val playerATargetB = playerA.calculate.first { it.name == "PlayerB" }
        assertEquals(2000, playerATargetB.account)

        val playerATargetC = playerA.calculate.first { it.name == "PlayerC" }
        assertEquals(1000, playerATargetC.account)
    }
}
