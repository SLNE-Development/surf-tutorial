package dev.slne.surf.tutorial.api.cinematic.keyframe

import dev.slne.surf.tutorial.api.cinematic.KeyFrame
import org.bukkit.entity.Player

data class PlayerActionKeyFrame(override val time: Long, val action: Player.() -> Unit) : KeyFrame