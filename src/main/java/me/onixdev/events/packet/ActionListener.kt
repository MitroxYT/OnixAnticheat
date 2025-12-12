package me.onixdev.events.packet

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings
import me.onixdev.OnixAnticheat
import me.onixdev.event.impl.PlayerCloseInventoryEvent
import me.onixdev.event.impl.PlayerPacketClickEvent
import me.onixdev.event.impl.PlayerPickEvent

class ActionListener : PacketListenerAbstract(PacketListenerPriority.HIGHEST) {
    override fun onPacketReceive(event: PacketReceiveEvent) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.packetType) || event.packetType === PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            val user = OnixAnticheat.INSTANCE.playerDatamanager[event.user.uuid]
            user?.brigingContainer?.handlePacket(event)
        }
        if (event.packetType === PacketType.Play.Client.CLICK_WINDOW) {
            val wrapperPlayClientClickWindow = WrapperPlayClientClickWindow(event)
            val user = OnixAnticheat.INSTANCE.playerDatamanager.get(event.user)
            if (user != null) {
                val event1 = PlayerPacketClickEvent(
                    wrapperPlayClientClickWindow.windowId,
                    wrapperPlayClientClickWindow.windowClickType,
                    wrapperPlayClientClickWindow.carriedItemStack
                )
                user.handleEvent(event1)
                if (event1.isCancelled) {
                    event.isCancelled = true
                }
            }
        }
        if (event.packetType === PacketType.Play.Client.CLOSE_WINDOW) {
            val wrapperPlayClientCloseWindow = WrapperPlayClientCloseWindow(event)
            val user = OnixAnticheat.INSTANCE.playerDatamanager.get(event.user)
            if (user != null) {
                val event1 = PlayerCloseInventoryEvent(wrapperPlayClientCloseWindow.windowId)
                user.handleEvent(event1)
                if (event1.isCancelled) {
                    event.isCancelled = true
                }
            }
        }
        if (event.packetType === PacketType.Play.Client.PICK_ITEM) {
            val user = OnixAnticheat.INSTANCE.playerDatamanager.get(event.user)
            if (user != null) {
                val event1 = PlayerPickEvent()
                user.handleEvent(event1)
                if (event1.isCancelled) {
                    event.isCancelled = true
                }
            }
        }
    }
}