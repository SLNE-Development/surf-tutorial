package dev.slne.surf.tutorial.api.cinematic.keyframe

import dev.slne.surf.tutorial.api.cinematic.KeyFrame
import org.bukkit.Location

data class PositionKeyFrame(override val time: Long, val position: Location) : KeyFrame
