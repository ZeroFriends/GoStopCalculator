package zero.friends.gostopcalculator.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object History : Route

    sealed interface Precondition : Route {
        @Serializable
        data object Player : Precondition

        @Serializable
        data class Rule(
            val playersJson: String,
            val gameName: String
        ) : Precondition
    }

    sealed interface Board : Route {
        @Serializable
        data class Main(val gameId: Long) : Board

        @Serializable
        data class Prepare(val gameId: Long) : Board

        @Serializable
        data class Score(val gameId: Long, val roundId: Long) : Board

        @Serializable
        data class End(val gameId: Long, val roundId: Long) : Board

        @Serializable
        data class RuleLog(val gameId: Long) : Board

        @Serializable
        data class Detail(val gameId: Long, val roundId: Long) : Board

        @Serializable
        data class Calculate(val gameId: Long) : Board

        @Serializable
        data object Manual : Board
    }
}
