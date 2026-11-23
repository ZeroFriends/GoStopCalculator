package zero.friends.gostopcalculator.ui.board.score

sealed interface Phase {
    fun getEnableNext(): Boolean

    sealed interface Toggleable : Phase
    sealed interface Editable : Phase
}

data class Selling(val enable: Boolean) : Phase.Editable {
    override fun getEnableNext(): Boolean = enable
}

object Scoring : Phase.Toggleable {
    override fun getEnableNext(): Boolean = true
}

data class Winner(val enable: Boolean = false) : Phase.Editable {
    override fun getEnableNext(): Boolean = enable
}

object Loser : Phase.Toggleable {
    override fun getEnableNext(): Boolean = true
}
