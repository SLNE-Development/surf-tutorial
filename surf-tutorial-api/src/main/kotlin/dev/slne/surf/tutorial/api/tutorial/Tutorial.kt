package dev.slne.surf.tutorial.api.tutorial

import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.tutorial.api.cinematic.KeyFrame

abstract class Tutorial(val name: String) {
    val keyFrames = mutableObjectSetOf<KeyFrame>()
}