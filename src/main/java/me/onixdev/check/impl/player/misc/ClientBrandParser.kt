package me.onixdev.check.impl.player.misc

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.configuration.client.WrapperConfigClientPluginMessage
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage
import me.onixdev.check.api.Check
import me.onixdev.check.api.CheckBuilder
import me.onixdev.user.OnixUser
import me.onixdev.util.color.MessageUtil

class ClientBrandParser(user: OnixUser) : Check(user, CheckBuilder.create().setCheckName("NotUsed").setType("A")) {
    private val CHANNEL: String = if (PacketEvents.getAPI().getServerManager().getVersion()
            .isNewerThanOrEquals(ServerVersion.V_1_13)
    ) "minecraft:brand" else "MC|Brand"

    override fun onPacketIn(event: PacketReceiveEvent?) {
        if (event!!.getPacketType() === PacketType.Play.Client.PLUGIN_MESSAGE) {
            val packet = WrapperPlayClientPluginMessage(event)
            handle(packet.channelName, packet.data)
        } else if (event.getPacketType() === PacketType.Configuration.Client.PLUGIN_MESSAGE) {
            val packet = WrapperConfigClientPluginMessage(event)
            handle(packet.channelName, packet.data)
        }
    }

    private fun handle(channel: String, data: ByteArray) {
        if (channel != CHANNEL) return
        var brand = ""
        val minusLength = ByteArray(data.size - 1)
        System.arraycopy(data, 1, minusLength, 0, minusLength.size)
        brand = String(minusLength).replace(" (Velocity)", "")
        brand = MessageUtil.stripColor(brand).toString()
        player.brand = brand
    }
}