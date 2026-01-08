package me.onixdev.util.net

import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying

object PacketUtil {
    fun isTransaction(packetType: PacketTypeCommon): Boolean {
        return packetType === PacketType.Play.Client.PONG || packetType === PacketType.Play.Client.WINDOW_CONFIRMATION
    }

    fun isTickPacketLegacy(packetType: PacketTypeCommon): Boolean {return  WrapperPlayClientPlayerFlying.isFlying(packetType) }
}