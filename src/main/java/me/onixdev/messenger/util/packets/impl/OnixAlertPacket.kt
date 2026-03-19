package me.onixdev.messenger.util.packets.impl

import me.onixdev.messenger.util.packets.base.OnixPacket
import org.json.JSONObject

class OnixAlertPacket : OnixPacket {
    private var alert: String = ""

    constructor(alertString: String) : super(0) {
        this.alert = alertString
    }

    constructor(json: JSONObject) : super(0) {
        this.alert = json.optString("alert", "faaa")
    }

    fun getAlert(): String {
        return alert
    }

    override fun encode(): String {
        return createBasePacket().put("alert", alert).toString()
    }
}