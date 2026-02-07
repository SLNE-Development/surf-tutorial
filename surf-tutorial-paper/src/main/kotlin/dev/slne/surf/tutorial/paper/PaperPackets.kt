package dev.slne.surf.tutorial.paper

import com.github.retrooper.packetevents.util.Vector3d
import com.github.retrooper.packetevents.wrapper.play.server.*
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Location
import org.bukkit.entity.EntityType
import java.util.*

object PaperPackets {
    fun createSpawnHolderPacket(entityId: Int, location: Location) =
        WrapperPlayServerSpawnEntity(
            entityId,
            UUID.randomUUID(),
            SpigotConversionUtil.fromBukkitEntityType(EntityType.MARKER),
            SpigotConversionUtil.fromBukkitLocation(location),
            0f,
            0,
            Vector3d.zero()
        )

    fun createTeleportHolderPacket(entityId: Int, location: Location) =
        WrapperPlayServerEntityTeleport(
            entityId,
            SpigotConversionUtil.fromBukkitLocation(location),
            true
        )

    fun createVelocityHolderPacket(entityId: Int, velocity: Vector3d) =
        WrapperPlayServerEntityVelocity(
            entityId,
            velocity
        )

    fun createDestroyHolderPacket(entityId: Int) =
        WrapperPlayServerDestroyEntities(
            entityId
        )

    fun createCameraPacket(entityId: Int) =
        WrapperPlayServerCamera(
            entityId
        )
}