package dev.slne.surf.tutorial.api.cinematic.keyframe

import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.tutorial.api.cinematic.KeyFrame

data class TextKeyFrame(
    override val time: Long,
    val typ: Type,
    val message: SurfComponentBuilder.() -> Unit
) : KeyFrame {
    enum class Type {
        TITLE, CHAT, ACTION_BAR
    }
}
