package zero.friends.gostopcalculator.ui.board.score

interface Phase {
    fun getButtonText(): String
    fun getEnableNext(): Boolean
    fun getMainText(): String
    fun getSubText(): String
}

class Scoring() : Phase {

    override fun getButtonText(): String = "다음 (1/3)"

    override fun getEnableNext(): Boolean = true

    override fun getMainText(): String = "점수기록"

    override fun getSubText(): String = "운이 좋네요!\n해당하는 곳에 체크를 해주세요."

}

class Winner(private val enable: Boolean = false) : Phase {
    override fun getButtonText(): String = "다음 (2/3)"

    override fun getEnableNext(): Boolean = enable

    override fun getMainText(): String = "승자 점수기록"

    override fun getSubText(): String = "이긴 플레이어 선택하고,\n몇점을 내었는지 계산 후 점수를 적어주세요."

}

class Loser(private val enable: Boolean) : Phase {
    override fun getButtonText(): String = "다음 (3/3)"

    override fun getEnableNext(): Boolean = enable

    override fun getMainText(): String = "패자 점수기록"

    override fun getSubText(): String = "게임에서 패배한 플레이어들의 박 여부를 체크해주세요."

}