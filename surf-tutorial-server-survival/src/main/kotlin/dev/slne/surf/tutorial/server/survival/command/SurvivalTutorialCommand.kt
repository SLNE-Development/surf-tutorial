package dev.slne.surf.tutorial.server.survival.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.surf.tutorial.server.survival.command.argument.PermissionRegistry

fun survivalTutorialCommand() = commandTree("survivalTutorial") {
    withPermission(PermissionRegistry.COMMAND_TUTORIAL)
}