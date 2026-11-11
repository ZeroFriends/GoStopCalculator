package zero.friends.domain.usecase.calculate.integration

import kotlin.test.Test
import kotlin.test.assertEquals
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Target
import zero.friends.domain.usecase.calculate.AggregateGamerResultsUseCase

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

    @Test
    fun testDifferentPlayersWithSameNameAreNotMerged() {
        val gamerA = Gamer(
            id = 1,
            playerId = 10,
            name = "Player",
            account = 1400,
            calculate = listOf(
                Target(playerId = 11, name = "Player", account = 700),
                Target(playerId = 12, name = "Player", account = 700)
            )
        )
        val gamerB = Gamer(id = 2, playerId = 11, name = "Player", account = -700)
        val gamerC = Gamer(id = 3, playerId = 12, name = "Player", account = -700)

        val result = aggregateGamerResultsUseCase(listOf(gamerA, gamerB, gamerC))

        assertEquals(3, result.size)
        val winner = result.first { it.playerId == 10L }
        assertEquals(1400, winner.account)
        assertEquals(2, winner.calculate.size)
        assertEquals(-700, result.first { it.playerId == 11L }.account)
        assertEquals(-700, result.first { it.playerId == 12L }.account)
    }

    @Test
    fun testLegacyTargetsWithRoundLocalIdsAggregateByName() {
        val round1Alice = Gamer(
            id = 101,
            playerId = 10,
            name = "Alice",
            account = 500,
            calculate = listOf(Target(playerId = 102, name = "Bob", account = 500))
        )
        val round1Bob = Gamer(id = 102, playerId = 20, name = "Bob", account = -500)
        // Simulate legacy Target.playerId colliding numerically with another player's Player.id (10)
        val round2Alice = Gamer(
            id = 201,
            playerId = 10,
            name = "Alice",
            account = 300,
            calculate = listOf(Target(playerId = 10, name = "Bob", account = 300))
        )
        val round2Bob = Gamer(id = 202, playerId = 20, name = "Bob", account = -300)

        val result = aggregateGamerResultsUseCase(listOf(round1Alice, round1Bob, round2Alice, round2Bob))

        assertEquals(2, result.size)
        val alice = result.first { it.playerId == 10L }
        assertEquals(800, alice.account)
        assertEquals(1, alice.calculate.size)
        assertEquals("Bob", alice.calculate.single().name)
        assertEquals(800, alice.calculate.single().account)
    }
}
