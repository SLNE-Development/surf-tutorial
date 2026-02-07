package dev.slne.surf.tutorial.api.cinematic.keyframe

import dev.slne.surf.tutorial.api.cinematic.KeyFrame
import org.bukkit.Location

data class CinematicStopKeyFrame(override val time: Long, val location: Location) : KeyFrame
