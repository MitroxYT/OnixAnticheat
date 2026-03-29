package me.onixdev.util.net

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation

object PacketUtil {
    fun isTransaction(packetType: PacketTypeCommon): Boolean {
        return packetType === PacketType.Play.Client.PONG || packetType === PacketType.Play.Client.WINDOW_CONFIRMATION
    }
    fun isTransactionOnixBased(event: PacketReceiveEvent) : Boolean {
        if (isTransaction(event.packetType)) {
            if (event.packetType === PacketType.Play.Client.PONG) {
                val wr = WrapperPlayClientPong(event)
                return wr.id > 0
            }
            if (event.packetType === PacketType.Play.Client.WINDOW_CONFIRMATION) {
                val wr = WrapperPlayClientWindowConfirmation(event)
                return wr.isAccepted && wr.actionId > 0
            }
        }
        return false
    }

    fun isTickPacketLegacy(packetType: PacketTypeCommon?): Boolean {
        return WrapperPlayClientPlayerFlying.isFlying(packetType)
    }
}