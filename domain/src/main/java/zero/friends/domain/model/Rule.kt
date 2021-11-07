package zero.friends.domain.model

data class Rule(
    val title: String,
    val isEssential: Boolean,
    val script: String,
    var score: Int,
)