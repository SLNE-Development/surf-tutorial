package dev.slne.surf.tutorial.api.cinematic.keyframe

import dev.slne.surf.tutorial.api.cinematic.KeyFrame
import org.bukkit.Location

data class CinematicStartKeyFrame(override val time: Long, val location: Location) : KeyFrame
