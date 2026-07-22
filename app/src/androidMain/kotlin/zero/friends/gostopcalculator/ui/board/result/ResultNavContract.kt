package zero.friends.gostopcalculator.ui.board.result

enum class ScreenType {
    Calculate, Detail, Result,
    CALCULATE, DETAIL, RESULT
}

object ResultContract {
    const val EXTRA_GAME_ID = "EXTRA_GAME_ID"
    const val EXTRA_ROUND_ID = "EXTRA_ROUND_ID"
    const val EXTRA_SCREEN_TYPE = "EXTRA_SCREEN_TYPE"
}
