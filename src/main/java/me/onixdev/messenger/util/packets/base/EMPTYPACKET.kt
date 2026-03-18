package me.onixdev.messenger.util.packets.base

class EMPTYPACKET : OnixPacket(1656) {
    override fun encode(): String {
        return createBasePacket().toString()
    }
}