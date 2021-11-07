package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import zero.friends.domain.model.Rule

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
    val rules: List<Rule>,
    val gameId: Long = 0,
)