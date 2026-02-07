package dev.slne.surf.tutorial.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.tutorial.api.tutorial.Tutorial

val surfTutorialApi = requiredService<SurfTutorialApi>()

interface SurfTutorialApi {
    fun registerTutorial(tutorial: Tutorial)
}