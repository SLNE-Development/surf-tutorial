package dev.slne.surf.tutorial.server.survival.command

import dev.jorel.commandapi.arguments.AsyncPlayerProfileArgument
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.surfapi.bukkit.api.command.executors.anyExecutorSuspend
import dev.slne.surf.surfapi.bukkit.api.command.util.awaitAsyncPlayerProfile
import dev.slne.surf.surfapi.bukkit.api.command.util.idOrThrow
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.tutorial.server.survival.command.argument.PermissionRegistry
import dev.slne.surf.tutorial.server.survival.util.getTutorialStep
import dev.slne.surf.tutorial.server.survival.util.setTutorialStep
import org.bukkit.Bukkit

fun survivalTutorialCommand() = commandTree("survivalTutorial") {
    withPermission(PermissionRegistry.COMMAND_TUTORIAL)

    literalArgument("setState") {
        argument(AsyncPlayerProfileArgument("profile")) {
            integerArgument("step") {
                anyExecutorSuspend { sender, args ->
                    val profile = args.awaitAsyncPlayerProfile("profile")
                    val step: Int by args
                    val player = Bukkit.getPlayer(profile.idOrThrow())

                    if (player == null) {
                        sender.sendText {
                            appendErrorPrefix()
                            error("Der Spieler ist nicht online.")
                        }
                        return@anyExecutorSuspend
                    }

                    player.setTutorialStep(step)

                    sender.sendText {
                        appendSuccessPrefix()
                        success("Der Tutorial-Schritt von ")
                        variableValue(player.name)
                        success(" wurde auf ")
                        variableValue(step.toString())
                        success(" gesetzt.")
                    }
                }
            }
        }
    }

    literalArgument("getState") {
        argument(AsyncPlayerProfileArgument("profile")) {
            anyExecutorSuspend { sender, args ->
                val profile = args.awaitAsyncPlayerProfile("profile")
                val player = Bukkit.getPlayer(profile.idOrThrow())

                if (player == null) {
                    sender.sendText {
                        appendErrorPrefix()
                        error("Der Spieler ist nicht online.")
                    }
                    return@anyExecutorSuspend
                }

                val step = player.getTutorialStep()

                sender.sendText {
                    appendSuccessPrefix()
                    success("Der Tutorial-Schritt von ")
                    variableValue(player.name)
                    success(" ist ")
                    variableValue(step.toString())
                    success(".")
                }
            }
        }
    }

    literalArgument("reset") {
        argument(AsyncPlayerProfileArgument("profile")) {
            anyExecutorSuspend { sender, args ->
                val profile = args.awaitAsyncPlayerProfile("profile")
                val player = Bukkit.getPlayer(profile.idOrThrow())

                if (player == null) {
                    sender.sendText {
                        appendErrorPrefix()
                        error("Der Spieler ist nicht online.")
                    }
                    return@anyExecutorSuspend
                }

                player.setTutorialStep(0)

                sender.sendText {
                    appendSuccessPrefix()
                    success("Der Tutorial-Schritt von ")
                    variableValue(player.name)
                    success(" wurde zurückgesetzt.")
                }
            }
        }
    }
}