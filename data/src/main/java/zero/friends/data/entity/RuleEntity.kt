package zero.friends.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import zero.friends.domain.model.Rule

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["gameId"], unique = false)]
)
data class RuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rules: List<Rule>,
    val gameId: Long = 0,
) {
    companion object {
        fun RuleEntity.toRule(): List<Rule> = rules
    }
}
