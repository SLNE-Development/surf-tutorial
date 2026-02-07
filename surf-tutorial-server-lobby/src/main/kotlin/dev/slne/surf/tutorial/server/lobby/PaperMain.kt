package dev.slne.surf.tutorial.server.lobby

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.tutorial.api.surfTutorialApi
import dev.slne.surf.tutorial.server.lobby.tutorial.LobbyTutorial
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        surfTutorialApi.registerTutorial(LobbyTutorial)
    }
}