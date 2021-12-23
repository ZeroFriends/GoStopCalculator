package zero.friends.domain.model

interface Option {
    val korean: String
}

inline fun <reified T : Enum<out T>> findOptional(values: String?): List<T> {
    return if (values == null) emptyList()
    else {
        val options = values.split(",")
        options.mapNotNull { option ->
            runCatching { enumValueOf<T>(option.trim()) }.getOrNull()
        }
    }
}


enum class WinnerOption(override val korean: String) : Option {
    Sell("광팜"),
    Winner("승자");
}

enum class ScoreOption(override val korean: String) : Option {
    President("총통"),
    FiveShine("5광"),
    FirstFuck("첫 뻑"),
    ThreeFuck("쓰리 뻑"),
    FirstDdadak("첫 따닥");
}

enum class LoserOption(override val korean: String) : Option {
    PeaBak("피 박"),
    LightBak("광 박"),
    MongBak("멍 박"),
    GoBack("고 박");
}