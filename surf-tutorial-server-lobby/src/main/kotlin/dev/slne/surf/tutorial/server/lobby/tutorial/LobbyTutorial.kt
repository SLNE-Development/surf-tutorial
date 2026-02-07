package dev.slne.surf.tutorial.server.lobby.tutorial

import dev.slne.surf.surfapi.core.api.messages.adventure.sound
import dev.slne.surf.tutorial.api.cinematic.keyframe.*
import dev.slne.surf.tutorial.api.tutorial.Tutorial
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import java.time.Duration

object LobbyTutorial : Tutorial("lobby_welcome") {
    init {
        addKeyFrame(TextKeyFrame(0, TextKeyFrame.Type.CHAT) {
            appendInfoPrefix()
            info("Willkommen beim Lobby Tutorial Test!")
            appendNewline()
            appendInfoPrefix()
            info("Nun kommt ein Cinematic, das dir die Lobby zeigt...")
        })

        addKeyFrame(
            CinematicStartKeyFrame(
                0,
                Location(Bukkit.getWorlds().first(), 104.0, 154.0, 298.0, -21.4f, 26.1f)
            )
        )

        addKeyFrame(
            CinematicLocationKeyFrame(
                80,
                Location(Bukkit.getWorlds().first(), 130.0, 159.0, 313.0, 90f, 30f)
            )
        )

        addKeyFrame(
            CinematicLocationKeyFrame(
                140,
                Location(Bukkit.getWorlds().first(), 112.0, 154.0, 337.0, 180f, 18f)
            )
        )

        addKeyFrame(
            CinematicStopKeyFrame(
                160,
                Location(Bukkit.getWorlds().first(), 101.0, 148.0, 315.0, 90f, -1f)
            )
        )

        addKeyFrame(
            TitleKeyFrame(
                170, {
                    primary("CASTCRAFTER.DE")
                }, {
                    note("Viel Spaß auf dem Server!")
                }, Title.Times.times(
                    Duration.ofMillis(500),
                    Duration.ofSeconds(2),
                    Duration.ofMillis(500)
                )
            )
        )

        addKeyFrame(SoundKeyFrame(160, sound {
            type(Sound.ENTITY_ENDER_DRAGON_GROWL)
        }))
    }
}