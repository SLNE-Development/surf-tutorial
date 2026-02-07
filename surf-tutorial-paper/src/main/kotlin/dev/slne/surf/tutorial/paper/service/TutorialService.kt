package dev.slne.surf.tutorial.paper.service

import com.jeff_media.morepersistentdatatypes.DataType
import dev.slne.surf.surfapi.bukkit.api.util.key
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.tutorial.api.tutorial.Tutorial
import dev.slne.surf.tutorial.paper.runner.CinematicRunner
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

val tutorialService = TutorialService()

@Suppress("UnstableApiUsage")
class TutorialService {
    private val _tutorials = mutableObjectSetOf<Tutorial>()
    private val activeTutorials = mutableObject2ObjectMapOf<UUID, Tutorial>()
    private val activeRunners = mutableObject2ObjectMapOf<UUID, CinematicRunner>()

    private val lastLocationKey = key("last_location")
    private val lastGameModeKey = key("last_gameMode")

    fun getTutorial(player: Player): Tutorial? = activeTutorials[player.uniqueId]
    fun getTutorial(name: String) = _tutorials.firstOrNull { it.name == name }
    fun getTutorials(): ObjectSet<Tutorial> = _tutorials.freeze()
    fun registerTutorial(tutorial: Tutorial) = _tutorials.add(tutorial)

    fun startTutorial(player: Player, tutorial: Tutorial) {
        if (activeTutorials.containsKey(player.uniqueId)) {
            return
        }

        player.persistentDataContainer.set(lastLocationKey, DataType.LOCATION, player.location)
        player.persistentDataContainer.set(lastGameModeKey, DataType.INTEGER, player.gameMode.value)

        player.gameMode = GameMode.SPECTATOR

        val runner = CinematicRunner(player, tutorial)
        activeTutorials[player.uniqueId] = tutorial
        activeRunners[player.uniqueId] = runner

        runner.start()
    }

    fun finishTutorial(player: Player) {
        activeRunners.remove(player.uniqueId)?.stop()

        val lastLocation = player.persistentDataContainer.get(lastLocationKey, DataType.LOCATION)
        val lastGameMode = player.persistentDataContainer.get(lastGameModeKey, DataType.INTEGER)
            ?.let { GameMode.getByValue(it) }

        if (lastLocation != null) {
            player.teleportAsync(lastLocation)
            player.persistentDataContainer.remove(lastLocationKey)
        }

        if (lastGameMode != null) {
            player.gameMode = lastGameMode
            player.persistentDataContainer.remove(lastGameModeKey)
        }

        activeTutorials.remove(player.uniqueId)
    }

    fun cancelTutorial(player: Player) {
        activeRunners.remove(player.uniqueId)?.stop()
        finishTutorial(player)
    }
}
