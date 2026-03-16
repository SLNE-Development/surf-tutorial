package dev.slne.surf.tutorial.server.survival.util

import dev.slne.surf.surfapi.bukkit.api.util.key
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

private val tutorialKey = key("tutorial_step")

fun Player.getTutorialStep() =
    this.persistentDataContainer.get(tutorialKey, PersistentDataType.INTEGER) ?: 0

fun Player.hasTutorialStep() =
    this.persistentDataContainer.has(tutorialKey, PersistentDataType.INTEGER)

fun Player.setTutorialStep(step: Int) =
    this.persistentDataContainer.set(tutorialKey, PersistentDataType.INTEGER, step)

fun SurfComponentBuilder.appendTutorialPrefix() = append {
    spacer(">> ")
    primary("Tutorial")
    darkSpacer(" | ")
}

fun Player.sendTutorialMessage(message: SurfComponentBuilder.() -> Unit) = this.sendText {
    appendTutorialPrefix()
    spacer("-".repeat(40))

    appendNewline()
    appendTutorialPrefix()

    appendNewline()
    append(SurfComponentBuilder(message))

    appendNewline()
    appendTutorialPrefix()

    appendNewline()
    appendTutorialPrefix()
    spacer("-".repeat(40))
}