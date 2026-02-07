package dev.slne.surf.tutorial.api.cinematic.keyframe

import dev.slne.surf.tutorial.api.cinematic.KeyFrame

data class CinematicRotationChangeKeyFrame(
    override val time: Long,
    val yaw: Float,
    val pitch: Float,
    val timeFrame: Long
) : KeyFrame
