package me.onixdev.util.net

import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon

object PacketUtil {
    fun isTransaction(packetType: PacketTypeCommon): Boolean {
        return packetType === PacketType.Play.Client.PONG || packetType === PacketType.Play.Client.WINDOW_CONFIRMATION
    }
}