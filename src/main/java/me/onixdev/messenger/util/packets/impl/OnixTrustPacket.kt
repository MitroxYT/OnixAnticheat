package me.onixdev.messenger.util.packets.impl

import me.onixdev.messenger.util.packets.base.OnixPacket
import org.json.JSONObject

class OnixTrustPacket : OnixPacket {
    constructor(alertString: String) : super(1)

    constructor(json: JSONObject) : super(1)

    override fun encode(): String {
        return createBasePacket().toString()
    }
}