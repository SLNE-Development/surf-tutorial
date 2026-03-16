package dev.slne.surf.tutorial.server.survival

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.tutorial.server.survival.command.survivalTutorialCommand
import dev.slne.surf.tutorial.server.survival.listener.NpcInteractListener
import dev.slne.surf.tutorial.server.survival.listener.PlayerJoinListener
import dev.slne.surf.tutorial.server.survival.listener.ProtectionListener
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
        NpcInteractListener.register()
        ProtectionListener.register()
        PlayerJoinListener.register()

        survivalTutorialCommand()
    }
}