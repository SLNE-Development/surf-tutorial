package dev.slne.surf.tutorial.api.cinematic.keyframe

import dev.slne.surf.tutorial.api.cinematic.KeyFrame
import net.kyori.adventure.sound.Sound

data class SoundKeyFrame(override val time: Long, val sound: Sound) : KeyFrame
