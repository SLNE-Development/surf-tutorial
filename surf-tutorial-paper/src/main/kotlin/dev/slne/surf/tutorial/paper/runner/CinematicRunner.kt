package dev.slne.surf.tutorial.paper.runner

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.showTitle
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.tutorial.api.cinematic.KeyFrame
import dev.slne.surf.tutorial.api.cinematic.keyframe.*
import dev.slne.surf.tutorial.api.tutorial.Tutorial
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

class CinematicRunner(
    private val player: Player,
    private val tutorial: Tutorial
) {

    private val asyncScheduler: AsyncScheduler = Bukkit.getAsyncScheduler()
    private val plugin = Bukkit.getPluginManager().plugins.first()

    private val frames = tutorial.getKeyFrames().sortedBy { it.time }
    private val prepared = mutableListOf<PreparedFrame>()
    private val timedFrames = mutableListOf<KeyFrame>()

    private var tick = 0L
    private var index = 0

    private lateinit var stand: ArmorStand
    private lateinit var task: ScheduledTask

    fun start() {
        prepare()
        spawn()
        run()
    }

    fun stop() {
        if (::task.isInitialized) task.cancel()
        if (::stand.isInitialized) stand.remove()
        player.spectatorTarget = null
    }

    private fun run() {
        task = asyncScheduler.runAtFixedRate(plugin, { _ ->
            timedFrames
                .filter { it.time == tick }
                .forEach { execute(it) }

            if (index >= prepared.size) {
                player.spectatorTarget = null
                stand.remove()
                task.cancel()
                return@runAtFixedRate
            }

            val frame = prepared[index]

            if (tick < frame.start) {
                tick++
                return@runAtFixedRate
            }

            if (tick <= frame.end) {
                val progress =
                    (tick - frame.start).toDouble() / (frame.end - frame.start).toDouble()
                val t = smooth(progress)
                val target = lerp(frame.from, frame.to, t)

                val velocity = target.toVector().subtract(stand.location.toVector())
                stand.velocity = velocity
            } else {
                stand.teleportAsync(frame.to)
                index++
            }

            tick++
        }, 0, 50, TimeUnit.MILLISECONDS)
    }

    private fun prepare() {
        var start: CinematicStartKeyFrame? = null

        frames.forEach {
            when (it) {
                is CinematicStartKeyFrame -> start = it
                is CinematicStopKeyFrame -> {
                    val s = start ?: return@forEach
                    prepared += PreparedFrame(
                        s.time,
                        it.time,
                        s.location.clone(),
                        it.location.clone()
                    )
                    start = null
                }

                else -> timedFrames += it
            }
        }
    }

    private fun execute(frame: KeyFrame) {
        when (frame) {
            is SoundKeyFrame -> player.playSound(frame.sound)
            is PlayerActionKeyFrame -> frame.action.invoke(player)
            is PositionKeyFrame -> player.teleportAsync(frame.position)
            is TextKeyFrame -> when (frame.typ) {
                TextKeyFrame.Type.CHAT -> {
                    player.sendText {
                        append(frame.message)
                    }
                }

                TextKeyFrame.Type.ACTION_BAR -> {
                    player.sendActionBar {
                        buildText {
                            append(frame.message)
                        }
                    }
                }
            }

            is TitleKeyFrame -> player.showTitle {
                title = SurfComponentBuilder(frame.title)
                subtitle = SurfComponentBuilder(frame.subTitle)
                times {
                    fadeIn(frame.times.fadeIn())
                    stay(frame.times.stay())
                    fadeOut(frame.times.fadeOut())
                }
            }
        }
    }

    private fun spawn() {
        val loc = prepared.first().from
        stand = loc.world.spawn(loc, ArmorStand::class.java).apply {
            isInvisible = true
            isMarker = true
            setGravity(false)
            setAI(false)
        }
        player.spectatorTarget = stand
    }

    private fun smooth(x: Double): Double =
        x * x * (3 - 2 * x)

    private fun lerp(a: Location, b: Location, t: Double): Location =
        Location(
            a.world,
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t,
            a.z + (b.z - a.z) * t,
            (a.yaw + (b.yaw - a.yaw) * t).toFloat(),
            (a.pitch + (b.pitch - a.pitch) * t).toFloat()
        )

    private data class PreparedFrame(
        val start: Long,
        val end: Long,
        val from: Location,
        val to: Location
    )
}
