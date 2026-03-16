package dev.slne.surf.tutorial.server.survival.listener

import dev.slne.surf.protect.paper.event.ProtectionCreateEvent
import dev.slne.surf.tutorial.server.survival.util.appendTutorialPrefix
import dev.slne.surf.tutorial.server.survival.util.getTutorialStep
import dev.slne.surf.tutorial.server.survival.util.sendTutorialMessage
import dev.slne.surf.tutorial.server.survival.util.setTutorialStep
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object ProtectionListener : Listener {
    @EventHandler
    fun onProtect(event: ProtectionCreateEvent) {
        val player = event.player

        if (player.getTutorialStep() != 2) {
            return
        }

        player.sendTutorialMessage {
            appendTutorialPrefix()
            info("Grundstück erstellt!")

            appendNewline()
            appendTutorialPrefix()
            white("Du hast dein erstes Grundstück erstellt und damit ")

            appendNewline()
            appendTutorialPrefix()
            variableValue("das Tutorial abgeschlossen.")

            appendNewline()
            appendTutorialPrefix()
            primary("Viel Spaß auf dem Server!")
        }
        player.setTutorialStep(3)
    }
}