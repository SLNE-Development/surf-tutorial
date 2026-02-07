package dev.slne.surf.tutorial.paper.runner

import com.github.retrooper.packetevents.util.Vector3d
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.showTitle
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.tutorial.api.cinematic.KeyFrame
import dev.slne.surf.tutorial.api.cinematic.keyframe.*
import dev.slne.surf.tutorial.api.tutorial.Tutorial
import dev.slne.surf.tutorial.paper.PaperPackets
import dev.slne.surf.tutorial.paper.util.sendPacket
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
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

    private val entityId = Random().nextInt(Int.MAX_VALUE)

    private var tick = 0L
    private var index = 0
    private lateinit var lastLocation: Location

    private lateinit var task: ScheduledTask

    fun start() {
        prepare()
        spawn()
        run()
    }

    fun stop() {
        if (::task.isInitialized) task.cancel()
        player.sendPacket(PaperPackets.createDestroyHolderPacket(entityId))
        player.sendPacket(PaperPackets.createCameraPacket(player.entityId))
    }

    private fun run() {
        task = asyncScheduler.runAtFixedRate(plugin, { _ ->
            // Execute all keyframes scheduled for this tick
            timedFrames
                .filter { it.time == tick }
                .forEach { execute(it) }

            // Check if we're done with both movement and all timed keyframes
            val movementComplete = index >= prepared.size
            val allKeyframesExecuted = timedFrames.all { it.time <= tick }
            
            if (movementComplete && allKeyframesExecuted) {
                player.sendPacket(PaperPackets.createDestroyHolderPacket(entityId))
                player.sendPacket(PaperPackets.createCameraPacket(player.entityId))
                task.cancel()
                return@runAtFixedRate
            }

            // Process movement if there are still segments to process
            if (index < prepared.size) {
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

                    val velocity = Vector3d(
                        (target.x - lastLocation.x) / 0.05,
                        (target.y - lastLocation.y) / 0.05,
                        (target.z - lastLocation.z) / 0.05
                    )

                    player.sendPacket(PaperPackets.createVelocityHolderPacket(entityId, velocity))

                    if (tick % 10L == 0L) {
                        player.sendPacket(PaperPackets.createTeleportHolderPacket(entityId, target))
                    }

                    lastLocation = target
                } else {
                    player.sendPacket(PaperPackets.createTeleportHolderPacket(entityId, frame.to))
                    lastLocation = frame.to
                    index++
                }
            }

            tick++
        }, 0, 50, TimeUnit.MILLISECONDS)
    }

    private fun prepare() {
        val points = mutableListOf<Pair<Long, Location>>()

        frames.forEach {
            when (it) {
                is CinematicStartKeyFrame ->
                    points += it.time to it.location.clone()

                is CinematicLocationKeyFrame ->
                    points += it.time to it.location.clone()

                is CinematicStopKeyFrame ->
                    points += it.time to it.location.clone()

                else -> timedFrames += it
            }
        }

        points
            .sortedBy { it.first }
            .zipWithNext { a, b ->
                PreparedFrame(
                    a.first,
                    b.first,
                    a.second,
                    b.second
                )
            }
            .forEach { prepared += it }
    }

    private fun execute(frame: KeyFrame) {
        when (frame) {
            is SoundKeyFrame -> player.playSound(frame.sound, Sound.Emitter.self())
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
        lastLocation = loc.clone()

        player.sendPacket(PaperPackets.createSpawnHolderPacket(entityId, loc))
        player.sendPacket(PaperPackets.createCameraPacket(entityId))
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
