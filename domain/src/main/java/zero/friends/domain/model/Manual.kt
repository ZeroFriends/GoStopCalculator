package zero.friends.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Manual {
    abstract val title: String
    abstract val script: List<Script>
}

@Serializable
data class Script(
    val header: String,
    val body: String
)

@Serializable
@SerialName("text")
data class Text(
    override val title: String,
    override val script: List<Script>
) : Manual()

@Serializable
@SerialName("image")
data class Image(
    override val title: String,
    override val script: List<Script>,
) : Manual()

@Serializable
@SerialName("full")
data class Full(
    override val title: String,
    override val script: List<Script>,
) : Manual()
