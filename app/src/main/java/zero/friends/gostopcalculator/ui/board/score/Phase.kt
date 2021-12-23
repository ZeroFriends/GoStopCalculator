package zero.friends.gostopcalculator.ui.board.score

interface Phase {
    fun getButtonText(): String
    fun getEnableNext(): Boolean
    fun getMainText(): String
    fun getSubText(): String
}

class Scoring() : Phase {

    override fun getButtonText(): String = "다음 (1/3)"

    override fun getEnableNext(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMainText(): String = "점수기록"

    override fun getSubText(): String = "운이 좋네요!\n" +
            "해당하는 곳에 체크를 해주세요."

}

class Winner() : Phase {
    override fun getButtonText(): String = "다음 (2/3)"

    override fun getEnableNext(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMainText(): String {
        TODO("Not yet implemented")
    }

    override fun getSubText(): String {
        TODO("Not yet implemented")
    }

}

class Loser() : Phase {
    override fun getButtonText(): String = "다음 (3/3)"

    override fun getEnableNext(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMainText(): String {
        TODO("Not yet implemented")
    }

    override fun getSubText(): String {
        TODO("Not yet implemented")
    }

}