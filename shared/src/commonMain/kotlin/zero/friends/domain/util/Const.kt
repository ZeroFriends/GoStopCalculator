package zero.friends.domain.util

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object Const {
    const val GameId = "gameId"
    const val GameName = "game_name"
    const val Players = "players"

    const val RoundId = "roundId"
    const val FuckEnding = "fuckEnding"

    @Serializable
    enum class Rule(val displayName: String) {
        @SerialName("승자")
        Winner("승자"),

        @SerialName("광팜")
        Seller("광팜"),

        @SerialName("뻑")
        Fuck("뻑"),

        @SerialName("첫 뻑")
        FirstFuck("첫 뻑"),

        @SerialName("연 뻑")
        SecondFuck("연 뻑"),

        @SerialName("삼연 뻑")
        ThreeFuck("삼연 뻑"),

        @SerialName("첫 따닥")
        FirstDdadak("첫 따닥"),

        @SerialName("피 박")
        PeaBak("피 박"),

        @SerialName("광 박")
        LightBak("광 박"),

        @SerialName("멍 박")
        MongBak("멍 박"),

        @SerialName("고 박")
        GoBak("고 박"),

        @SerialName("점당")
        Score("점당"),

        @SerialName("광팔기")
        Sell("광팔기"),
    }
}
