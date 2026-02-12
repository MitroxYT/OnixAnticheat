package me.onixdev.check.impl.player.block

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem
import me.onixdev.OnixAnticheat
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser
import me.onixdev.util.net.PlayerUtil
import org.bukkit.Material

class GhostHandC(user: OnixUser) : Check(user, CheckBuilder().setCheckName("GhostHand").setDescription("Stop Player Using NoInteract)").setType("C")) {
    override fun onPacketIn(event: PacketReceiveEvent?) {
        if (event == null) return
        if (event.packetType == PacketType.Play.Client.USE_ITEM) {
            if (OnixAnticheat.INSTANCE.compatibilityManager.isLeafTicking) return
            if (player.bukkitPlayer == null) return
            val use = WrapperPlayClientUseItem(event)
            val itemInHand = player.inventory.getItemInHand(use.hand)
            val result = PlayerUtil.raytrace(player.bukkitPlayer,player.rotation.toDirection(), 2.0, 0.5)
            if (result.second != null && !player.bukkitPlayer.isSneaking) {
                val block = result.second!!
                if (block.type == Material.CRAFTING_TABLE && itemInHand.type == Material.ENDER_PEARL || block.type == Material.FURNACE && itemInHand.type == Material.ENDER_PEARL) {
                    fail("Player Tried to Use Item on Interactable Block")
                    if (shouldCancel()) event.isCancelled = true
                }
            }
        }
    }
}