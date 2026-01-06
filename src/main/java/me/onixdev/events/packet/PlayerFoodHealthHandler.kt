package me.onixdev.events.packet

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth
import me.onixdev.OnixAnticheat

class PlayerFoodHealthHandler: PacketListenerAbstract(PacketListenerPriority.NORMAL) {
    override fun onPacketSend(event: PacketSendEvent?) {
        if (event == null) return
        if (event.packetType == PacketType.Play.Server.UPDATE_HEALTH) {
            val packet = WrapperPlayServerUpdateHealth(event)
            val user = OnixAnticheat.INSTANCE.playerDatamanager.get(event.user)?: return
            user.food = packet.food
        }
    }
}