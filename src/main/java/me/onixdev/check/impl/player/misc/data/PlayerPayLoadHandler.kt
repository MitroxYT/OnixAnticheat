package me.onixdev.check.impl.player.misc.data

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign
import dev.onixac.api.check.util.PayloadExploitData
import dev.onixac.api.events.impl.PlayerPayLoadEvent
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser
import me.onixdev.util.world.utils.SingUtil
import java.util.*


class PlayerPayLoadHandler(user: OnixUser) : Check(user, CheckBuilder.create().setCheckName("checks").setType("aaa").build()) {
    private val packetSecret: UUID = UUID.randomUUID()
    private val data: MutableList<PayloadExploitData> = mutableListOf()
    override fun onPacketIn(event: PacketReceiveEvent) {
        if (event.packetType !== PacketType.Play.Client.UPDATE_SIGN) return
        val lines: Array<String?>?

        try {
            lines = WrapperPlayClientUpdateSign(event).textLines
            player.handleEvent(PlayerPayLoadEvent(lines,player))
        } catch (e: Exception) {
            return
        }


    }
    fun register(payload: PayloadExploitData) {
        data.add(payload)
    }
    fun getData() : MutableList<PayloadExploitData> {
        return data;
    }
}