package dev.slne.surf.tutorial.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.tutorial.paper.command.tutorialCommand
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        tutorialCommand()
    }
}