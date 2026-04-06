package me.onixdev.util.versions

import com.github.retrooper.packetevents.manager.server.ServerVersion
import me.onixdev.OnixAnticheat
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.block.data.Waterlogged
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class InstanceDefault : NmsInstance {
     override fun getAttackCooldown(player: Player): Float {
        return if (OnixAnticheat.INSTANCE.serverVersion
                .isNewerThanOrEquals(ServerVersion.V_1_16_5)
        ) player.attackCooldown else 1f
    }

     override fun isChunkLoaded(world: World, x: Int, z: Int): Boolean {
        return world.isChunkLoaded(x shr 4, z shr 4)
    }

     override fun getType(block: Block): Material {
        return block.type
    }

     override fun getChunkEntities(world: World, x: Int, z: Int): Array<Entity?>? {
        return if (world.isChunkLoaded(x shr 4, z shr 4)) world.getChunkAt(x shr 4, z shr 4)
            .entities else arrayOfNulls(0)
    }

     override fun isWaterLogged(block: Block): Boolean {
        return OnixAnticheat.INSTANCE.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_13_1)
                && (block.blockData is Waterlogged
                && (block as Waterlogged).isWaterlogged)
    }

     override fun isDead(player: Player): Boolean {
        return player.isDead
    }

     override fun isSleeping(player: Player): Boolean {
        return player.isSleeping
    }

     override fun isGliding(player: Player): Boolean {
        return OnixAnticheat.INSTANCE.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9_2) && player.isGliding
    }

     override fun isInsideVehicle(player: Player): Boolean {
        return player.isInsideVehicle
    }

     override fun isRiptiding(player: Player): Boolean {
        return OnixAnticheat.INSTANCE.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_13_2) && player.isRiptiding
    }

     override fun isBlocking(player: Player): Boolean {
        return player.isBlocking
    }

     override fun isSneaking(player: Player): Boolean {
        return player.isSneaking
    }

     @Suppress("DEPRECATION")
     override fun getItemInMainHand(player: Player): ItemStack {
        return player.itemInHand
    }

     override fun getItemInOffHand(player: Player): ItemStack {
        return (if (OnixAnticheat.INSTANCE.serverVersion
                .isNewerThanOrEquals(ServerVersion.V_1_9_2)
        ) player.inventory.itemInOffHand else null)!!
    }

     override fun getWalkSpeed(player: Player): Float {
        return player.walkSpeed
    }

     @Suppress("DEPRECATION")
     override fun getAttributeSpeed(player: Player): Float {
        return if (OnixAnticheat.INSTANCE.serverVersion
                .isNewerThanOrEquals(ServerVersion.V_1_9_2)
        ) player.getAttribute(
            Attribute.valueOf("GENERIC_MOVEMENT_SPEED")
        )!!.value.toFloat() else 0f
    }

     override fun getAllowFlight(player: Player): Boolean {
        return player.allowFlight
    }

     override fun isFlying(player: Player): Boolean {
        return player.isFlying
    }

     override fun getFallDistance(player: Player): Float {
        return player.fallDistance
    }
}