package me.onixdev.util.items

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerHeldItemChange
import me.onixdev.user.OnixUser
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Suppress("UNREACHABLE_CODE", "DEPRECATION")
class PlayerInventory(val user: OnixUser) {
    var heldItemSlot : Int = 0
    var lastHeldItemSlot : Int = 0
    var serverRequiestSetHeldItem : Boolean = false
    fun getItemInHand(hand:InteractionHand) : ItemStack {
        if (user.bukkitPlayer == null) return ItemStack(Material.AIR)
        return if (PacketEvents.getAPI().serverManager.version.isNewerThan(ServerVersion.V_1_9)) when (hand) {
            InteractionHand.MAIN_HAND -> {
                user.bukkitPlayer.inventory.itemInMainHand
            }

            InteractionHand.OFF_HAND -> {
                user.bukkitPlayer.inventory.itemInOffHand
            }
        } else user.bukkitPlayer.inventory.itemInHand
        return ItemStack(Material.AIR)
    }
    fun getItemInMainHand() : ItemStack {
        return getItemInHand(InteractionHand.MAIN_HAND)
    }
    fun getItemInOffHand() : ItemStack {
        return getItemInHand(InteractionHand.OFF_HAND)
    }
    fun swapSlot() {
        var slot = heldItemSlot+1
        if (slot > 8) {
            slot = 0
        }
        val slotChange = WrapperPlayServerHeldItemChange(slot)
        user.user.sendPacket(slotChange)
        serverRequiestSetHeldItem = true;
    }
}