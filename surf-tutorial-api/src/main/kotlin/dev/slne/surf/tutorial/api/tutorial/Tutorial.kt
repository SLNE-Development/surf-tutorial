package dev.slne.surf.tutorial.api.tutorial

import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.tutorial.api.cinematic.KeyFrame
import dev.slne.surf.tutorial.api.surfTutorialApi
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.entity.Player

abstract class Tutorial(val name: String) {
    private val _keyFrames = mutableObjectSetOf<KeyFrame>()

    fun start(player: Player) = surfTutorialApi.startTutorial(player, this)
    fun addKeyFrame(keyFrame: KeyFrame) = _keyFrames.add(keyFrame)
    fun getKeyFrames(): ObjectSet<KeyFrame> = _keyFrames.freeze()
}