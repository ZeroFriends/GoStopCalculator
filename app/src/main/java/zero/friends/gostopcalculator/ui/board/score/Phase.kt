package zero.friends.gostopcalculator.ui.board.score

sealed interface Phase {
    fun getButtonText(): String
    fun getEnableNext(): Boolean
    fun getMainText(): String
    fun getSubText(): String
    fun extraButtonText(): String

    sealed interface Toggleable : Phase
    sealed interface Editable : Phase
}


class Selling(private val enable: Boolean) : Phase.Editable {
    override fun getButtonText(): String = "다음"

    override fun getEnableNext(): Boolean = enable

    override fun getMainText(): String = "광팔기"

    override fun getSubText(): String = "4인 플레이경우 한명이 필수로 광을 팔아야 플레이가 가능합니다. 광을 판 플레이어를 선택해주세요."

    override fun extraButtonText(): String = "광팔 수 있는 패"

}

object Scoring : Phase.Toggleable {

    override fun getButtonText(): String = "다음 (1/3)"

    override fun getEnableNext(): Boolean = true

    override fun getMainText(): String = "점수기록"

    override fun getSubText(): String = "운이 좋네요!\n해당하는 곳에 체크를 해주세요."

    override fun extraButtonText(): String = "고스톱 설명서"
}

class Winner(private val enable: Boolean = false) : Phase.Editable {
    override fun getButtonText(): String = "다음 (2/3)"

    override fun getEnableNext(): Boolean = enable

    override fun getMainText(): String = "승자 점수기록"

    override fun getSubText(): String = "이긴 플레이어 선택하고,\n몇점을 내었는지 계산 후 점수를 적어주세요."

    override fun extraButtonText(): String = "고스톱 설명서"
}

object Loser : Phase.Toggleable {
    override fun getButtonText(): String = "다음 (3/3)"

    override fun getEnableNext(): Boolean = true

    override fun getMainText(): String = "패자 점수기록"

    override fun getSubText(): String = "게임에서 패배한 플레이어들의 박 여부를 체크해주세요."

    override fun extraButtonText(): String = "고스톱 설명서"
}