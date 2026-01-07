package me.onixdev.events.packet

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.DiggingAction
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import me.onixdev.OnixAnticheat
import me.onixdev.check.impl.player.badpackets.BadPacketA
import me.onixdev.check.impl.player.badpackets.BadPacketB
import me.onixdev.event.impl.PlayerHeldItemChangeEvent
import me.onixdev.util.items.MaterialsUtil
import me.onixdev.util.net.BukkitNms

class PlayerUsingItemStatehandler : PacketListenerAbstract(PacketListenerPriority.LOW) {
    override fun onPacketSend(event: PacketSendEvent) {
        if (event.packetType === PacketType.Play.Server.HELD_ITEM_CHANGE) {
            val user = OnixAnticheat.INSTANCE.playerDatamanager[event.user]
            if (user != null) {
                if (user.inventory.serverRequiestSetHeldItem) {
                    return
                }
                user.setUsingItem(false)
            }
        }
    }

    override fun onPacketReceive(event: PacketReceiveEvent) {
        if (event.packetType === PacketType.Play.Client.USE_ITEM) {
            val useItem = WrapperPlayClientUseItem(event)
            val user = OnixAnticheat.INSTANCE.playerDatamanager.get(event.user)
            if (user != null) {
                val itemStack = user.inventory.getItemInHand(useItem.hand)
                val peStack = SpigotConversionUtil.fromBukkitItemStack(itemStack)
                if (user.inventory.hasCooldown(peStack.type)) return
                val usable = MaterialsUtil.isUsable(itemStack, user.food)
                user.sendTransaction()
                val badPacketB = user.getCheck(BadPacketB::class.java)
                if (badPacketB != null) {
                    if (!badPacketB.validdate() && user.bukkitPlayer != null) {
                        BukkitNms.resetBukkitItemUsage(user.bukkitPlayer)
                        return
                    }
                }
                user.usingHand = useItem.hand
                user.setUsingItem(usable)
            }
        }
        if (event.packetType === PacketType.Play.Client.HELD_ITEM_CHANGE) {
            val useItem = WrapperPlayClientHeldItemChange(event)
            val user = OnixAnticheat.INSTANCE.playerDatamanager.get(event.user)
            if (user != null) {
                if (user.inventory.heldItemSlot == user.inventory.lastHeldItemSlot) {
                    if (user.inventory.serverRequiestSetHeldItem) {
                        if (user.bukkitPlayer != null) {
                            BukkitNms.resetBukkitItemUsage(user.bukkitPlayer)
                            user.inventory.serverRequiestSetHeldItem = false
                        }
                    }
                }
                user.inventory.lastHeldItemSlot = user.inventory.heldItemSlot
                user.inventory.heldItemSlot = useItem.slot
                val playerHeldItemChangeEvent =
                    PlayerHeldItemChangeEvent(user.inventory.heldItemSlot, user.inventory.lastHeldItemSlot)
                user.handleEvent(playerHeldItemChangeEvent)
                if (playerHeldItemChangeEvent.isCancelled) {
                    event.isCancelled = true
                    return
                }
            }
            if (user != null) {
                // Нужно чтобы митиграция слотов не убирало состояние использования
                if (!user.inventory.serverRequiestSetHeldItem) user.setUsingItem(false)
                user.inventory.serverRequiestSetHeldItem = false
            }
        }
        if (event.packetType === PacketType.Play.Client.PLAYER_DIGGING) {
            val digging = WrapperPlayClientPlayerDigging(event)
            if (digging.action == DiggingAction.FINISHED_DIGGING || digging.action == DiggingAction.START_DIGGING || digging.action == DiggingAction.CANCELLED_DIGGING) return
            val user = OnixAnticheat.INSTANCE.playerDatamanager.get(event.user)
            if (user != null) {
                val validate = user.getCheck(BadPacketA::class.java)
                if (validate != null && validate.isValid(digging)) {
                    user.setUsingItem(false)
                }
            }
        }
    }
}