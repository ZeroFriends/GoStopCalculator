package zero.friends.domain.model

import zero.friends.domain.util.Const

sealed interface Option {
    val korean: String
}

inline fun <reified T : Enum<T>> findOptional(values: String?): List<T> {
    return if (values == null) emptyList()
    else {
        val options = values.split(",")
        options.mapNotNull { option ->
            runCatching { enumValueOf<T>(option.trim()) }.getOrNull()
        }
    }
}

enum class WinnerOption(override val korean: String) : Option {
    Winner(Const.Rule.Winner.displayName);
}

enum class SellerOption(override val korean: String) : Option {
    Seller(Const.Rule.Seller.displayName),
}

enum class ScoreOption(override val korean: String) : Option {
    FirstFuck(Const.Rule.FirstFuck.displayName),
    SecondFuck(Const.Rule.SecondFuck.displayName),
    ThreeFuck(Const.Rule.ThreeFuck.displayName),
    FirstDdadak(Const.Rule.FirstDdadak.displayName);
}

enum class LoserOption(override val korean: String) : Option {
    PeaBak(Const.Rule.PeaBak.displayName),
    LightBak(Const.Rule.LightBak.displayName),
    MongBak(Const.Rule.MongBak.displayName),
    GoBak(Const.Rule.GoBak.displayName);
}
