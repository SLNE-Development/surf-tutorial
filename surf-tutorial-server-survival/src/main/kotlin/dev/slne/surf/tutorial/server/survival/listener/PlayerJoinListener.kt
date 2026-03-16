package dev.slne.surf.tutorial.server.survival.listener

import dev.slne.surf.tutorial.server.survival.util.appendTutorialPrefix
import dev.slne.surf.tutorial.server.survival.util.getTutorialStep
import dev.slne.surf.tutorial.server.survival.util.sendTutorialMessage
import dev.slne.surf.tutorial.server.survival.util.setTutorialStep
import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object PlayerJoinListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerClientLoadedWorldEvent) {
        val player = event.player

        if (player.getTutorialStep() == 0) {
            player.closeInventory()
            player.sendTutorialMessage {
                appendTutorialPrefix()
                info("Willkommen auf dem Server!")

                appendNewline()
                appendTutorialPrefix()
                white("Starte dein Abenteuer mit einer kurzen Erkundung des Spawns.")

                appendNewline()
                appendTutorialPrefix()
                variableValue("Spreche mit dem Shop Händler.")
            }
            player.setTutorialStep(1)
        }
    }
}