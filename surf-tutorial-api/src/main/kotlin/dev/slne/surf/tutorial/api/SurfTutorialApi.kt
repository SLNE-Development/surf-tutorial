package dev.slne.surf.tutorial.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.tutorial.api.tutorial.Tutorial
import org.bukkit.entity.Player

val surfTutorialApi = requiredService<SurfTutorialApi>()

interface SurfTutorialApi {
    fun registerTutorial(tutorial: Tutorial)
    fun startTutorial(player: Player, tutorial: Tutorial)

    fun cancelTutorial(player: Player)
}