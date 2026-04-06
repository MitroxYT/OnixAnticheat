package me.onixdev.util.versions

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface NmsInstance {
    fun getAttackCooldown(player: Player): Float

    fun isChunkLoaded(world: World, x: Int, z: Int): Boolean

    fun getType(block: Block): Material

    fun getChunkEntities(world: World, x: Int, z: Int): Array<Entity?>?

    fun isWaterLogged(block: Block): Boolean

    fun isDead(player: Player): Boolean

    fun isSleeping(player: Player): Boolean

    fun isGliding(player: Player): Boolean

    fun isInsideVehicle(player: Player): Boolean

    fun isRiptiding(player: Player): Boolean

    fun isBlocking(player: Player): Boolean

    fun isSneaking(player: Player): Boolean

    fun getItemInMainHand(player: Player): ItemStack

    fun getItemInOffHand(player: Player): ItemStack

    fun getWalkSpeed(player: Player): Float

    fun getAttributeSpeed(player: Player): Float

    fun getAllowFlight(player: Player): Boolean

    fun isFlying(player: Player): Boolean

    fun getFallDistance(player: Player): Float
}
