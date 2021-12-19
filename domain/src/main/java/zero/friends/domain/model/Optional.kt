package zero.friends.domain.model

enum class Optional(val korean: String) {
    None(""),
    Sell("광팜"),
    Winner("승자"),
    President("총통"),
    FiveShine("5광"),
    FirstFuck("첫 뻑"),
    ThreeFuck("쓰리 뻑"),
    FirstDdadak("첫 따닥"),
    //피박,광박,멍박,고박 lose optional 로 빼야하는가? WinnerOptional, LoserOptional
    ;

    companion object {
        fun findOptional(values: String?): List<Optional> = if (values == null) listOf(None)
        else {
            val options = values.split(",")
            options.map { option ->
                runCatching { enumValueOf<Optional>(option) }.getOrNull() ?: None
            }
        }

    }

}