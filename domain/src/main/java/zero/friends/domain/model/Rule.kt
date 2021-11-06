package zero.friends.domain.model

data class Rule(
    val title: String,
    val isEssential: Boolean,
    val script: String,
    val score: Int,
)

//data class DomainRule(
//    val moneyPerScore: Int = 0,//점 당
//    val firstFuck: Int = 0,//첫 뻑
//    val threeFuck: Int = 0,//쓰리 뻑
//    val firstDdadak: Int = 0,//첫 따닥
//    val president: Int = 0,//총통
//    val fiveShine: Int = 0,//5광
//    val sellShine: Int = 0,//광팔기
//    val limit: Int = 0,//상한선
//)