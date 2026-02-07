package dev.slne.surf.tutorial.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.tutorial.api.SurfTutorialApi
import dev.slne.surf.tutorial.api.tutorial.Tutorial
import dev.slne.surf.tutorial.paper.service.tutorialService
import net.kyori.adventure.util.Services
import org.bukkit.entity.Player

@AutoService(SurfTutorialApi::class)
class SurfTutorialApiImpl : SurfTutorialApi, Services.Fallback {
    override fun registerTutorial(tutorial: Tutorial) {
        tutorialService.registerTutorial(tutorial)
    }

    override fun startTutorial(
        player: Player,
        tutorial: Tutorial
    ) {
        tutorialService.startTutorial(player, tutorial)
    }

    override fun cancelTutorial(player: Player) {
        tutorialService.cancelTutorial(player)
    }
}