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
    Winner(Const.Rule.Winner);
}

enum class SellerOption(override val korean: String) : Option {
    Seller(Const.Rule.Seller),
}

enum class ScoreOption(override val korean: String) : Option {
    FirstFuck(Const.Rule.FirstFuck),
    SecondFuck(Const.Rule.SecondFuck),
    ThreeFuck(Const.Rule.ThreeFuck),
    FirstDdadak(Const.Rule.FirstDdadak);
}

enum class LoserOption(override val korean: String) : Option {
    PeaBak(Const.Rule.PeaBak),
    LightBak(Const.Rule.LightBak),
    MongBak(Const.Rule.MongBak),
    GoBak(Const.Rule.GoBak);
}
