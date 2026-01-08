package me.onixdev.user.data

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import dev.onixac.api.user.data.IPlayerClickData
import me.onixdev.user.OnixUser
import me.onixdev.util.net.PacketUtil

class ClickData(private val user: OnixUser) : IPlayerClickData {
    val ticksPerSecond: Int = 20
    private var totalTickPacketsSent = 0
    private val clicks: MutableList<Int?> = ArrayList<Int?>()
    private var clicking = false
    fun handlePacket(event: PacketReceiveEvent) {
        if (PacketUtil.isTickPacketLegacy(event.getPacketType())) {
            totalTickPacketsSent++
        }

        val packetType = event.packetType


        if (PacketUtil.isTickPacketLegacy(packetType)) {
            val iterator = clicks.iterator()

            while (iterator.hasNext() && totalTickPacketsSent - iterator.next()!! > ticksPerSecond) {
                iterator.remove()
            }

            clicking = false
        } else if (packetType === PacketType.Play.Client.ANIMATION) {
            val awkwardSituation: Boolean = false

            if (!awkwardSituation) {
                clicks.add(totalTickPacketsSent)
                clicking = true
            }
        }
    }
    override fun getCPS(): Int {
        return clicks.size
    }

    override fun isClicking(): Boolean {
        return clicking
    }
}