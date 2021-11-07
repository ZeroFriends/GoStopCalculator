package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onUpdate = CASCADE
        )
    ]
)
data class RuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleName: String = "",
    val moneyPerScore: Int = 0,//점 당
    val firstFuck: Int = 0,//첫 뻑
    val threeFuck: Int = 0,//쓰리 뻑
    val firstDdadak: Int = 0,//첫 따닥
    val president: Int = 0,//총통
    val fiveShine: Int = 0,//5광
    val sellShine: Int = 0,//광팔기
    val limit: Int = 0,//상한선,
    val gameId: Long = 0,
)