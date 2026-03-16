package dev.slne.surf.tutorial.server.survival.command.argument

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {
    private const val BASE = "surf.tutorial.survival"

    val COMMAND_TUTORIAL = create("$BASE.command")
}