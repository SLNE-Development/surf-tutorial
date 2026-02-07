package dev.slne.surf.tutorial.api.cinematic.keyframe

import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.tutorial.api.cinematic.KeyFrame
import net.kyori.adventure.title.Title

data class TitleKeyFrame(
    override val time: Long,
    val title: SurfComponentBuilder.() -> Unit,
    val subTitle: SurfComponentBuilder.() -> Unit,
    val times: Title.Times
) : KeyFrame
