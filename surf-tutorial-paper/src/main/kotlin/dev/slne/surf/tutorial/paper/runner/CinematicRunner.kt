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
    private val pathPoints = mutableListOf<PathPoint>()
    private val timedFrames = mutableListOf<KeyFrame>()

    private val entityId = Random().nextInt(Int.MAX_VALUE)

    private var tick = 0L
    private var startTick = 0L
    private var endTick = 0L
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
            val movementComplete = tick > endTick
            val allKeyframesExecuted = timedFrames.all { it.time <= tick }
            
            if (movementComplete && allKeyframesExecuted) {
                player.sendPacket(PaperPackets.createDestroyHolderPacket(entityId))
                player.sendPacket(PaperPackets.createCameraPacket(player.entityId))
                task.cancel()
                return@runAtFixedRate
            }

            // Process movement if we're within the cinematic time range
            if (tick >= startTick && tick <= endTick) {
                // Calculate position along the smooth spline path
                val target = calculatePositionAlongPath(tick)

                // Calculate velocity for smooth visual movement
                val velocity = Vector3d(
                    (target.x - lastLocation.x) / 0.05,
                    (target.y - lastLocation.y) / 0.05,
                    (target.z - lastLocation.z) / 0.05
                )

                // Send velocity packet for smooth movement
                player.sendPacket(PaperPackets.createVelocityHolderPacket(entityId, velocity))
                
                // Send teleport packet every tick for accuracy (prevents drift)
                player.sendPacket(PaperPackets.createTeleportHolderPacket(entityId, target))

                lastLocation = target
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

        val sortedPoints = points.sortedBy { it.first }
        
        if (sortedPoints.isEmpty()) return
        
        startTick = sortedPoints.first().first
        endTick = sortedPoints.last().first
        
        // Store path points for smooth interpolation
        sortedPoints.forEach { (time, location) ->
            pathPoints += PathPoint(time, location)
        }
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
        val loc = pathPoints.first().location
        lastLocation = loc.clone()

        player.sendPacket(PaperPackets.createSpawnHolderPacket(entityId, loc))
        player.sendPacket(PaperPackets.createCameraPacket(entityId))
    }

    /**
     * Calculate position along the smooth path using Catmull-Rom spline interpolation.
     * This ensures the camera passes through all waypoints smoothly without stopping.
     */
    private fun calculatePositionAlongPath(currentTick: Long): Location {
        if (pathPoints.isEmpty()) return lastLocation
        if (pathPoints.size == 1) return pathPoints.first().location
        
        // Find which segment we're in
        var segmentIndex = 0
        for (i in 0 until pathPoints.size - 1) {
            if (currentTick >= pathPoints[i].time && currentTick <= pathPoints[i + 1].time) {
                segmentIndex = i
                break
            }
        }
        
        // If we're past the last point, return it
        if (segmentIndex >= pathPoints.size - 1) {
            return pathPoints.last().location
        }
        
        val p1 = pathPoints[segmentIndex]
        val p2 = pathPoints[segmentIndex + 1]
        
        // Calculate local t (0 to 1) within this segment
        val segmentProgress = (currentTick - p1.time).toDouble() / (p2.time - p1.time).toDouble()
        val t = segmentProgress.coerceIn(0.0, 1.0)
        
        // Get control points for Catmull-Rom spline
        val p0 = if (segmentIndex > 0) pathPoints[segmentIndex - 1].location else p1.location
        val p3 = if (segmentIndex + 2 < pathPoints.size) pathPoints[segmentIndex + 2].location else p2.location
        
        // Calculate position using Catmull-Rom spline for smooth path through all points
        return catmullRomSpline(p0, p1.location, p2.location, p3, t)
    }
    
    /**
     * Catmull-Rom spline interpolation for smooth curves through control points.
     * The curve passes through p1 and p2, using p0 and p3 to determine curvature.
     */
    private fun catmullRomSpline(p0: Location, p1: Location, p2: Location, p3: Location, t: Double): Location {
        val t2 = t * t
        val t3 = t2 * t
        
        // Catmull-Rom basis matrix coefficients
        val x = 0.5 * (
            (2.0 * p1.x) +
            (-p0.x + p2.x) * t +
            (2.0 * p0.x - 5.0 * p1.x + 4.0 * p2.x - p3.x) * t2 +
            (-p0.x + 3.0 * p1.x - 3.0 * p2.x + p3.x) * t3
        )
        
        val y = 0.5 * (
            (2.0 * p1.y) +
            (-p0.y + p2.y) * t +
            (2.0 * p0.y - 5.0 * p1.y + 4.0 * p2.y - p3.y) * t2 +
            (-p0.y + 3.0 * p1.y - 3.0 * p2.y + p3.y) * t3
        )
        
        val z = 0.5 * (
            (2.0 * p1.z) +
            (-p0.z + p2.z) * t +
            (2.0 * p0.z - 5.0 * p1.z + 4.0 * p2.z - p3.z) * t2 +
            (-p0.z + 3.0 * p1.z - 3.0 * p2.z + p3.z) * t3
        )
        
        // Interpolate rotation separately with simple lerp for smooth camera rotation
        val yaw = p1.yaw + (p2.yaw - p1.yaw) * t.toFloat()
        val pitch = p1.pitch + (p2.pitch - p1.pitch) * t.toFloat()
        
        return Location(p1.world, x, y, z, yaw, pitch)
    }

    private data class PathPoint(
        val time: Long,
        val location: Location
    )
}
