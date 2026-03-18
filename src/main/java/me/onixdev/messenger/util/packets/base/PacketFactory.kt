package me.onixdev.messenger.util.packets.base

import me.onixdev.messenger.util.packets.impl.OnixAlertPacket
import me.onixdev.messenger.util.packets.impl.OnixTrustPacket
import org.json.JSONObject

object PacketFactory {
    fun getPacket(json: JSONObject): OnixPacket {
        return when (json.getInt("id")) {
            0 -> {
                OnixAlertPacket(json)
            }

            1 -> OnixTrustPacket(json)

            else -> null
        }!!
    }

}