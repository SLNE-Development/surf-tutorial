package dev.slne.surf.tutorial.server.survival.listener

import dev.slne.surf.npc.api.event.NpcInteractEvent
import dev.slne.surf.tutorial.server.survival.util.appendTutorialPrefix
import dev.slne.surf.tutorial.server.survival.util.getTutorialStep
import dev.slne.surf.tutorial.server.survival.util.sendTutorialMessage
import dev.slne.surf.tutorial.server.survival.util.setTutorialStep
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

object NpcInteractListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onNpcInteract(event: NpcInteractEvent) {
        val player = event.player
        val npc = event.npc

        if (player.getTutorialStep() != 1) {
            return
        }

        if (npc.uniqueName.startsWith("surf_shop_npc-")) {
            player.closeInventory()
            player.sendTutorialMessage {
                appendTutorialPrefix()
                info("Hey, ich bin der Shop Händler.")

                appendNewline()
                appendTutorialPrefix()
                info("Hier kannst du Items von anderen Spielern ")

                appendNewline()
                appendTutorialPrefix()
                info("kaufen oder deinen eigenen Shop erstellen.")

                appendNewline()
                appendTutorialPrefix()

                appendNewline()
                appendTutorialPrefix()
                variableValue("Verlasse nun den Spawn und suche dein")

                appendNewline()
                appendTutorialPrefix()
                variableValue("neues Zuhause. Erstelle ein Grundstück und")

                appendNewline()
                appendTutorialPrefix()
                variableValue("baue dein erstes Haus.")
            }

            player.setTutorialStep(2)
        }
    }
}