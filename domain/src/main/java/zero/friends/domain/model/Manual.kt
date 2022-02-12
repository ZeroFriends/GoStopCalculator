package zero.friends.domain.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Keep
sealed class Manual {
    abstract val title: String
    abstract val script: List<Script>
}

@Keep
@Serializable
data class Script(
    val header: String,
    val body: String
)

@Serializable
@Keep
@SerialName("text")
data class Text(
    override val title: String,
    override val script: List<Script>
) : Manual()

@Serializable
@Keep
@SerialName("image")
data class Image(
    override val title: String,
    override val script: List<Script>,
) : Manual()