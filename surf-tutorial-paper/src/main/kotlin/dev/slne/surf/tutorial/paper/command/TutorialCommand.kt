package dev.slne.surf.tutorial.paper.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.tutorial.api.tutorial.Tutorial
import dev.slne.surf.tutorial.paper.command.argument.tutorialArgument

fun tutorialCommand() = commandTree("tutorial") {
    literalArgument("start") {
        tutorialArgument("tutorial") {
            playerExecutor { player, args ->
                val tutorial: Tutorial by args
                tutorial.start(player)

                player.sendText {
                    appendSuccessPrefix()
                    success("Das Tutorial ")
                    variableValue(tutorial.name)
                    success(" wurde gestartet.")
                }
            }
        }
    }
}