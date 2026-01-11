package me.onixdev.check.impl.player.badpackets

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser

class BadPacketT(user: OnixUser) : Check(user, CheckBuilder().setCheckName("BadPacket").setDescription("Action Data").setType("T").build()) {

    override fun onPacketIn(event: PacketReceiveEvent?) {
        if (event == null) return
        if (event.packetType == PacketType.Play.Client.HELD_ITEM_CHANGE) {
            if (player.packetData.using || player.packetData.attacking || player.packetData.swapping) {
                fail("use: ${player.packetData.using} attacked: ${player.packetData.attacking} swapping: ${player.packetData.swapping}")
                if (shouldCancel()) event.isCancelled = true
            }
        }
    }
}