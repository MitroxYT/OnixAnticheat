package me.onixdev.messenger.util.packets.base

import org.json.JSONObject

abstract class OnixPacket(private val id: Int) {
    fun getId() : Int {
        return id;
    }
    protected fun createBasePacket(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        return json
    }
    abstract fun encode() : String
}