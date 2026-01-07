package me.onixdev.events.packet

import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCooldown
import me.onixdev.util.extend.KotlinExtends.getData

class PlayerServerCooldownHandler : PacketListenerAbstract(PacketListenerPriority.NORMAL) {
    override fun onPacketSend(event: PacketSendEvent?) {
        if (event == null) return
        if (event.packetType == PacketType.Play.Server.SET_COOLDOWN) {
            val cooldown = WrapperPlayServerSetCooldown(event)
            val user = event.user.getData()
            if (user != null) {
                user.inventory.handleCooldownPacket(cooldown)
            }
        }
    }

}